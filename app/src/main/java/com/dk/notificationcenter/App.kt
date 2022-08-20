package com.dk.notificationcenter

import android.app.Application

class App : Application() {

    companion object{
        lateinit var INSTANCE:Application
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}