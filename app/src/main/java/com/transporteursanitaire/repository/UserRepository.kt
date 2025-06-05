package com.transporteursanitaire.repository

import com.transporteursanitaire.data.local.PatientDatabase
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.transporteursanitaire.data.model.Patient
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.poifs.filesystem.FileMagic
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedInputStream
import java.io.InputStream

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
     * La feuille doit s'appeler "Planning", avec les en-têtes à la ligne 6 (index 5)
     * et les données à partir de la ligne 7 (index 6).
     */
    fun importPatientsFromExcel(filePath: String) {
        Log.d("UserRepository", "Début de l'import du fichier Excel : $filePath")
        val uri = filePath.toUri()
        val docFile = DocumentFile.fromSingleUri(context, uri)
        val fileNameDebug = docFile?.name ?: "Chemin inconnu"
        Log.d("UserRepository", "Nom du fichier : $fileNameDebug")

        val rawInputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (rawInputStream == null) {
            Log.e("UserRepository", "Impossible d'ouvrir le fichier : $filePath")
            return
        }
        val inputStream = BufferedInputStream(rawInputStream)

        try {
            System.setProperty(
                "javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.event.XMLEventFactoryImpl"
            )
            Log.d("UserRepository", "Propriété XMLEventFactory définie avec Aalto impl.")

            Log.d("UserRepository", "Fichier ouvert avec succès, démarrage du parsing")
            val magic = FileMagic.valueOf(inputStream)
            Log.d("UserRepository", "Format du fichier détecté : $magic")
            val workbook = when (magic) {
                FileMagic.OLE2 -> WorkbookFactory.create(inputStream)
                FileMagic.OOXML -> {
                    val opcPackage: OPCPackage = OPCPackage.open(inputStream)
                    WorkbookFactory.create(opcPackage)
                }
                else -> throw IllegalArgumentException("Format non supporté : $magic")
            }

            val sheet: Sheet? = workbook.getSheet("Planning")
            if (sheet == null) {
                Log.e("UserRepository", "Feuille 'Planning' introuvable.")
                workbook.close()
                return
            }

            Log.d("UserRepository", "Nombre total de lignes dans la feuille : ${sheet.lastRowNum + 1}")
            val formatter = DataFormatter()
            val patientList = mutableListOf<Patient>()

            Log.d("UserRepository", "Début du parsing des données (à partir de la ligne 7, index 6)")
            for (rowIndex in 6..sheet.lastRowNum) {
                val row: Row = sheet.getRow(rowIndex) ?: continue
                val name = formatter.formatCellValue(row.getCell(2)).trim().ifEmpty { "Nom Inconnu" }
                val birthDate = formatter.formatCellValue(row.getCell(3)).trim()
                val dep = formatter.formatCellValue(row.getCell(4)).trim()
                val da = formatter.formatCellValue(row.getCell(5)).trim()
                val validiteDaDebut = formatter.formatCellValue(row.getCell(6)).trim()
                val validiteDaFin = formatter.formatCellValue(row.getCell(7)).trim()
                val transportDu = formatter.formatCellValue(row.getCell(8)).trim()
                val transportAu = formatter.formatCellValue(row.getCell(9)).trim()
                val adresseGeographique = formatter.formatCellValue(row.getCell(10)).trim()
                val trajet = formatter.formatCellValue(row.getCell(11)).trim()
                val pointPcArrivee = formatter.formatCellValue(row.getCell(12)).trim()
                val complementAdresse = formatter.formatCellValue(row.getCell(13)).trim()
                val tel = formatter.formatCellValue(row.getCell(14)).trim()
                val typeTraitement = formatter.formatCellValue(row.getCell(21)).trim().ifEmpty { "Type inconnu" }

                Log.d("UserRepository", "Ligne $rowIndex : name=$name, birthDate=$birthDate")
                patientList += Patient(
                    id = 0,
                    name = name,
                    birthDate = birthDate,
                    dep = dep,
                    da = da,
                    validiteDaDebut = validiteDaDebut,
                    validiteDaFin = validiteDaFin,
                    transportDu = transportDu,
                    transportAu = transportAu,
                    adresseGeographique = adresseGeographique,
                    trajet = trajet,
                    pointPcArrivee = pointPcArrivee,
                    complementAdresse = complementAdresse,
                    tel = tel,
                    typeTraitement = typeTraitement
                )
            }

            workbook.close()
            inputStream.close()

            if (patientList.isEmpty()) {
                Log.e("UserRepository", "Aucun patient trouvé dans le fichier importé.")
                return
            }

            Log.d("UserRepository", "Extraction réussie : ${patientList.size} patients extraits. Début de l'insertion en base.")
            Thread {
                try {
                    patientDao.insertPatients(patientList)
                    patientListCache = patientList
                    lastLoadedFileName = docFile?.name ?: filePath
                    lastLoadedFileUri = filePath
                    Log.d("UserRepository", "Import réussi : ${patientList.size} patients insérés en base.")
                } catch (e: Exception) {
                    Log.e("UserRepository", "Erreur lors de l'insertion dans Room : ${e.message}", e)
                }
            }.start()

        } catch (e: Exception) {
            Log.e("UserRepository", "Erreur lors du parsing du fichier Excel : ${e.message}", e)
        }
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
    fun getPatients(): List<Patient> {
        return patientDao.getPatients()
    }
    fun clearDatabase() = patientDao.clearDatabase()
}