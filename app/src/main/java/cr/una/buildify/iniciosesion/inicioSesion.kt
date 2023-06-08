package cr.una.buildify.iniciosesion

import android.widget.*
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
import com.google.firebase.firestore.FirebaseFirestore

import cr.una.buildify.director_proyecto_drawer
import cr.una.buildify.ui.duenno_obra.duenno_obra_drawer
import cr.una.buildify.ui.evaluador_obra.Evaluador_Obra_drawer
import cr.una.buildify.ui.trabajador_independiente.trabajador_independiente_drawer
import cr.una.buildify.ui.trabajador_independiente.trabajador_independiente_main
import cr.una.buildify.ui.usuario_invitado.Usuario_Invitado_Drawer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.MainActivity
import cr.una.buildify.ProviderType
import cr.una.buildify.R
import cr.una.buildify.director_proyecto.Director_Proyecto_Home


lateinit var btnRegistrar:Button
lateinit var btnIngresar:Button
lateinit var btnGoogle:TextView
lateinit var autoCompleteTextView: AutoCompleteTextView

@Suppress("DEPRECATION")
class inicioSesion : AppCompatActivity() {

    private val GOOGLE_SING_IN = 100
    private lateinit var baseDatos: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        baseDatos = FirebaseFirestore.getInstance()

        title = "Buildify - Inicio de Sesión"
        //Configuración de botones
        //Botón de registrar
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            if(findViewById<TextView>(R.id.inputEmail).text.isNotEmpty() && findViewById<TextView>(R.id.inputContrasena).text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(findViewById<TextView>(R.id.inputEmail).text.toString(),
                    findViewById<TextView>(R.id.inputContrasena).text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            navegarPrincipal(it.result?.user?.uid?:"", it.result?.user?.email?:"",findViewById<AutoCompleteTextView>(R.id.cmbRol).text.toString())
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
                        navegarPrincipal(it.result?.user?.uid?:"", it.result?.user?.email?:"",findViewById<AutoCompleteTextView>(R.id.cmbRol).text.toString())
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

        val roles = resources.getStringArray(R.array.roles)
        val arrayAdapter = ArrayAdapter(this,R.layout.roles,roles)

        //Comprobar si existe una sesión activa
        //sesion()

        autoCompleteTextView = findViewById(R.id.cmbRol)
        with(autoCompleteTextView){
            setAdapter(arrayAdapter)
        }
    }

    private fun postUsuario(email: String,tipo: String){
        val idUsuario = findViewById<TextView>(R.id.inputEmail).text.toString() // Obtener el ID del usuario desde el TextView
        val tipo = findViewById<AutoCompleteTextView>(R.id.cmbRol).text.toString() // Obtener el tipo de usuario desde el AutoCompleteTextView

        val usuario = hashMapOf(
            "idUsuario" to idUsuario,
            "tipo" to tipo
        )

        baseDatos.collection("Usuarios")
            .document(idUsuario)
            .set(usuario)
            .addOnSuccessListener {
                val toast = Toast.makeText(this,"Usuario agregado correctamente",Toast.LENGTH_SHORT)
                toast.show()
            }
            .addOnFailureListener{
                val toast = Toast.makeText(this,"Error al agregar el usuario ",Toast.LENGTH_SHORT)
                toast.show()
            }
    }

    private fun mostrarAlerta(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en los datos ingresados")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun navegarPrincipal(uid: String,email : String,tipo : String){
        var paginaPrincipal: Intent? = null
        when(tipo){
            "Director de Proyecto" ->  paginaPrincipal = Intent(this,director_proyecto_drawer::class.java).apply {
                putExtra("UID", uid)
                putExtra("Email",email)
                putExtra("Email",email)
                                                            putExtra("Tipo",tipo)
                                                            }
            "Dueño de la Obra" -> paginaPrincipal = Intent(this, duenno_obra_drawer::class.java).apply {
                                                        putExtra("Email",email)
                                                        putExtra("Tipo",tipo)
                                                    }
            "Evaluador de la Obra" -> paginaPrincipal = Intent(this,Evaluador_Obra_drawer::class.java).apply {
                                                            putExtra("Email",email)
                                                            putExtra("Tipo",tipo)
                                                        }
            "Trabajador Independiente" -> paginaPrincipal = Intent(this,trabajador_independiente_drawer::class.java).apply {
                                                                putExtra("Email",email)
                                                                putExtra("Tipo",tipo)
                                                            }
            "Usuario Invitado" -> paginaPrincipal = Intent(this,Usuario_Invitado_Drawer::class.java).apply {
                                                        putExtra("Email",email)
                                                        putExtra("Tipo",tipo)
                                                    }
        }
        startActivity(paginaPrincipal)
    }

    /*private fun sesion(){
        val prefs : SharedPreferences? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email : String? = prefs?.getString("email",null)
        val provider: String? = prefs?.getString("tipo",null)
        if(email != null){
            navegarPrincipal(email, ProviderType.valueOf(provider.toString()))
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SING_IN){
            val tarea : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val cuenta : GoogleSignInAccount? = tarea.getResult(ApiException::class.java)
                val tipo : String = findViewById<AutoCompleteTextView>(R.id.cmbRol).text.toString()

                //Autenticación
                if(cuenta != null){
                    val credencial: AuthCredential = GoogleAuthProvider.getCredential(cuenta.idToken,null)

                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {
                        if (it.isSuccessful){
                            navegarPrincipal(cuenta.id?:"", cuenta.email?:"null", findViewById<AutoCompleteTextView>(R.id.cmbRol).text.toString())
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