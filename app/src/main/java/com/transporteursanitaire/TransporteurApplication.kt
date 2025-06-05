package com.transporteursanitaire

import android.app.Application
import android.util.Log

/**
 * Classe d'application personnalisée qui initialise la configuration StAX.
 */
class TransporteurApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Forcer l'utilisation d'Aalto XML pour XMLEventFactory et les factories associées.
        // Ceci garantit que POI utilisera une implémentation présente dans votre projet.
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.event.XMLEventFactoryImpl")
        System.setProperty("javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl")
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl")

        Log.d("TransporteurApplication", "Propriétés StAX définies pour Aalto XML")
    }
}