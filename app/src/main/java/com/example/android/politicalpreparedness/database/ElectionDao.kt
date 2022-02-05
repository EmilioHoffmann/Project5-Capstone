package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElectionList(electionList: List<Election>)

    @Query("select * from election_table order by electionDay asc")
    fun getElections(): LiveData<List<Election>>

    @Query("select * from election_table where id = :electionId")
    fun getElection(electionId: Int): Election

    @Query("select * from election_table where isFavorite = 1")
    fun getFavoriteElections(): LiveData<List<Election>>

    @Query("delete from election_table where id = :electionId")
    fun deleteElection(electionId: Int)

    @Query("delete from election_table")
    fun deleteAllElections()
}
