package com.melodify.musicapp.core.common

import com.melodify.musicapp.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserProvider @Inject constructor() {
    private var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun getCurrentUser(): User? = currentUser

    fun clear() {
        currentUser = null
    }
}