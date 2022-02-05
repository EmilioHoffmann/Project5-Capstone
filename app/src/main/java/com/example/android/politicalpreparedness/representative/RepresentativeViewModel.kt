package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeViewModel(
    private val repository: ElectionRepository
) : ViewModel() {

    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var zip = MutableLiveData<String>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _isLoading = SingleLiveEvent<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getRepresentatives() {
        _isLoading.postValue(true)
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
                    _isLoading.postValue(false)
                }
                else -> {
                    _isLoading.postValue(false)
                }
            }
        }
    }

    fun setAddress(address: Address) {
        addressLine1.value = address.line1
        addressLine2.value = address.line2.orEmpty()
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
        getRepresentatives()
    }
}
