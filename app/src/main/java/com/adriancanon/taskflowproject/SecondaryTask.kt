package com.adriancanon.taskflowproject

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class SecondaryTask : AppCompatActivity() {
    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary_task) // Establece el diseño de la actividad

        // Obtiene las preferencias compartidas y recupera el conjunto de nombres de tareas secundarias
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val taskNames = sharedPreferences.getStringSet("secondaryTaskNames", mutableSetOf())

        // Configura la vista con las tareas secundarias
        setup(taskNames ?: mutableSetOf())
        // Configura el botón de inicio
        showhome()
    }

    // Método para configurar la vista con las tareas secundarias
    private fun setup(taskNames: MutableSet<String>) {
        val layout = findViewById<LinearLayout>(R.id.taskLayout)

        // Itera sobre cada tarea secundaria
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

            // Crea y configura un ImageButton para marcar la tarea como terminada
            val fabTask = ImageButton(this)
            fabTask.setImageResource(R.drawable.ic_close)
            fabTask.setBackgroundColor(ContextCompat.getColor(this, R.color.color_background))
            fabTask.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar")
                    .setMessage("¿Estás seguro de que deseas marcar esta tarea como terminada?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        // Guardar la tarea en las tareas terminadas
                        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val finishedTaskNames =
                            sharedPreferences.getStringSet("finishedTaskNames", mutableSetOf())
                        finishedTaskNames?.add(taskInfo)
                        val editor = sharedPreferences.edit()
                        editor.putStringSet("finishedTaskNames", finishedTaskNames)
                        editor.apply()

                        // Eliminar la tarea del layout
                        layout.removeView(taskLayout)
                        // Eliminar la tarea del conjunto de tareas secundarias
                        taskNames.remove(taskInfo)
                        editor.putStringSet("secondaryTaskNames", taskNames)
                        editor.apply()

                        dialog.dismiss()
                    }
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })
                    .show()
            }
            taskLayout.addView(fabTask)
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