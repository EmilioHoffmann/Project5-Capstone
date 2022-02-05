package com.example.android.politicalpreparedness.network

import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class ElectionRepository(private val database: ElectionDatabase) {

    val _requestError = SingleLiveEvent<Boolean>()
    val _isLoading = MutableLiveData<Boolean>()

    suspend fun updateElections() {
        _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
            when (val response = CivicsApi.retrofitService.getElections()) {
                is NetworkResponse.Success -> {
                    database.electionDao.insertElectionList(response.body.elections)
                    _isLoading.postValue(false)
                }
                else -> {
                    _requestError.postValue(true)
                    _isLoading.postValue(false)
                }
            }
        }
    }

    suspend fun getElectionVoterDetails(
        divisionState: String,
        electionID: Int
    ): NetworkResponse<VoterInfoResponse, ResponseBody> {
        return withContext(Dispatchers.IO) {
            return@withContext CivicsApi.retrofitService.getVoterInfo(divisionState, electionID)
        }
    }
}
