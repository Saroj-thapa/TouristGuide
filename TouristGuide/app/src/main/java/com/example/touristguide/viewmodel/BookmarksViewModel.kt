package com.example.touristguide.viewmodel

import androidx.lifecycle.ViewModel
import com.example.touristguide.ui.screen.BookmarkedItem
import com.example.touristguide.ui.screen.BookmarkType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookmarksViewModel : ViewModel() {
    private val _bookmarks = MutableStateFlow<List<BookmarkedItem>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkedItem>> = _bookmarks

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        fetchBookmarks()
    }

    private fun fetchBookmarks() {
        userId?.let { uid ->
            dbRef.child(uid).child("bookmarks").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<BookmarkedItem>()
                    for (child in snapshot.children) {
                        val id = child.key ?: ""
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val address = child.child("address").getValue(String::class.java)
                        val details = child.child("details").getValue(String::class.java)
                        val typeStr = child.child("type").getValue(String::class.java) ?: "PLACE"
                        val type = try {
                            BookmarkType.valueOf(typeStr)
                        } catch (e: Exception) {
                            BookmarkType.PLACE
                        }
                        // MIGRATION: If address is missing but details exists, migrate
                        if (address == null && details != null) {
                            child.ref.child("address").setValue(details)
                            child.ref.child("details").removeValue()
                            android.util.Log.d("BookmarksVM", "Migrated bookmark: name=$name, details->$details")
                            list.add(BookmarkedItem(id, type, name, details))
                        } else {
                            list.add(BookmarkedItem(id, type, name, address ?: ""))
                        }
                        android.util.Log.d("BookmarksVM", "Fetched: id=$id, name=$name, address=${address ?: details}, type=$typeStr")
                    }
                    _bookmarks.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }

    fun removeBookmark(bookmark: BookmarkedItem) {
        userId?.let { uid ->
            dbRef.child(uid).child("bookmarks").child(bookmark.id).removeValue()
        }
    }

    fun addBookmark(bookmark: BookmarkedItem) {
        userId?.let { uid ->
            val key = dbRef.child(uid).child("bookmarks").push().key ?: return
            val bookmarkData = mapOf(
                "type" to bookmark.type.name,
                "name" to bookmark.name,
                "address" to bookmark.details
            )
            dbRef.child(uid).child("bookmarks").child(key).setValue(bookmarkData)
        }
    }
}
