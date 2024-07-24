package com.adriancanon.taskflowproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainView : AppCompatActivity() {
    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo Edge-to-Edge para una interfaz de usuario inmersiva
        setContentView(R.layout.activity_main_view) // Establece el diseño de la actividad

        // Obtiene referencias a los botones y al FloatingActionButton desde el layout
        val btnMainTask = findViewById<AppCompatButton>(R.id.btnMainTask)
        val btnSecondaryTask = findViewById<AppCompatButton>(R.id.btnSecondaryTask)
        val btnFinishedTask = findViewById<AppCompatButton>(R.id.btnFinishedTask)
        val fabAddNewTask = findViewById<FloatingActionButton>(R.id.fabAddNewTask)
        val btnLogin = findViewById<ImageButton>(R.id.btnLogin)

        // Configura el evento de clic para el botón de tareas principales
        btnMainTask.setOnClickListener {
            val intent = Intent(this, MainTask::class.java)
            startActivity(intent) // Inicia la actividad MainTask
        }

        // Configura el evento de clic para el botón de tareas secundarias
        btnSecondaryTask.setOnClickListener {
            val intent = Intent(this, SecondaryTask::class.java)
            startActivity(intent) // Inicia la actividad SecondaryTask
        }

        // Configura el evento de clic para el botón de tareas finalizadas
        btnFinishedTask.setOnClickListener {
            val intent = Intent(this, FinishedTask::class.java)
            startActivity(intent) // Inicia la actividad FinishedTask
        }

        // Configura el evento de clic para el FloatingActionButton para agregar una nueva tarea
        fabAddNewTask.setOnClickListener {
            val intent = Intent(this, NewTask::class.java)
            startActivity(intent) // Inicia la actividad NewTask
        }

        // Configura el evento de clic para el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent) // Inicia la actividad Login
        }
    }
}