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
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val details = child.child("address").getValue(String::class.java) ?: "" // Use address as details
                        val typeStr = child.child("type").getValue(String::class.java) ?: "PLACE" // Default to PLACE if missing
                        val type = try {
                            BookmarkType.valueOf(typeStr)
                        } catch (e: Exception) {
                            BookmarkType.PLACE
                        }
                        list.add(BookmarkedItem(type, name, details))
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
            dbRef.child(uid).get().addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val typeStr = child.child("type").getValue(String::class.java)
                    val name = child.child("name").getValue(String::class.java)
                    val details = child.child("details").getValue(String::class.java)
                    if (typeStr == bookmark.type.name && name == bookmark.name && details == bookmark.details) {
                        child.ref.removeValue()
                        break
                    }
                }
            }
        }
    }
} 