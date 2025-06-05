package com.transporteursanitaire.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// Importez le Fragment depuis androidx (et non android.app.Fragment)
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.transporteursanitaire.R
import com.transporteursanitaire.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    // Exemple d’items pour l’écran d’accueil. Assurez-vous que HomeItem, HomeDestination et HomeIconAdapter existent.
    private val homeItems by lazy {
        listOf(
            HomeItem(getString(R.string.home_patients), R.drawable.ic_patients, HomeDestination.PATIENTS),
            HomeItem(getString(R.string.home_planning), R.drawable.ic_planning, HomeDestination.PLANNING),
            HomeItem(getString(R.string.home_rdv), R.drawable.ic_rdv, HomeDestination.RDV),
            HomeItem(getString(R.string.home_sync), R.drawable.ic_sync, HomeDestination.SYNC)
        )
    }

    // Signature correcte de onCreateView en Kotlin
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Signature correcte de onViewCreated, en appelant toujours super.onViewCreated(view, savedInstanceState)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvHomeIcons.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = HomeIconAdapter(homeItems) { item ->
            when (item.destination) {
                HomeDestination.PATIENTS -> findNavController().navigate(R.id.action_homeFragmentToPatientsFragment)
                HomeDestination.PLANNING -> findNavController().navigate(R.id.action_homeFragmentToPlanningFragment)
                HomeDestination.RDV -> findNavController().navigate(R.id.action_homeFragmentToRdvFragment)
                HomeDestination.SYNC -> findNavController().navigate(R.id.action_homeFragmentToSynchronizationFragment)
            }
        }
        binding.rvHomeIcons.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}