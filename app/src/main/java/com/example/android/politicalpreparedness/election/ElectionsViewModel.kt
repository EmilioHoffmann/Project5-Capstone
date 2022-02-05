package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.ElectionRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElectionsViewModel(
    private val repository: ElectionRepository,
    private val database: ElectionDatabase
) : ViewModel() {
    val elections: LiveData<List<Election>>
        get() = database.electionDao.getElections()

    val favoriteElections: LiveData<List<Election>>
        get() = database.electionDao.getFavoriteElections()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val requestError: LiveData<Boolean>
        get() = repository._requestError

    init {
        initElections()
    }

    private fun initElections() {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElections()
        }
    }
    // TODO: Create live data val for upcoming elections

    // TODO: Create live data val for saved elections

    // TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    // TODO: Create functions to navigate to saved or upcoming election voter info
}
