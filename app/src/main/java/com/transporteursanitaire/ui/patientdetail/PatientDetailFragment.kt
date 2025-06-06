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
    private val viewModel: PatientDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@PatientDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Récupération de l'ID du patient via PatientDetailFragmentArgs (Safe Args).
        val patientId: Int? = arguments?.let { bundle ->
            PatientDetailFragmentArgs.fromBundle(bundle).patientId
        }

        patientId?.let { id ->
            viewModel.loadPatient(id)
        }

        binding.buttonEdit.setOnClickListener {
            toggleEditing(true)
        }

        binding.buttonSave.setOnClickListener {
            viewModel.updatePatient()
            viewModel.errorMessage.value?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
            toggleEditing(false)
        }
    }

    /**
     * Active ou désactive le mode édition pour les champs de saisie.
     */
    private fun toggleEditing(enable: Boolean) {
        binding.apply {
            editTextPatientName.isEnabled = enable
            editTextPatientTypeTraitement.isEnabled = enable
            buttonEdit.visibility = if (enable) View.GONE else View.VISIBLE
            buttonSave.visibility = if (enable) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}