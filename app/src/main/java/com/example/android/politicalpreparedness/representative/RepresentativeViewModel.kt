package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeViewModel(
    private val repository: ElectionRepository
) : ViewModel() {

    var addressLine1 = MutableLiveData<String>("Amphitheatre Parkway")
    var addressLine2 = MutableLiveData<String>("1600")
    var city = MutableLiveData<String>("Mountain View")
    var state = MutableLiveData<String>("California")
    var zip = MutableLiveData<String>("94043")

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives
    // TODO: Establish live data for representatives and address

    // TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

     val (offices, officials) = getRepresentativesDeferred.await()
     _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

     Note: getRepresentatives in the above code represents the method used to fetch data from the API
     Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun getRepresentatives() {
        viewModelScope.launch(Dispatchers.IO) {
            val address = Address(
                addressLine1.value.orEmpty(),
                addressLine2.value.orEmpty(),
                city.value.orEmpty(),
                state.value.orEmpty(),
                zip.value.orEmpty()
            ).toFormattedString()
            when (val response = repository.getRepresentatives(address)) {
                is NetworkResponse.Success -> {
                    val (offices, officials) = response.body
                    withContext(Dispatchers.Main) {
                        _representatives.value =
                            offices.flatMap { office -> office.getRepresentatives(officials) }
                    }
                }
                else -> {
                }
            }
        }
    }

    // TODO: Create function get address from geo location
}
