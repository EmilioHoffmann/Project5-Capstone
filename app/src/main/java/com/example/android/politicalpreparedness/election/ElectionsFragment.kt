package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : Fragment() {
    private val viewModel: ElectionsViewModel by viewModel()

    private var _binding: FragmentElectionBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var electionsAdapter: ElectionListAdapter
    private lateinit var favoriteElectionsAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        setObservers()

        electionsAdapter = ElectionListAdapter {
            navigateToElectionDetail(it)
        }

        favoriteElectionsAdapter = ElectionListAdapter {
            navigateToElectionDetail(it)
        }

        binding.upcomingElectionRV.adapter = electionsAdapter
        binding.savedElectionRV.adapter = favoriteElectionsAdapter

        return binding.root
    }

    private fun navigateToElectionDetail(election: Election) {
        findNavController().navigate(ElectionsFragmentDirections.toVoterInfoFragment(election))
    }

    private fun setObservers() {
        viewModel.elections.observe(viewLifecycleOwner) {
            electionsAdapter.submitList(it)
            binding.loadingProgressBar.isVisible = it.isEmpty()
        }

        viewModel.favoriteElections.observe(viewLifecycleOwner) {
            favoriteElectionsAdapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.isVisible = it
        }

        viewModel.requestError.observe(viewLifecycleOwner) {
            if (it && viewModel.elections.value?.isEmpty() == true) {
                binding.electionsContainer.isVisible = false
                binding.errorMessage.isVisible = true
                binding.loadingProgressBar.isVisible = false
            }
        }
    }
}
