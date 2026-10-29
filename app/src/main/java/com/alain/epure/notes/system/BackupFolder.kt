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

    // Appelé quand le dossier choisi a disparu (renommé, supprimé) : SPEC F-18.8.
    var onUnavailable: (() -> Unit)? = null

    fun write(id: String, text: String) = writeFile(fileName(id), text)

    fun delete(id: String) = deleteFile(fileName(id))

    // Copie d'un fichier quelconque de l'app, par exemple `taches.md` (SPEC F-18.4).
    fun writeFile(name: String, text: String) {
        val tree = treeUri ?: return
        try {
            val document = findOrCreate(tree, name)
            if (document == null) {
                reportIfUnavailable(tree)
                return
            }
            resolver.openOutputStream(document, "wt")?.use { it.write(text.toByteArray(Charsets.UTF_8)) }
        } catch (error: IOException) {
            reportIfUnavailable(tree)
        }
    }

    fun deleteFile(name: String) {
        val tree = treeUri ?: return
        try {
            findChild(tree, name)?.let { DocumentsContract.deleteDocument(resolver, it) }
        } catch (error: IOException) {
            reportIfUnavailable(tree)
        }
    }

    // Lit les fichiers `.md` du dossier (nom, texte), pour la restauration (SPEC F-18.7).
    fun readAll(tree: Uri): List<Pair<String, String>> {
        val rootId = DocumentsContract.getTreeDocumentId(tree)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(tree, rootId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )
        val children = mutableListOf<Pair<String, Uri>>()
        resolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(1)
                if (name.endsWith(".md")) {
                    children += name to DocumentsContract.buildDocumentUriUsingTree(tree, cursor.getString(0))
                }
            }
        }
        return children.mapNotNull { (name, documentUri) ->
            try {
                resolver.openInputStream(documentUri)?.bufferedReader(Charsets.UTF_8)?.use { name to it.readText() }
            } catch (error: IOException) {
                null
            }
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
        } catch (error: RuntimeException) {
            // Dossier retiré ou accès révoqué (SecurityException, IllegalArgumentException) : pas de nom.
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

    // Si le dossier n'existe plus, la sauvegarde s'arrête (SPEC F-18.8). Une erreur passagère n'arrête rien.
    private fun reportIfUnavailable(tree: Uri) {
        if (folderName(tree) == null) {
            treeUri = null
            onUnavailable?.invoke()
        }
    }

    private fun fileName(id: String) = "$id.md"

    private companion object {
        const val MIME_TYPE = "application/octet-stream"
    }
}
