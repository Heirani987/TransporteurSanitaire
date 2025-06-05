package com.transporteursanitaire.ui.patientdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.transporteursanitaire.databinding.FragmentPatientDetailBinding

/**
 * Fragment pour afficher et modifier les informations d'un patient.
 */
class PatientDetailFragment : Fragment() {

    private var _binding: FragmentPatientDetailBinding? = null
    private val binding get() = _binding!!

    // Utilisation explicite du type pour le delegate viewModels.
    private val viewModel: PatientDetailViewModel by viewModels<PatientDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        // Utilisez viewLifecycleOwner pour les observateurs LiveData.
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Récupération de l'ID du patient via PatientDetailFragmentArgs (Safe Args).
        // Vérifiez que Safe Args est correctement configuré dans votre projet.
        val patientId = arguments?.let { bundle: Bundle -> PatientDetailFragmentArgs.fromBundle(bundle).patientId }
        patientId?.let { id: Int -> viewModel.loadPatient(id) }

        binding.buttonEdit.setOnClickListener {
            toggleEditing(true)
        }

        binding.buttonSave.setOnClickListener {
            viewModel.updatePatient()
            // Affiche un Toast si un message d'erreur est présent.
            viewModel.errorMessage.value?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
            toggleEditing(false)
        }
    }

    /**
     * Active ou désactive le mode édition pour les champs de saisie.
     * Note : Le layout utilise désormais l'ID editTextPatientTypeTraitement pour le type de traitement.
     */
    private fun toggleEditing(enable: Boolean) {
        binding.editTextPatientName.isEnabled = enable
        binding.editTextPatientTypeTraitement.isEnabled = enable
        binding.buttonEdit.visibility = if (enable) View.GONE else View.VISIBLE
        binding.buttonSave.visibility = if (enable) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}