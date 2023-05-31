package cr.una.buildify.carga_archivos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView
import cr.una.buildify.R

lateinit var btn_Preconstruccion: CardView
lateinit var btn_Documentos: CardView
lateinit var btn_Progreso: CardView


class Cargar_Archivos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargar_archivos)

        btn_Preconstruccion = findViewById(R.id.btn_Preconstruccion)
        btn_Preconstruccion.setOnClickListener {
            val siguiente = Intent(this, Cargar_Planos::class.java)
            startActivity(siguiente)
        }

        btn_Documentos = findViewById(R.id.btn_Documentos)
        btn_Documentos.setOnClickListener {
            val siguiente = Intent(this, Cargar_Documentos::class.java)
            startActivity(siguiente)
        }

        btn_Progreso = findViewById(R.id.btn_Progreso)
        btn_Progreso.setOnClickListener {
            val siguiente = Intent(this, Cargar_Progreso::class.java)
            startActivity(siguiente)
        }
    }
}