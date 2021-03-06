package io.sellmair.example

import android.app.Application
import io.sellmair.example.destination.AppDestination
import io.sellmair.kompass.*

/**
 * Created by sebastiansellmair on 27.01.18.
 */

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        val kompass = Kompass.builder<AppDestination>()
                .autoCrane()
                .autoMap()
            .autoDetour()
                .build()

        DummyDependencyHolder.setKompass(kompass)
    }
}
