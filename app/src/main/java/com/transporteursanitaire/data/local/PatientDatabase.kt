package com.transporteursanitaire.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.transporteursanitaire.data.model.Patient
import com.transporteursanitaire.data.model.User

@Database(
    entities = [Patient::class, User::class], // Ajout User
    version = 1,
    exportSchema = false
)
abstract class PatientDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun userDao(): UserDao // Ajout de ce getter

    companion object {
        @Volatile
        private var INSTANCE: PatientDatabase? = null

        fun getInstance(context: Context): PatientDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): PatientDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                PatientDatabase::class.java,
                "patient_database"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}