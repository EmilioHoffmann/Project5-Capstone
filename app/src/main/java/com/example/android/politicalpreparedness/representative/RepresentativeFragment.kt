package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RepresentativeFragment : Fragment() {

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: RepresentativeViewModel by viewModel()

    private val representativeListAdapter = RepresentativeListAdapter()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableLocationRequestLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        setRequestPermissionLauncher()
        setListeners()
        setObservers()
        binding.representativesRV.adapter = representativeListAdapter
//        binding.representativesRV.layoutManager = object : LinearLayoutManager(requireContext()) {
//            override fun canScrollVertically(): Boolean {
//                return representativeListAdapter.itemCount != 0
//            }
//        }
        binding.viewModel = viewModel
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    private fun setRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getLocation()
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(
                        context,
                        R.string.permission_denied_explanation,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    showLocationRequestSnackBar()
                }
            }

        enableLocationRequestLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getLocation()
            }
        }
    }

    private fun setListeners() {
        binding.searchButton.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives()
        }

        binding.locationButton.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
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

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.isVisible = it
        }
    }

    private fun checkLocationPermissions() {
        if (hasLocationPermission()) {
            getLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        try {
            if (hasLocationPermission()) {
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_LOW_POWER
                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingsClient = LocationServices.getSettingsClient(requireActivity())

                val locationSettingsResponseTask =
                    settingsClient.checkLocationSettings(builder.build())
                locationSettingsResponseTask.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            enableLocationRequestLauncher.launch(
                                IntentSenderRequest.Builder(exception.resolution).build()
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            Log.d(
                                "RepresentativeFragment",
                                "Error getting location settings resolution: " + sendEx.message
                            )
                        }
                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            R.string.location_required_error,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(android.R.string.ok) {
                            getLocation()
                        }.show()
                    }
                }
                locationSettingsResponseTask.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val locationResult = fusedLocationProviderClient.lastLocation
                        locationResult.addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                task.result?.let { location ->
                                    viewModel.setAddress(geoCodeLocation(location))
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("RepresentativeFragment", "Exception: ${e.message}")
        }
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

    private fun showLocationRequestSnackBar() {
        Snackbar.make(
            requireView(),
            R.string.permission_denied_explanation,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.settings) {
                startActivity(
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }.show()
    }
}
