package com.jdacodes.mvicomposedemo.profile.presentation

sealed class ProfileUiEffect {
    data class ShowToast(val message: String) : ProfileUiEffect()
    data object Navigate: ProfileUiEffect()
}