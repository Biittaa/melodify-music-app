package com.melodify.musicapp.data.remote.firestore

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestorePagingHelper {

    companion object {
        suspend fun <T> paginate(
            query: Query,
            limit: Int,
            lastDocument: DocumentSnapshot? = null,
            mapper: (DocumentSnapshot) -> T?
        ): Pair<List<T>, DocumentSnapshot?> {
            var queryBuilder = query
                .limit(limit.toLong())

            if (lastDocument != null) {
                queryBuilder = queryBuilder.startAfter(lastDocument)
            }

            val snapshot = queryBuilder.get().await()
            val documents = snapshot.documents
            val items = documents.mapNotNull { mapper(it) }
            val lastDoc = documents.lastOrNull()

            return Pair(items, lastDoc)
        }
    }
}