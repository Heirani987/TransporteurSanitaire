package com.transporteursanitaire.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.transporteursanitaire.databinding.FragmentUserListBinding
import com.transporteursanitaire.data.model.User
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class UserFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = UserAdapter(
            onEdit = { user -> showEditUser(user) },
            onDelete = { user -> deleteUser(user) }
        )
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter

        binding.buttonAddUser.setOnClickListener {
            showAddUser()
        }

        // Observe users list
        viewModel.users.observe(viewLifecycleOwner) { userList ->
            adapter.submitList(userList)
            binding.textViewEmpty.visibility = if (userList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showAddUser() {
        // TODO : ouvrir un écran/dialog d’ajout. Pour test, on ajoute un user "Chauffeur X"
        lifecycleScope.launch {
            viewModel.addUserAuto()
        }
    }

    private fun showEditUser(user: User) {
        Toast.makeText(requireContext(), "Modification non implémentée", Toast.LENGTH_SHORT).show()
        // TODO : afficher un écran d’édition
    }

    private fun deleteUser(user: User) {
        lifecycleScope.launch {
            viewModel.deleteUser(user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}