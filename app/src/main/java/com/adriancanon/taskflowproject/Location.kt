package com.adriancanon.taskflowproject

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class Location : AppCompatActivity() {
    // Declaración de la variable del mapa
    private lateinit var map: MapView

    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo Edge-to-Edge para una interfaz de usuario inmersiva
        setContentView(R.layout.activity_location) // Establece el diseño de la actividad

        // Configura OSMdroid con las preferencias compartidas
        org.osmdroid.config.Configuration.getInstance().load(
            this, getSharedPreferences("osm", MODE_PRIVATE)
        )
        setContentView(R.layout.activity_location) // Establece el diseño de la actividad

        // Inicializa el MapView y sus configuraciones básicas
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true) // Habilita controles multitáctiles
        map.controller.setZoom(9) // Establece el nivel de zoom del mapa
        map.controller.setCenter(GeoPoint(40.500, -3.667)) // Establece el centro del mapa

        // Inicializa el Geocoder para obtener direcciones a partir de coordenadas
        val geocoder = Geocoder(this, Locale.getDefault())

        // Define un receptor de eventos del mapa
        val receiver = object : MapEventsReceiver {
            // Maneja los eventos de un solo toque en el mapa
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    // Crea y agrega un marcador en la posición tocada
                    val marker = Marker(map)
                    marker.position = it
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(marker)
                    map.invalidate() // Redibuja el mapa

                    // Obtiene la dirección a partir de las coordenadas y la guarda en el intent de resultado
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val address = addresses?.get(0)?.getAddressLine(0)

                    val resultIntent = Intent()
                    resultIntent.putExtra("location", address)
                    setResult(RESULT_OK, resultIntent)
                    finish() // Finaliza la actividad
                }
                return true
            }

            // Maneja los eventos de pulsación larga en el mapa
            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let {
                    // Crea y agrega un marcador en la posición tocada
                    val marker = Marker(map)
                    marker.position = it
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(marker)
                    map.invalidate() // Redibuja el mapa

                    // Obtiene la dirección a partir de las coordenadas y la guarda en el intent de resultado
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val address = addresses?.get(0)?.getAddressLine(0)

                    val resultIntent = Intent()
                    resultIntent.putExtra("location", address)
                    setResult(RESULT_OK, resultIntent)
                    finish() // Finaliza la actividad
                }
                return true
            }
        }

        // Agrega el receptor de eventos al mapa
        val overlay = MapEventsOverlay(receiver)
        map.overlays.add(overlay)
    }
}