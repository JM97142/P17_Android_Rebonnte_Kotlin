package com.openclassrooms.rebonnte

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.ui.recovery.RecoveryViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecoveryViewModelTest {

    private lateinit var viewModel: RecoveryViewModel
    private lateinit var auth: FirebaseAuth
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        auth = mockk(relaxed = true)
        viewModel = RecoveryViewModel(auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email change resets error`() {
        viewModel.onEmailChange("test@example.com")
        assertEquals("test@example.com", viewModel.email.value)
        assertEquals(null, viewModel.emailError.value)
    }

    @Test
    fun `sendResetEmail with empty email sets error`() {
        viewModel.onEmailChange("")
        viewModel.sendResetEmail()
        assertEquals("Email cannot be empty", viewModel.emailError.value)
    }

    @Test
    fun send_reset_email_success_updates_is_success() {
        val mockTask = mockk<Task<Void>>()
        coEvery { auth.sendPasswordResetEmail(any()) } returns mockTask

        every { mockTask.addOnCompleteListener(any()) } answers {
            val listener = arg<com.google.android.gms.tasks.OnCompleteListener<Void>>(0)
            every { mockTask.isSuccessful } returns true
            listener.onComplete(mockTask)
            mockTask
        }

        viewModel.onEmailChange("user@example.com")
        viewModel.sendResetEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isSuccess.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun send_reset_email_failure_sets_email_error() {
        val mockTask = mockk<Task<Void>>()
        coEvery { auth.sendPasswordResetEmail(any()) } returns mockTask

        every { mockTask.addOnCompleteListener(any()) } answers {
            val listener = arg<com.google.android.gms.tasks.OnCompleteListener<Void>>(0)
            every { mockTask.isSuccessful } returns false
            every { mockTask.exception } returns Exception("no user record")
            listener.onComplete(mockTask)
            mockTask
        }

        viewModel.onEmailChange("unknown@example.com")
        viewModel.sendResetEmail()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No account found with this email address.", viewModel.emailError.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `resetDialog resets isSuccess`() {
        viewModel.resetDialog()
        assertEquals(false, viewModel.isSuccess.value)
    }
}