package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase) {

    val _requestError = SingleLiveEvent<Boolean>()

    suspend fun updateElections() {
        withContext(Dispatchers.IO) {
            when (val response = CivicsApi.retrofitService.getElections()) {
                is NetworkResponse.Success -> {
                    database.electionDao.insertElectionList(response.body.elections)
                }
                else -> {
                    _requestError.postValue(true)
                }
            }
        }
    }
//
//    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse {
//        return CivicsApi.retrofitService.getVoterInfo(address, electionId)
//    }
}
