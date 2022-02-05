package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RepresentativeFragment : Fragment() {

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: RepresentativeViewModel by viewModel()

    private val representativeListAdapter = RepresentativeListAdapter()

    companion object {
        // TODO: Add Constant for Location request
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setListeners()
        setObservers()
        binding.representativesRV.adapter = representativeListAdapter
        binding.viewModel = viewModel
        return binding.root
    }

    private fun setListeners() {
        binding.searchButton.setOnClickListener {
            viewModel.getRepresentatives()
        }

        binding.locationButton.setOnClickListener {
        }

        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.state.value = binding.state.selectedItem as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.state.value = binding.state.selectedItem as String
            }
        }
    }

    private fun setObservers() {
        viewModel.representatives.observe(viewLifecycleOwner) {
            representativeListAdapter.submitList(it)
            binding.emptyListMessage.isVisible = it.isEmpty()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // TODO: Handle location permission result to get location on permission granted
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            // TODO: Request Location permissions
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        // TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return false
    }

    private fun getLocation() {
        // TODO: Get location from LocationServices
        // TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
