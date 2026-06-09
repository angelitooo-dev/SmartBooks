package co.edu.cecar.smartbooks

import android.app.Application
import co.edu.cecar.smartbooks.core.network.HttpClientProvider

class SmartBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HttpClientProvider.initialize(this)
    }
}