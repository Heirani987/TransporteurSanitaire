package com.transporteursanitaire.ui.synchronization

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.findNavController
import com.transporteursanitaire.R
import com.transporteursanitaire.repository.UserRepository
import com.transporteursanitaire.databinding.FragmentSynchronizationBinding

class SynchronizationFragment : Fragment() {

    private var _binding: FragmentSynchronizationBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: UserRepository
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    // Passage de String? à Uri? pour que l'affectation dans le callback soit détectée correctement.
    private var currentFileUri: Uri? = null
    private var currentFileName: String? = null

    companion object {
        private const val KEY_FILE_URI = "key_file_uri"
        private const val KEY_FILE_NAME = "key_file_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            // Lors de la restauration, on reconstruit l'URI à partir de sa représentation String, si disponible.
            currentFileUri = it.getString(KEY_FILE_URI)?.let { Uri.parse(it) }
            currentFileName = it.getString(KEY_FILE_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSynchronizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repository = UserRepository.getInstance(requireContext())

        repository.lastLoadedFileName?.let {
            currentFileName = it
            // Reconstruction de l'URI à partir de la sauvegarde dans le repository si possible.
            currentFileUri = repository.lastLoadedFileUri?.let { Uri.parse(it) }
        }

        updateUI()

        filePickerLauncher = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let { selectedUri ->
                requireContext().contentResolver.openInputStream(selectedUri)?.use { stream ->
                    val documentFile = DocumentFile.fromSingleUri(requireContext(), selectedUri)
                    val fileName = documentFile?.name ?: getString(R.string.sync_no_file_selected)

                    Log.d("SynchronizationFragment", "Fichier sélectionné : $fileName")

                    if (fileName.endsWith(".xlsm", ignoreCase = true) ||
                        fileName.endsWith(".xlsx", ignoreCase = true)
                    ) {
                        // Affectation de l'URI sélectionnée
                        currentFileUri = selectedUri
                        currentFileName = fileName

                        // Sauvegarde dans le repository en conservant la représentation en String
                        repository.lastLoadedFileUri = currentFileUri?.toString()
                        repository.lastLoadedFileName = currentFileName

                        updateUI()
                        performImport(currentFileUri!!.toString())
                    } else {
                        showError(getString(R.string.sync_format_non_supporte))
                    }
                } ?: run {
                    Log.e("SynchronizationFragment", "Impossible d'ouvrir le fichier sélectionné.")
                    Toast.makeText(requireContext(), "Erreur lors de l'ouverture du fichier", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.buttonBrowse.setOnClickListener {
            if (currentFileName != null) {
                showConfirmationDialog("changer de source") { launchFilePicker() }
            } else {
                launchFilePicker()
            }
        }

        binding.buttonCreateNew.setOnClickListener {
            if (currentFileName != null) {
                showConfirmationDialog("créer une nouvelle base (changer la source)") { createNewBase() }
            } else {
                createNewBase()
            }
        }

        binding.buttonSync.setOnClickListener {
            // On utilise repository.lastLoadedFileUri pour vérifier l'existence d'une URI.
            val uriString = repository.lastLoadedFileUri
            if (uriString.isNullOrEmpty()) {
                showError(getString(R.string.sync_no_file_selected))
            } else {
                performSync(uriString)
            }
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigate(
                SynchronizationFragmentDirections.actionSynchronizationFragmentToHomeFragment()
            )
        }
    }

    private fun launchFilePicker() {
        Log.d("SynchronizationFragment", "Lancement du file picker.")
        filePickerLauncher.launch(
            arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel.sheet.macroEnabled.12"
            )
        )
    }

    private fun showConfirmationDialog(actionText: String, onConfirmed: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sync_title))
            .setMessage("Une source est déjà définie ($currentFileName). Voulez-vous $actionText ?")
            .setPositiveButton(getString(R.string.sync_update_database)) { _, _ -> onConfirmed() }
            .setNegativeButton(getString(R.string.sync_cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun performImport(uri: String) {
        Log.d("SynchronizationFragment", "Début de l'import pour le fichier : $uri")
        try {
            repository.importPatientsFromExcel(uri)
            repository.lastLoadedFileName = currentFileName
            repository.lastLoadedFileUri = uri
            Toast.makeText(requireContext(), "Base correctement chargée", Toast.LENGTH_SHORT).show()
            binding.buttonSync.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e("SynchronizationFragment", "Erreur lors de l'import du fichier : $uri", e)
            e.printStackTrace()
            showError("Une erreur s'est produite lors de l'import : ${e.localizedMessage}")
        }
    }

    private fun performSync(uri: String) {
        Log.d("SynchronizationFragment", "Début de la synchronisation pour le fichier : $uri")
        try {
            repository.importPatientsFromExcel(uri)
            Toast.makeText(requireContext(), "Synchronisation réalisée avec succès", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SynchronizationFragment", "Erreur lors de la synchronisation du fichier : $uri", e)
            showError("Une erreur s'est produite lors de la synchronisation : ${e.localizedMessage}")
        }
    }

    private fun createNewBase() {
        try {
            repository.clearDatabase()
            currentFileName = "Suivi VSL"
            currentFileUri = null
            repository.lastLoadedFileName = currentFileName
            repository.lastLoadedFileUri = null
            updateUI()
            Toast.makeText(requireContext(), "Base correctement créée", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SynchronizationFragment", "Erreur lors de la création de la base : ${e.message}", e)
            e.printStackTrace()
            showError("Une erreur s'est produite lors de la création de la base : ${e.localizedMessage}")
        }
    }

    private fun updateUI() {
        binding.textViewFileName.text = getString(
            R.string.sync_fichier_source,
            currentFileName ?: getString(R.string.sync_no_file_selected)
        )
        binding.buttonSync.visibility = if (currentFileName != null) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_FILE_URI, currentFileUri?.toString())
        outState.putString(KEY_FILE_NAME, currentFileName)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}