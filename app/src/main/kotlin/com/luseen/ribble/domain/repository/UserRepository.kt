package com.luseen.ribble.domain.repository

import com.luseen.ribble.presentation.model.User
import io.reactivex.Flowable

/**
 * Created by Chatikyan on 10.08.2017.
 */
interface UserRepository {

    fun saveUserLoggedIn()

    fun saveUserLoggedOut()

    fun isUserLoggedIn(): Boolean

    fun getUser(): Flowable<User>
}