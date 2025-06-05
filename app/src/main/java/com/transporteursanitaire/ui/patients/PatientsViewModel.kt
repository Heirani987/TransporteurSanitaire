package com.transporteursanitaire.ui.patients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.transporteursanitaire.repository.UserRepository
import com.transporteursanitaire.data.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PatientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository = UserRepository.getInstance(application)

    // LiveData contenant la liste des patients pour alimenter l'affichage.
    private val _patients = MutableLiveData<List<Patient>>()
    val patients: LiveData<List<Patient>> = _patients

    // LiveData pour le patient actuellement sélectionné.
    private val _selectedPatient = MutableLiveData<Patient?>()
    val selectedPatient: LiveData<Patient?> = _selectedPatient

    // LiveData dérivé permettant d’accéder directement à l'attribut `dep` du patient sélectionné.
    val selectedPatientDep: LiveData<String?> = _selectedPatient.map { it?.dep }

    // LiveData pour l'état de chargement.
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData pour les messages d'erreur.
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadPatients()
    }

    /**
     * Charge la liste des patients depuis le repository.
     */
    fun loadPatients() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val list = repository.getPatients()
                _patients.postValue(list.ifEmpty { emptyList() })
                _error.postValue(if (list.isEmpty()) "Aucun patient trouvé." else null)
            } catch (e: Exception) {
                _error.postValue("Erreur lors du chargement : ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    /**
     * Supprime tous les patients de la base de données.
     */
    fun clearAllPatients() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.clearDatabase()
                _patients.postValue(emptyList())
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue("Erreur lors de la suppression : ${e.message}")
            }
        }
    }

    /**
     * Définit le patient sélectionné.
     */
    fun setSelectedPatient(patient: Patient?) {
        _selectedPatient.postValue(patient)
    }

    /**
     * Recharge la liste des patients.
     */
    fun reloadPatients() {
        loadPatients()
    }
}