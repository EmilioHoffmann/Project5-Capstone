package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

class ElectionsFragment : Fragment() {
    private val viewModel: VoterInfoViewModel by viewModels()

    private var _binding: FragmentElectionBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentElectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        // TODO: Add ViewModel values and create ViewModel

        // TODO: Add binding values

        // TODO: Link elections to voter info

        // TODO: Initiate recycler adapters

        // TODO: Populate recycler adapters
        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
}
