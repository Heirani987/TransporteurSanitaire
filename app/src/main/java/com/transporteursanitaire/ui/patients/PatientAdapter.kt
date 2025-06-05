package com.transporteursanitaire.ui.patients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.transporteursanitaire.data.model.Patient
import com.transporteursanitaire.databinding.ItemPatientBinding

/**
 * Adapter du RecyclerView affichant la liste des patients.
 * Utilise ListAdapter et DiffUtil pour une meilleure performance lors des mises à jour.
 */
class PatientsAdapter(private val onItemClick: (Patient) -> Unit) :
    ListAdapter<Patient, PatientsAdapter.PatientViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Patient>() {
            override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                // Compare les identifiants uniques
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
                // Compare l'intégralité des données (ici, tous les champs)
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PatientViewHolder(private val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patient: Patient) {
            binding.textViewPatientName.text = patient.name
            binding.root.setOnClickListener {
                onItemClick(patient)
            }
        }
    }
}