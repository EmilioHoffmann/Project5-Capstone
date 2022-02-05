package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
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

    val requestError: LiveData<Boolean>
        get() = repository._requestError

    val isLoading: LiveData<Boolean>
        get() = repository._isLoading

    init {
        initElections()
    }

    private fun initElections() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElections()
        }
    }
}
