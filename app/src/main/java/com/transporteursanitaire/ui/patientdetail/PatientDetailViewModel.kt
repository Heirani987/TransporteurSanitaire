package com.transporteursanitaire.ui.patientdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.transporteursanitaire.repository.UserRepository
import com.transporteursanitaire.data.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel permettant de gérer l'affichage et la mise à jour des informations d'un patient.
 */
class PatientDetailViewModel(application: Application) : AndroidViewModel(application) {

    // Référence au repository pour accéder aux données des patients.
    private val repository: UserRepository = UserRepository.getInstance(application)

    // LiveData pour le nom et le type de traitement du patient.
    val patientName: MutableLiveData<String> = MutableLiveData()
    val patientTypeTraitement: MutableLiveData<String> = MutableLiveData()
    val errorMessage: MutableLiveData<String?> = MutableLiveData()

    // Identifiant du patient en cours.
    private var patientId: Int = 0

    /**
     * Charge les informations du patient dont l'identifiant est passé en paramètre.
     */
    fun loadPatient(patientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val patient: Patient? = repository.getPatientById(patientId)
            if (patient != null) {
                patientName.postValue(patient.name)
                patientTypeTraitement.postValue(patient.typeTraitement ?: "")
                this@PatientDetailViewModel.patientId = patient.id
            } else {
                errorMessage.postValue("Patient non trouvé")
            }
        }
    }

    /**
     * Met à jour les informations du patient dans la base de données.
     * Si le nom est vide, un message d'erreur est envoyé.
     */
    fun updatePatient() {
        if (patientName.value.isNullOrBlank()) {
            errorMessage.value = "Le nom du patient ne peut pas être vide."
            return
        }
        errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            // Création du patient mis à jour.
            val updatedPatient = Patient(
                id = patientId,
                name = patientName.value ?: "",
                typeTraitement = patientTypeTraitement.value
            )
            repository.updatePatient(updatedPatient)
        }
    }
}