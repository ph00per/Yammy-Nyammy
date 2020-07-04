package com.phooper.yammynyammy.domain.usecases

import com.google.firebase.auth.EmailAuthProvider
import com.phooper.yammynyammy.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ReauthenticateUseCase(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) {

    suspend fun execute(currentPassword: String) =
        withContext(IO) {
            getCurrentUserUseCase.execute()
                ?.let { user ->
                    user.email
                        ?.let { email ->
                            EmailAuthProvider.getCredential(email, currentPassword)
                        }?.let { authCredential ->
                            authRepository.reauthenticate(user, authCredential)
                        }
                }
        }
}