package com.transporteursanitaire.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.transporteursanitaire.data.model.Patient

/**
 * Déclare la base de données Room pour l'entité Patient.
 */
@Database(
    entities = [Patient::class],
    version = 1,
    exportSchema = false
)
abstract class PatientDatabase : RoomDatabase() {

    /** Retourne l’instance du DAO Patient. */
    abstract fun patientDao(): PatientDao

    companion object {
        @Volatile
        private var INSTANCE: PatientDatabase? = null

        /**
         * Renvoie l'instance singleton de PatientDatabase.
         * Le synchronized garantit que la base n'est construite qu'une seule fois.
         */
        fun getInstance(context: Context): PatientDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        /**
         * Construit la base Room avec une migration destructive par défaut.
         * Renommez "patient_database" ou modifiez la stratégie de migration si besoin.
         */
        private fun buildDatabase(context: Context): PatientDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                PatientDatabase::class.java,
                "patient_database"
            )
                // En cas de changement de version sans migration, détruit et recrée la base
                .fallbackToDestructiveMigration()
                .build()
    }
}