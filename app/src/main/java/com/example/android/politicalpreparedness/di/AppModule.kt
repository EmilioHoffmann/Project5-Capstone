package com.example.android.politicalpreparedness.di

import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { ElectionsViewModel() }
    viewModel { VoterInfoViewModel() }
    viewModel { RepresentativeViewModel() }
}
