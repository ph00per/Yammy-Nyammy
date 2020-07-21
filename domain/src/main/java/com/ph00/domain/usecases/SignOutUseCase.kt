package com.ph00.domain.usecases

import com.ph00.domain.repositories.AuthRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SignOutUseCase@Inject constructor(private val authRepository: AuthRepository) {

    fun execute(): Completable = authRepository.signOut()

}