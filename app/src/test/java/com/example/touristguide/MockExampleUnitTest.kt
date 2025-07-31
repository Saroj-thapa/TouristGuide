package com.example.touristguide

import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Assert.*

class MockExampleUnitTest {
    interface UserRepository {
        fun getUserName(): String
    }

    @Test
    fun testMockUserName() {
        val userRepo = mock(UserRepository::class.java)
        `when`(userRepo.getUserName()).thenReturn("Prajwol")

        assertEquals("Prajwol", userRepo.getUserName())
        verify(userRepo).getUserName()
    }
}