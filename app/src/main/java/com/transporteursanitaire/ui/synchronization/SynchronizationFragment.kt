package com.transporteursanitaire.ui.synchronization

import android.content.Intent
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

    private var currentFileUri: String? = null
    private var currentFileName: String? = null

    companion object {
        private const val KEY_FILE_URI = "key_file_uri"
        private const val KEY_FILE_NAME = "key_file_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restaurer l'état des variables sauvegardées
        savedInstanceState?.let {
            currentFileUri = it.getString(KEY_FILE_URI)
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

        // Restaurer l'état depuis le repository si disponible
        repository.lastLoadedFileName?.let {
            currentFileName = it
            currentFileUri = repository.lastLoadedFileUri
        }

        if (currentFileName != null) {
            binding.textViewFileName.text = "Fichier source : $currentFileName"
            binding.buttonSync.visibility = View.VISIBLE
        } else {
            binding.textViewFileName.text = getString(R.string.sync_no_file_selected)
            binding.buttonSync.visibility = View.GONE
        }

        binding.textViewTitle.text = getString(R.string.sync_title)
        binding.buttonBrowse.text = getString(R.string.sync_browse)
        binding.buttonCreateNew.text = getString(R.string.sync_create_new_database)
        binding.buttonCancel.text = getString(R.string.sync_cancel)

        // Enregistre le launcher pour le sélecteur de document
        filePickerLauncher = registerForActivityResult(OpenDocument()) { uri: Uri? ->
            uri?.let {
                val documentFile = DocumentFile.fromSingleUri(requireContext(), it)
                val fileName = documentFile?.name ?: getString(R.string.sync_no_file_selected)
                Log.d("SynchronizationFragment", "Fichier sélectionné (avant vérification) : $fileName")
                if (fileName.endsWith(".xlsm", ignoreCase = true) ||
                    fileName.endsWith(".xlsx", ignoreCase = true)
                ) {
                    currentFileUri = it.toString()
                    currentFileName = fileName
                    binding.textViewFileName.text = "Fichier source : $fileName"
                    performImport(it.toString())
                } else {
                    binding.textViewError.text = getString(R.string.sync_format_non_supporte)
                    binding.textViewError.visibility = View.VISIBLE
                }
                Log.d("SynchronizationFragment", "Fichier sélectionné : $fileName")
            }
        }

        // Bouton pour sélectionner un fichier Excel
        binding.buttonBrowse.setOnClickListener {
            if (currentFileName != null) {
                showConfirmationDialog("changer de source", onConfirmed = { launchFilePicker() })
            } else {
                launchFilePicker()
            }
        }

        // Bouton pour créer une nouvelle base
        binding.buttonCreateNew.setOnClickListener {
            if (currentFileName != null) {
                showConfirmationDialog("créer une nouvelle base (changer la source)", onConfirmed = { createNewBase() })
            } else {
                createNewBase()
            }
        }

        // Bouton Sync pour mettre à jour la base
        binding.buttonSync.setOnClickListener {
            if (currentFileUri.isNullOrEmpty()) {
                binding.textViewError.text = getString(R.string.sync_no_file_selected)
                binding.textViewError.visibility = View.VISIBLE
            } else {
                performSync(currentFileUri!!)
            }
        }

        // Bouton Retour pour revenir à l'écran Home
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
            Log.d("SynchronizationFragment", "Import terminé pour le fichier : $uri")
            repository.lastLoadedFileName = currentFileName
            repository.lastLoadedFileUri = currentFileUri
            Toast.makeText(requireContext(), "Base correctement chargée", Toast.LENGTH_SHORT).show()
            binding.buttonSync.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e("SynchronizationFragment", "Erreur lors de l'import du fichier : $uri", e)
            Toast.makeText(requireContext(), "Erreur lors du chargement", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSync(uri: String) {
        Log.d("SynchronizationFragment", "Début de la synchronisation pour le fichier : $uri")
        try {
            repository.importPatientsFromExcel(uri)
            Log.d("SynchronizationFragment", "Synchronisation réussie pour le fichier : $uri")
            Toast.makeText(requireContext(), "Synchronisation réalisée avec succès", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("SynchronizationFragment", "Erreur lors de la synchronisation du fichier : $uri", e)
            Toast.makeText(requireContext(), "Une erreur s'est produite lors de la synchronisation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewBase() {
        try {
            repository.clearDatabase()
            currentFileName = "Suivi VSL"
            currentFileUri = null
            repository.lastLoadedFileName = currentFileName
            repository.lastLoadedFileUri = currentFileUri
            binding.textViewFileName.text = "Fichier source : $currentFileName"
            Toast.makeText(requireContext(), "Base correctement créée", Toast.LENGTH_SHORT).show()
            binding.buttonSync.visibility = View.GONE
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erreur lors de la création du fichier", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_FILE_URI, currentFileUri)
        outState.putString(KEY_FILE_NAME, currentFileName)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}