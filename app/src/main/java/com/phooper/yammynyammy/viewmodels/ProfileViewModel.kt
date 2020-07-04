package com.phooper.yammynyammy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import com.phooper.yammynyammy.domain.models.User
import com.phooper.yammynyammy.domain.usecases.GetUserDataAsDocumentUseCase
import com.phooper.yammynyammy.domain.usecases.SignOutUseCase
import com.phooper.yammynyammy.utils.Event
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserDataAsDocumentUseCase: GetUserDataAsDocumentUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>(ViewState.LOADING)
    val state: LiveData<ViewState> get() = _state

    private val _event = MutableLiveData<Event<ViewEvent>>()
    val event: LiveData<Event<ViewEvent>> get() = _event

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    init {
        viewModelScope.launch {
            loadUser()
        }
    }

    private suspend fun loadUser() {
        getUserDataAsDocumentUseCase.execute()?.let {
            it.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    _event.postValue(Event(ViewEvent.ERROR))
                    return@addSnapshotListener
                }
                documentSnapshot?.toObject<User>()?.let { user ->
                    _userData.postValue(user)
                    _state.postValue(ViewState.DEFAULT)
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        signOutUseCase.execute()
        _event.postValue(Event(ViewEvent.NAVIGATE_TO_LOGIN_ACTIVITY))
    }

    enum class ViewState {
        LOADING,
        DEFAULT
    }

    enum class ViewEvent {
        ERROR,
        NAVIGATE_TO_LOGIN_ACTIVITY
    }
}