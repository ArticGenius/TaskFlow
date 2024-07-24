package com.adriancanon.taskflowproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {


    // Configuración del lanzador de actividad para el inicio de sesión con Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showProfile(account.email ?: "", ProviderType.Google)
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    )

    // Método que se llama cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo Edge-to-Edge para una interfaz de usuario inmersiva
        setContentView(R.layout.activity_login) // Establece el diseño de la actividad

        setup() // Configura los botones y los campos de entrada
        session() // Verifica si hay una sesión activa
        showhome() // Configura el botón de inicio
    }

    // Método que se llama cuando la actividad se vuelve visible
    override fun onStart() {
        super.onStart()
        val authLayout = findViewById<LinearLayout>(R.id.authLayout)
        authLayout.visibility = View.VISIBLE // Hace visible el layout de autenticación
    }

    // Método para verificar si hay una sesión activa
    private fun session() {
        val authLayout = findViewById<LinearLayout>(R.id.authLayout)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            authLayout.visibility = View.INVISIBLE // Oculta el layout de autenticación
            showProfile(email, ProviderType.valueOf(provider)) // Muestra el perfil del usuario
        }
    }

    // Método para configurar los botones y los campos de entrada
    private fun setup() {
        val btSingUp = findViewById<Button>(R.id.btSingUp)
        val etEmailAdress = findViewById<EditText>(R.id.etEmailAdress)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btLogin = findViewById<Button>(R.id.btLogin)
        val btGoogle = findViewById<Button>(R.id.btGoogle)

        // Configura el botón de registro
        btSingUp.setOnClickListener {
            if (etEmailAdress.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etEmailAdress.text.toString(),
                    etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showProfile(it.result?.user?.email ?: "", ProviderType.Email)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        // Configura el botón de inicio de sesión
        btLogin.setOnClickListener {
            if (etEmailAdress.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etEmailAdress.text.toString(),
                    etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showProfile(it.result?.user?.email ?: "", ProviderType.Email)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        // Configura el botón de inicio de sesión con Google
        btGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut() // Cierra sesión de cualquier cuenta anterior
            googleSignInLauncher.launch(googleClient.signInIntent) // Inicia el flujo de inicio de sesión con Google
        }
    }

    // Método para mostrar una alerta en caso de error
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Método para mostrar el perfil del usuario
    private fun showProfile(email: String, provider: ProviderType) {
        val profileIntent = Intent(this, Profile::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(profileIntent)
    }

    // Método para configurar el botón de inicio
    private fun showhome() {
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            val homeIntent = Intent(this, MainView::class.java)
            startActivity(homeIntent)
        }
    }
}