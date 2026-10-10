package com.daragly.daraglyhair

import android.app.Application
import com.daragly.daraglyhair.common.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HallaakApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HallaakApplication)
            androidLogger()
            modules(appModule)
        }
    }
}
