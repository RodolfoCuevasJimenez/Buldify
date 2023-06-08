package cr.una.buildify

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import cr.una.buildify.iniciosesion.inicioSesion

enum class ProviderType{
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {

    lateinit var btnCerrarSesion: Button
    lateinit var txtNombreUsuario:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Obtener los datos del login
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")

        txtNombreUsuario.text = email


        // Guardar datos de sesión
        val prefs : SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs?.putString("email",email)
        prefs?.putString("provider",provider)
        prefs?.apply()


        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener{
            //Borrado de datos de sesión
            val prefs : SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs?.clear()
            prefs?.apply()

            FirebaseAuth.getInstance().signOut()
            this.onBackPressed()
        }

    }
}