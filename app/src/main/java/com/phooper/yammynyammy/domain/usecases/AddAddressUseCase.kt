package com.phooper.yammynyammy.domain.usecases

import com.phooper.yammynyammy.domain.models.Address
import com.phooper.yammynyammy.domain.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class AddAddressUseCase(
    private val userRepository: UserRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend fun execute(address: Address): Boolean? =
        withContext(IO) {
            getCurrentUserUseCase.execute()?.uid?.let { userUid ->
                userRepository.addAddress(address, userUid)
            }
        }

}