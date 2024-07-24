package com.adriancanon.taskflowproject

import android.content.Intent
import android.os.Bundle
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class FinishedTask : AppCompatActivity() {
    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_task)

        // Obtiene las preferencias compartidas y recupera el conjunto de nombres de tareas finalizadas
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val taskNames =
            sharedPreferences.getStringSet("finishedTaskNames", mutableSetOf())?.toMutableSet()

        // Configura la vista con las tareas finalizadas
        setup(taskNames ?: mutableSetOf())
        // Configura el botón de inicio
        showhome()
    }

    // Método para configurar la vista con las tareas finalizadas
    private fun setup(taskNames: MutableSet<String>) {
        val layout = findViewById<LinearLayout>(R.id.taskLayout)
        layout.removeAllViews() // Limpia el layout antes de agregar nuevas vistas

        // Itera sobre cada tarea finalizada
        for (taskInfo in taskNames) {
            // Crea un nuevo LinearLayout horizontal para cada tarea
            val taskLayout = LinearLayout(this)
            taskLayout.orientation = LinearLayout.HORIZONTAL

            // Crea y configura un TextView para mostrar el nombre de la tarea
            val tvTask = TextView(this)
            tvTask.text = taskInfo
            tvTask.setTextColor(ContextCompat.getColor(this, R.color.title_card_name))
            tvTask.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background))
            tvTask.background = ContextCompat.getDrawable(this, R.drawable.corner_radius)
            tvTask.textAlignment = TEXT_ALIGNMENT_CENTER
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            params.setMargins(0, 15, 0, 0)
            tvTask.layoutParams = params
            taskLayout.addView(tvTask)

            // Crea y configura un ImageButton para eliminar la tarea
            val fabTask = ImageButton(this)
            fabTask.setImageResource(R.drawable.ic_close)
            fabTask.setBackgroundColor(ContextCompat.getColor(this, R.color.color_background))

            // Configura el evento de clic del botón para mostrar un diálogo de confirmación
            fabTask.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar")
                    .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        // Elimina la tarea del LinearLayout
                        layout.removeView(taskLayout)
                        // Elimina la tarea del conjunto de tareas
                        taskNames.remove(taskInfo)
                        // Actualiza las SharedPreferences
                        val editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit()
                        editor.putStringSet("finishedTaskNames", taskNames)
                        editor.apply()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            // Agrega el botón al layout de la tarea
            taskLayout.addView(fabTask)

            // Agrega el layout de la tarea al layout principal
            layout.addView(taskLayout)
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