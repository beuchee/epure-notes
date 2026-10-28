package com.alain.epure.notes.system

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import java.io.IOException

// Copie des notes dans le dossier de sauvegarde choisi par l'utilisateur (SPEC F-18.4, DEC-0019).
// Ne touche qu'aux fichiers portant le nom d'une note de l'app ; les autres fichiers du dossier sont ignorés.
class BackupFolder(private val resolver: ContentResolver) {

    // Dossier choisi, ou null si la sauvegarde est désactivée.
    var treeUri: Uri? = null

    fun write(id: String, text: String) {
        val tree = treeUri ?: return
        try {
            val document = findOrCreate(tree, fileName(id)) ?: return
            resolver.openOutputStream(document, "wt")?.use { it.write(text.toByteArray(Charsets.UTF_8)) }
        } catch (error: IOException) {
            // Dossier inaccessible : le traitement de ce cas (SPEC F-18.8) viendra avec la restauration.
        }
    }

    fun delete(id: String) {
        val tree = treeUri ?: return
        try {
            findChild(tree, fileName(id))?.let { DocumentsContract.deleteDocument(resolver, it) }
        } catch (error: IOException) {
            // Voir write().
        }
    }

    // Recopie toutes les notes (SPEC F-18.5 : à l'activation de la sauvegarde).
    fun writeAll(notes: Iterable<Pair<String, String>>) {
        notes.forEach { (id, text) -> write(id, text) }
    }

    // Nom affiché du dossier choisi, pour l'écran Réglages (SPEC F-18.3).
    fun folderName(tree: Uri): String? =
        try {
            val rootId = DocumentsContract.getTreeDocumentId(tree)
            val rootUri = DocumentsContract.buildDocumentUriUsingTree(tree, rootId)
            resolver.query(rootUri, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null)
                ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
        } catch (error: SecurityException) {
            // Accès retiré hors de l'app : pas de nom à afficher.
            null
        }

    private fun findOrCreate(tree: Uri, name: String): Uri? {
        findChild(tree, name)?.let { return it }
        val rootId = DocumentsContract.getTreeDocumentId(tree)
        val rootUri = DocumentsContract.buildDocumentUriUsingTree(tree, rootId)
        // « application/octet-stream » : le fournisseur garde le nom tel quel, sans ajouter d'extension.
        return DocumentsContract.createDocument(resolver, rootUri, MIME_TYPE, name)
    }

    private fun findChild(tree: Uri, name: String): Uri? {
        val rootId = DocumentsContract.getTreeDocumentId(tree)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(tree, rootId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )
        resolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getString(1) == name) {
                    val documentId = cursor.getString(0)
                    return DocumentsContract.buildDocumentUriUsingTree(tree, documentId)
                }
            }
        }
        return null
    }

    private fun fileName(id: String) = "$id.md"

    private companion object {
        const val MIME_TYPE = "application/octet-stream"
    }
}
