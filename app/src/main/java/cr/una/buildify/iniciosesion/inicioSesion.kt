package cr.una.buildify.iniciosesion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import cr.una.buildify.MainActivity
import cr.una.buildify.ProviderType
import cr.una.buildify.R
import cr.una.buildify.director_proyecto.Director_Proyecto_Home


lateinit var btnRegistrar:Button
lateinit var btnIngresar:Button
lateinit var btnGoogle:TextView

@Suppress("DEPRECATION")
class inicioSesion : AppCompatActivity() {

    private val GOOGLE_SING_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        title = "Buildify - Inicio de Sesión"
        //Configuración de botones
        //Botón de registrar
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            if(findViewById<TextView>(R.id.inputEmail).text.isNotEmpty() && findViewById<TextView>(R.id.inputContrasena).text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(findViewById<TextView>(R.id.inputEmail).text.toString(),
                    findViewById<TextView>(R.id.inputContrasena).text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            navegarPrincipal(it.result?.user?.uid?:"", it.result?.user?.email?:"",ProviderType.BASIC)
                        }
                        else{
                            mostrarAlerta()
                        }
                }

            }
            else{
                mostrarAlerta()
            }
        }

        //Botón de acceder
        btnIngresar = findViewById(R.id.btnIngresar)
        btnIngresar.setOnClickListener{
            if(findViewById<TextView>(R.id.inputEmail).text.isNotEmpty() && findViewById<TextView>(R.id.inputContrasena).text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(findViewById<TextView>(R.id.inputEmail).text.toString(),
                    findViewById<TextView>(R.id.inputContrasena).text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        navegarPrincipal(it.result?.user?.uid?:"", it.result?.user?.email?:"",ProviderType.BASIC)
                    }
                    else{
                        mostrarAlerta()
                    }
                }

            }
            else{
                mostrarAlerta()
            }
        }

        //Botón de inicio de sesión con google
        btnGoogle = findViewById(R.id.txtSesionGoogle)
        btnGoogle.setOnClickListener{
            // Configuración
            val googleConf : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleCliente: GoogleSignInClient? = GoogleSignIn.getClient(this,googleConf)
            googleCliente?.signOut()
            if (googleCliente != null) {
                startActivityForResult(googleCliente.signInIntent,GOOGLE_SING_IN)
            }
        }

        //Comprobar si existe una sesión activa
        sesion()
    }

    private fun mostrarAlerta(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en los datos ingresados")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun navegarPrincipal(uid: String, email : String,provider : ProviderType){
        val paginaPrincipal = Intent(this,Director_Proyecto_Home::class.java).apply {
            putExtra("UID", uid)
            putExtra("Email",email)
            putExtra("Provider",provider.name)
        }
        startActivity(paginaPrincipal)
    }

    private fun sesion(){
        val prefs : SharedPreferences? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val uid : String? = prefs?.getString("UID", null)
        val email : String? = prefs?.getString("Email",null)
        val provider: String? = prefs?.getString("Provider",null)
        if(email != null && uid != null){
            navegarPrincipal(uid, email, ProviderType.valueOf(provider.toString()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SING_IN){
            val tarea : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val cuenta : GoogleSignInAccount? = tarea.getResult(ApiException::class.java)

                //Autenticación
                if(cuenta != null){
                    val credencial: AuthCredential = GoogleAuthProvider.getCredential(cuenta.idToken,null)

                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {
                        if (it.isSuccessful){
                            navegarPrincipal(cuenta.id?:"", cuenta.email?:"null",ProviderType.GOOGLE)
                        }
                        else{
                            mostrarAlerta()
                        }
                    }
                }
            }catch (e:ApiException){
                mostrarAlerta()
            }

        }
    }

}