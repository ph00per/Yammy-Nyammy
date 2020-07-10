package com.ph00.domain.usecases

import com.ph00.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SignInViaEmailAndPasswordUseCase(private val authRepository: AuthRepository) {

    suspend fun execute(email: String, password: String): Boolean? =
        withContext(IO) { authRepository.signInViaEmailAndPassword(email, password) }

}