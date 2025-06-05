package com.transporteursanitaire.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.transporteursanitaire.R
import com.transporteursanitaire.repository.UserRepository

class FileChooserFragment : Fragment() {

    private lateinit var repository: UserRepository

    // Déclaration du launcher pour obtenir le résultat de la sélection de fichier.
    private val excelFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val fileUri = result.data?.data
            fileUri?.let { uri ->
                // Vérifier l'extension du fichier via DocumentFile.
                val docFile = DocumentFile.fromSingleUri(requireContext(), uri)
                val fileName = docFile?.name ?: ""
                if (fileName.endsWith(".xlsm", ignoreCase = true) ||
                    fileName.endsWith(".xlsx", ignoreCase = true)
                ) {
                    // Retourne le résultat (URI et nom du fichier) via le Fragment Result API.
                    setFragmentResult("fileResultKey", bundleOf("fileUri" to uri.toString(), "fileName" to fileName))
                    // Retourne au fragment précédent
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Format non supporté", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialiser le repository via son singleton
        repository = UserRepository.getInstance(requireContext())
        // Retourne le layout associé (assurez-vous que R.layout.fragment_file_chooser existe)
        return inflater.inflate(R.layout.fragment_file_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Affecter le bouton du layout, lequel doit avoir l'ID "buttonSelectFile"
        view.findViewById<Button>(R.id.buttonSelectFile).setOnClickListener {
            // Appelle la méthode de création de l'intent depuis le repository
            val intent: Intent = repository.createExcelFileChooserIntent()
            excelFileLauncher.launch(intent)
        }
    }
}