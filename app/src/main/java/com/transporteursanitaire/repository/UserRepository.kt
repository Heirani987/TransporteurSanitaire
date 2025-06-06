package com.transporteursanitaire.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.transporteursanitaire.data.local.PatientDatabase
import com.transporteursanitaire.data.model.Patient
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.poifs.filesystem.FileMagic
import org.apache.poi.ss.usermodel.*
import java.io.*

class UserRepository private constructor(private val context: Context) {

    private val patientDao = PatientDatabase.getInstance(context).patientDao()
    var patientListCache: List<Patient>? = null
    var lastLoadedFileName: String? = null
    var lastLoadedFileUri: String? = null

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(context: Context): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(context.applicationContext).also { instance = it }
            }
    }

    /**
     * Importe la liste des patients depuis un fichier Excel.
     */
    fun importPatientsFromExcel(filePath: String) {
        Log.d("UserRepository", "Début de l'import du fichier Excel : $filePath")
        val uri = filePath.toUri()
        val docFile = DocumentFile.fromSingleUri(context, uri)
        val fileNameDebug = docFile?.name ?: "Chemin inconnu"
        Log.d("UserRepository", "Nom du fichier : $fileNameDebug")

        context.contentResolver.openInputStream(uri)?.use { rawInputStream ->
            val tempFile = createTempFileFromStream(rawInputStream)
            val workbook: Workbook? = try {
                Log.d("UserRepository", "Détection du format du fichier Excel")
                when (FileMagic.valueOf(FileInputStream(tempFile))) {
                    FileMagic.OLE2 -> WorkbookFactory.create(FileInputStream(tempFile))
                    FileMagic.OOXML -> WorkbookFactory.create(FileInputStream(tempFile))
                    else -> throw IllegalArgumentException("Format non supporté")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Erreur lors de l'ouverture du fichier Excel : ${e.message}")
                null
            }

            workbook?.let {
                processSheet(it.getSheet("Planning"))
                it.close()
            }
        }
    }

    /**
     * Crée un fichier temporaire à partir d'un InputStream.
     */
    private fun createTempFileFromStream(inputStream: InputStream): File {
        val tempFile = File.createTempFile("excelTemp", ".xlsx", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }

    /**
     * Traite la feuille Excel et extrait les données des patients.
     */
    private fun processSheet(sheet: Sheet?) {
        if (sheet == null) {
            Log.e("UserRepository", "Feuille 'Planning' introuvable.")
            return
        }

        val formatter = DataFormatter()
        val patientList = mutableListOf<Patient>()

        Log.d("UserRepository", "Début du parsing des données (à partir de la ligne 7, index 6)")
        for (rowIndex in 6..sheet.lastRowNum) {
            val row: Row = sheet.getRow(rowIndex) ?: continue
            patientList.add(parsePatientRow(row, formatter))
        }

        if (patientList.isEmpty()) {
            Log.e("UserRepository", "Aucun patient trouvé dans le fichier importé.")
        } else {
            savePatientsToDatabase(patientList)
        }
    }

    /**
     * Extrait les informations d'un patient à partir d'une ligne Excel.
     */
    private fun parsePatientRow(row: Row, formatter: DataFormatter): Patient {
        return Patient(
            id = 0,
            name = formatter.formatCellValue(row.getCell(2)).trim().ifEmpty { "Nom Inconnu" },
            birthDate = formatter.formatCellValue(row.getCell(3)).trim(),
            dep = formatter.formatCellValue(row.getCell(4)).trim(),
            da = formatter.formatCellValue(row.getCell(5)).trim(),
            validiteDaDebut = formatter.formatCellValue(row.getCell(6)).trim(),
            validiteDaFin = formatter.formatCellValue(row.getCell(7)).trim(),
            transportDu = formatter.formatCellValue(row.getCell(8)).trim(),
            transportAu = formatter.formatCellValue(row.getCell(9)).trim(),
            adresseGeographique = formatter.formatCellValue(row.getCell(10)).trim(),
            trajet = formatter.formatCellValue(row.getCell(11)).trim(),
            pointPcArrivee = formatter.formatCellValue(row.getCell(12)).trim(),
            complementAdresse = formatter.formatCellValue(row.getCell(13)).trim(),
            tel = formatter.formatCellValue(row.getCell(14)).trim(),
            typeTraitement = formatter.formatCellValue(row.getCell(21)).trim().ifEmpty { "Type inconnu" }
        )
    }

    /**
     * Insère les patients extraits en base de données.
     */
    private fun savePatientsToDatabase(patientList: List<Patient>) {
        Thread {
            try {
                patientDao.insertPatients(patientList)
                patientListCache = patientList
                Log.d("UserRepository", "Import réussi : ${patientList.size} patients insérés.")
            } catch (e: Exception) {
                Log.e("UserRepository", "Erreur lors de l'insertion dans Room : ${e.message}", e)
            }
        }.start()
    }

    fun createExcelFileChooserIntent(): Intent =
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.ms-excel"
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel.sheet.macroEnabled.12"
                )
            )
        }

    fun getPatientById(patientId: Int): Patient? = patientDao.getPatientById(patientId)
    fun updatePatient(patient: Patient) = patientDao.updatePatient(patient)
    fun getPatients(): List<Patient> = patientDao.getPatients()
    fun clearDatabase() = patientDao.clearDatabase()
}