package com.adriancanon.taskflowproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewTask : AppCompatActivity() {

    // Constante para el código de solicitud de ubicación
    private val LOCATION_REQUEST_CODE = 1

    // Declaración de variables para los elementos de la interfaz
    private lateinit var etTaskName: AppCompatEditText
    private lateinit var btnSelectDateTime: Button
    private lateinit var btnLocation: Button
    private lateinit var btnClose: ImageButton
    private lateinit var spnOptions: Spinner
    private lateinit var btnAdd: Button

    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo Edge-to-Edge para una interfaz de usuario inmersiva
        setContentView(R.layout.activity_new_task) // Establece el diseño de la actividad

        // Inicialización de los elementos de la interfaz
        etTaskName = findViewById(R.id.etTaskName)
        btnLocation = findViewById(R.id.btnLocation)
        btnSelectDateTime = findViewById(R.id.btnSelectDateTime)
        spnOptions = findViewById(R.id.spnOptions)
        btnAdd = findViewById(R.id.btnAdd)

        // Configura el spinner con opciones
        val options = resources.getStringArray(R.array.spinner_options)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, options)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnOptions.adapter = adapter

        // Configura el botón para seleccionar fecha y hora
        btnSelectDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    val selectedDateTime = calendar.time

                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    btnSelectDateTime.text = format.format(selectedDateTime)
                }

                // Muestra el diálogo de selección de hora
                TimePickerDialog(
                    this,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            // Muestra el diálogo de selección de fecha
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Configura el botón para seleccionar ubicación
        btnLocation.setOnClickListener {
            val intent = Intent(this, Location::class.java)
            startActivityForResult(intent, LOCATION_REQUEST_CODE)
        }

        // Configura el botón para agregar una nueva tarea
        btnAdd.setOnClickListener {
            val selectedOption = spnOptions.selectedItem.toString()
            val taskName = etTaskName.text.toString()
            val dateTime = btnSelectDateTime.text.toString()
            val location = btnLocation.text.toString()

            val sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            when (selectedOption) {
                "Tareas Principales" -> {
                    val taskNames = sharedPreferences.getStringSet("mainTaskNames", mutableSetOf())
                        ?: mutableSetOf()
                    taskNames.add("$taskName - $dateTime - $location")
                    editor.putStringSet("mainTaskNames", taskNames)
                    editor.apply()
                    val intentMain = Intent(this, MainTask::class.java)
                    startActivity(intentMain)
                }

                "Tareas Secundarias" -> {
                    val taskNames =
                        sharedPreferences.getStringSet("secondaryTaskNames", mutableSetOf())
                            ?: mutableSetOf()
                    taskNames.add("$taskName - $dateTime - $location")
                    editor.putStringSet("secondaryTaskNames", taskNames)
                    editor.apply()
                    val intentSecondary = Intent(this, SecondaryTask::class.java)
                    startActivity(intentSecondary)
                }

                else -> {
                    val taskNames =
                        sharedPreferences.getStringSet("finishedTaskNames", mutableSetOf())
                            ?: mutableSetOf()
                    taskNames.add("$taskName - $dateTime - $location")
                    editor.putStringSet("finishedTaskNames", taskNames)
                    editor.apply()
                    val intentFinished = Intent(this, FinishedTask::class.java)
                    startActivity(intentFinished)
                }
            }
        }

        // Obtiene la ubicación seleccionada desde el intent y la establece en el botón
        val location = intent.getStringExtra("location")
        btnLocation.text = location

        // Configura el botón de inicio
        showhome()
    }

    // Maneja el resultado de la actividad de selección de ubicación
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            val location = data?.getStringExtra("location")
            btnLocation.text = location
        }
    }

    // Guarda el estado de la actividad cuando se pausa
    override fun onPause() {
        super.onPause()

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("editText", etTaskName.text.toString())
            putString("fechaHora", btnSelectDateTime.text.toString())
            putString("ubicacion", btnLocation.text.toString())
            apply()
        }
    }

    // Limpia el estado guardado de la actividad cuando se destruye
    override fun onDestroy() {
        super.onDestroy()

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            remove("editText")
            remove("fechaHora")
            remove("ubicacion")
            apply()
        }
    }

    // Método para configurar el botón de inicio
    private fun showhome() {
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            // Inicia la actividad MainView cuando se hace clic en el botón
            val homeIntent = Intent(this, MainView::class.java)
            startActivity(homeIntent)
        }
    }
}