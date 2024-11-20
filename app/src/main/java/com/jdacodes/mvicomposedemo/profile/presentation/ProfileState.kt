package com.jdacodes.mvicomposedemo.profile.presentation

import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User?) : ProfileState()
}