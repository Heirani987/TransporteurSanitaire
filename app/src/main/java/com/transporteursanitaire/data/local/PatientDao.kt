package com.transporteursanitaire.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.transporteursanitaire.data.model.Patient

/**
 * Interface DAO pour gérer les opérations sur les patients via Room.
 */
@Dao
interface PatientDao {

    /**
     * Récupère tous les patients stockés dans la base de données.
     */
    @Query("SELECT * FROM patients")
    fun getAllPatients(): List<Patient>

    /**
     * Récupère tous les patients stockés dans la base de données.
     * Cela sert comme alias pour getAllPatients() afin de répondre aux besoins du Repository.
     */
    @Query("SELECT * FROM patients")
    fun getPatients(): List<Patient>

    /**
     * Insère ou met à jour une liste de patients dans la base.
     * En cas de conflit (même id), l'enregistrement est remplacé.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPatients(patients: List<Patient>)

    /**
     * Met à jour les informations d'un patient dans la base.
     * @param patient L'objet Patient contenant les nouvelles valeurs.
     */
    @Update
    fun updatePatient(patient: Patient)

    /**
     * Récupère un patient en fonction de son identifiant unique.
     * @param patientId L'identifiant du patient à récupérer.
     * @return Un objet Patient correspondant à l'id ou null si inexistant.
     */
    @Query("SELECT * FROM patients WHERE id = :patientId LIMIT 1")
    fun getPatientById(patientId: Int): Patient?

    /**
     * Efface toutes les données de la table des patients.
     */
    @Query("DELETE FROM patients")
    fun clearDatabase()
}