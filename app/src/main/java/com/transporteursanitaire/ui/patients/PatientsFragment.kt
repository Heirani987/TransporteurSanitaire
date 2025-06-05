package com.transporteursanitaire.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.transporteursanitaire.R
import com.transporteursanitaire.data.model.Patient
import com.transporteursanitaire.databinding.FragmentPatientsBinding

class PatientsFragment : Fragment() {

    private lateinit var binding: FragmentPatientsBinding
    private val viewModel: PatientsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Liaison du layout au ViewModel grâce à DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_patients, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()

        // Observer la liste des patients pour actualiser le Spinner
        viewModel.patients.observe(viewLifecycleOwner) { patientList ->
            updateSpinnerAdapter(patientList)
        }

        // Observer l'état de chargement pour afficher ou masquer la ProgressBar
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarPatients.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Afficher les erreurs (par Toast)
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Configure le listener du Spinner.
     */
    private fun setupSpinner() {
        binding.spinnerPatients.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                viewModel.patients.value?.let { patientList ->
                    val selectedPatient = patientList[position]
                    viewModel.setSelectedPatient(selectedPatient)
                    updatePatientDetails(selectedPatient)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setSelectedPatient(null)
                clearPatientDetails()
            }
        }
    }

    /**
     * Met à jour l'adapter du Spinner avec la liste des patients.
     */
    private fun updateSpinnerAdapter(patientList: List<Patient>) {
        val names = patientList.map { it.name } // On affiche les noms
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPatients.adapter = adapter

        // Rétablir la sélection précédente si elle existe
        viewModel.selectedPatient.value?.let { selected ->
            val index = patientList.indexOf(selected)
            if (index != -1) {
                binding.spinnerPatients.setSelection(index)
            }
        }
    }

    /**
     * Met à jour la zone de détails avec les informations du patient sélectionné.
     */
    private fun updatePatientDetails(patient: Patient) {
        // Informations principales
        binding.textViewDateNaissance.text = patient.birthDate
        // 'DN' n'est pas disponible, on affiche "N/A"
        binding.textViewDN.text = requireContext().getString(R.string.na)
        binding.textViewTel.text = patient.tel

        // Informations administratives
        // Le TextView pour DEP est lié via BindingAdapter (app:depWarning) dans le layout.
        binding.textViewDEP.text = patient.dep
        binding.textViewDA.text = patient.da
        // Utilisation d'un string resource pour concaténer les dates avec placeholder.
        binding.textViewValiditeDA.text = String.format(
            requireContext().getString(R.string.validite_da_format),
            patient.validiteDaDebut,
            patient.validiteDaFin
        )

        // Informations de localisation et déplacement
        binding.textViewAdresseGeo.text = patient.adresseGeographique
        binding.textViewComplementAdresse.text = patient.complementAdresse
        // 'PK - KM SUPLL' n'est pas disponible, on affiche "N/A"
        binding.textViewPKKM.text = requireContext().getString(R.string.na)
        binding.textViewTrajet.text = patient.trajet
        // Utilisation du champ 'pointPcArrivee' pour afficher le point PC Patient
        binding.textViewPointPC.text = patient.pointPcArrivee
    }

    /**
     * Efface la zone de détails.
     */
    private fun clearPatientDetails() {
        binding.textViewDateNaissance.text = ""
        binding.textViewDN.text = ""
        binding.textViewTel.text = ""
        binding.textViewDEP.text = ""
        binding.textViewDA.text = ""
        binding.textViewValiditeDA.text = ""
        binding.textViewAdresseGeo.text = ""
        binding.textViewComplementAdresse.text = ""
        binding.textViewPKKM.text = ""
        binding.textViewTrajet.text = ""
        binding.textViewPointPC.text = ""
    }
}