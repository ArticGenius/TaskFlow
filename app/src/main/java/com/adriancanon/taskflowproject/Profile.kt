package com.adriancanon.taskflowproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Enum para definir los tipos de proveedores de autenticación
enum class ProviderType {
    Email, Google
}

class Profile : AppCompatActivity() {

    // Inicializa la instancia de Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Recupera el email y el proveedor de autenticación desde el intent
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "", provider ?: "") // Configura la vista con los datos recuperados

        // Guardado de datos en las preferencias compartidas
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        // Configura el botón de inicio
        showhome()
    }

    // Método para configurar la vista con los datos del usuario
    private fun setup(email: String, provider: String) {
        val etEmail = findViewById<TextView>(R.id.etEmail)
        val etProvider = findViewById<TextView>(R.id.etProvider)
        val btLogOut = findViewById<TextView>(R.id.btLogOut)
        val btnSave = findViewById<TextView>(R.id.btSave)
        val btnRecover = findViewById<TextView>(R.id.btRecover)
        val btnDelete = findViewById<TextView>(R.id.btDelete)
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        val tvPhoneNumber = findViewById<TextView>(R.id.tvPhoneNumber)

        // Muestra el email y el proveedor de autenticación en los TextView correspondientes
        etEmail.text = email
        etProvider.text = provider

        // Configura el botón de cerrar sesión
        btLogOut.setOnClickListener {
            // Borra los datos guardados en las preferencias compartidas
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            // Cierra la sesión de Firebase y vuelve atrás en la pila de actividades
            FirebaseAuth.getInstance().signOut()
            OnBackPressedDispatcher()
        }

        // Configura el botón de guardar datos
        btnSave.setOnClickListener {
            db.collection("users").document(email).set(
                hashMapOf(
                    "provider" to provider,
                    "address" to tvAddress.text.toString(),
                    "phone" to tvPhoneNumber.text.toString()
                )
            )
        }

        // Configura el botón de recuperar datos
        btnRecover.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                tvAddress.text = it.get("address") as String?
                tvPhoneNumber.text = it.get("phone") as String?
            }
        }

        // Configura el botón de eliminar datos
        btnDelete.setOnClickListener {
            db.collection("users").document(email).delete()
        }
    }

    // Método para configurar el botón de inicio
    private fun showhome() {
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            val homeIntent = Intent(this, MainView::class.java)
            startActivity(homeIntent) // Inicia la actividad MainView
        }
    }
}