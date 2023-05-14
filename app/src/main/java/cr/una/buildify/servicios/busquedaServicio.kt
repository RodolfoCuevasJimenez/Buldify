package cr.una.buildify.servicios
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import cr.una.buildify.R

class busquedaServicio : AppCompatActivity() {

    private lateinit var editTextBuscar: TextView
    private lateinit var btnBuscar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda_servicio)
        editTextBuscar = findViewById(R.id.editTextBuscar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnBuscar.setOnClickListener{
            val tipo = editTextBuscar.text.toString().trim().lowercase()
            val intent = Intent(this, BuscarServicios::class.java)
            intent.putExtra("tipo", tipo)
            startActivity(intent)
        }
    }

        }

