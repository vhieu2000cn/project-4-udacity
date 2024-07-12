package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenticationViewModel: ViewModel() {
    private val firebaseUser : FirebaseUserLiveData by lazy {
        FirebaseUserLiveData()
    }
    val authenticationState = firebaseUser.map { user ->
        if (user == null){
            AuthenticationState.UNAUTHENTICATED
        } else {
            AuthenticationState.AUTHENTICATED
        }
    }

}

enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED
}
