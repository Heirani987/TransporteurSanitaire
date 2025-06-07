package com.transporteursanitaire.ui.users

import android.app.Application
import androidx.lifecycle.*
import com.transporteursanitaire.data.model.User
import com.transporteursanitaire.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository.getInstance(application)

    private val _users = MutableLiveData<List<User>>(emptyList())
    val users: LiveData<List<User>> = _users

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _users.value = userRepository.getAllUsers()
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.insertUser(user)
            loadUsers()
        }
    }

    // Pour test rapide, ajoute un user fictif "Chauffeur X"
    fun addUserAuto() {
        val randomId = java.util.UUID.randomUUID().toString()
        val user = User(
            id = randomId,
            name = "Chauffeur X",
            role = com.transporteursanitaire.data.model.UserRole.CHAUFFEUR
        )
        addUser(user)
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
            loadUsers()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user)
            loadUsers()
        }
    }
}