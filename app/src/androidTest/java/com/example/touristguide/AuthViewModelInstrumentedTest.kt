
package com.example.touristguide

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.touristguide.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthViewModelInstrumentedTest {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    @Before
    fun setup() {
        // Initialize Firebase
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        firebaseAuth = Firebase.auth

        // Use emulator for testing (recommended)
        firebaseAuth.useEmulator("10.0.2.2", 9099)

        // Initialize ViewModel with default dependencies
        authViewModel = AuthViewModel()
    }

    @After
    fun cleanup() {
        firebaseAuth.signOut()
    }

    @Test
    fun testLoginSuccess() = runBlocking {
        // Test credentials (create this user in your Firebase test environment)
        val email = "test@example.com"
        val password = "password123"

        // Test login
        authViewModel.login(email, password)

        // Verify login was successful
        assertNotNull(firebaseAuth.currentUser)
        assertEquals(email, firebaseAuth.currentUser?.email)
    }

    @Test
    fun testLogout() = runBlocking {
        // First login
        authViewModel.login("test@example.com", "password123")
        assertTrue(authViewModel.isUserLoggedIn())

        // Then logout
        authViewModel.logout()

        // Verify logout
        assertNull(firebaseAuth.currentUser)
    }

    @Test
    fun testAnonymousLogin() = runBlocking {
        // Test anonymous login
        authViewModel.loginAnonymously()

        // Verify anonymous user
        assertNotNull(firebaseAuth.currentUser)
        assertTrue(firebaseAuth.currentUser?.isAnonymous ?: false)
        assertEquals("Anonymous", authViewModel.userName.value)
    }
}