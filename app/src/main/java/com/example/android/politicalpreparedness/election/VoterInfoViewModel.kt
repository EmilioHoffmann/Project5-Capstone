package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: ElectionRepository,
    private val database: ElectionDatabase
) : ViewModel() {
    private val _electionDetailsResponse = MutableLiveData<VoterInfoResponse>()
    val electionDetailsResponse: LiveData<VoterInfoResponse>
        get() = _electionDetailsResponse

    private val _requestError = SingleLiveEvent<Boolean>()
    val requestError: LiveData<Boolean>
        get() = _requestError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _unavailableData = SingleLiveEvent<Boolean>()
    val unavailableData: LiveData<Boolean>
        get() = _unavailableData

    fun updateElection(election: Election) {
        viewModelScope.launch(Dispatchers.IO) {
            database.electionDao.updateElection(election)
        }
    }

    fun getElectionDetails(divisionState: String, electionId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.getElectionVoterDetails(divisionState, electionId)) {
                is NetworkResponse.Success -> {
                    _electionDetailsResponse.postValue(response.body)
                    _isLoading.postValue(false)
                }
                is NetworkResponse.ServerError -> {
                    if (response.code == 400) {
                        _unavailableData.postValue(true)
                    }
                }
                else -> {
                    _requestError.postValue(true)
                    _isLoading.postValue(false)
                }
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
}
