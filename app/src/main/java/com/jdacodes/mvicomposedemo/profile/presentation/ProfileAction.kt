package com.jdacodes.mvicomposedemo.profile.presentation

sealed class ProfileAction {
    object DisplayUserDetails : ProfileAction()
    // ... other actions ...
}