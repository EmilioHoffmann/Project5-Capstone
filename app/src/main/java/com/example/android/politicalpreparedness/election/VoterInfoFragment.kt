package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Election
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoterInfoFragment : Fragment() {

    private var _binding: FragmentVoterInfoBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: VoterInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoterInfoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        setObservers()
        val electionClicked = VoterInfoFragmentArgs.fromBundle(requireArguments()).electionData

        viewModel.getElectionDetails(electionClicked.division.state, electionClicked.id)
        setupSaveButton(electionClicked)
        setListeners()
        return binding.root
    }

    private fun setObservers() {
        viewModel.electionDetailsResponse.observe(viewLifecycleOwner) {
            binding.voterInfoResponse = it
            binding.stateBallot.isVisible =
                it.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl?.isNotBlank() == true
            binding.stateLocations.isVisible =
                it.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl?.isNotBlank() == true
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgressBar.isVisible = isLoading
            binding.voterInfoContent.isVisible = !isLoading
        }

        viewModel.requestError.observe(viewLifecycleOwner) {
            binding.errorMessage.isVisible = true
            binding.voterInfoContent.isVisible = false
            binding.loadingProgressBar.isVisible = false
        }

        viewModel.unavailableData.observe(viewLifecycleOwner) {
            binding.unavailableDataMessage.isVisible = it
            binding.voterInfoContent.isVisible = false
            binding.loadingProgressBar.isVisible = false
        }
    }

    private fun setListeners() {
        binding.stateLocations.setOnClickListener {
            openWebUrl(viewModel.electionDetailsResponse.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl)
        }

        binding.stateBallot.setOnClickListener {
            openWebUrl(viewModel.electionDetailsResponse.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl)
        }
    }

    private fun setupSaveButton(election: Election) {
        if (election.isFavorite) {
            binding.saveButton.playAnimation()
        }

        binding.saveButton.setOnClickListener {
            election.isFavorite = !election.isFavorite
            viewModel.updateElection(election)
            animateSaveButton(election.isFavorite)
        }
    }

    private fun openWebUrl(url: String?) {
        if (url.isNullOrEmpty()) return
        val webIntent = Intent(Intent.ACTION_VIEW)
        webIntent.data = Uri.parse(url)
        startActivity(webIntent)
    }

    private fun animateSaveButton(isFavorite: Boolean) {
        if (isFavorite) {
            if (binding.saveButton.speed > 0) {
                binding.saveButton.playAnimation()
            } else {
                binding.saveButton.reverseAnimationSpeed()
                binding.saveButton.playAnimation()
            }
        } else {
            if (binding.saveButton.speed > 0) {
                binding.saveButton.reverseAnimationSpeed()
                binding.saveButton.playAnimation()
            } else {
                binding.saveButton.playAnimation()
            }
        }
    }
}
