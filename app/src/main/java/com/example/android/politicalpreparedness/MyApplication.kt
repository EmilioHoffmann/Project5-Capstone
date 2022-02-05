package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
