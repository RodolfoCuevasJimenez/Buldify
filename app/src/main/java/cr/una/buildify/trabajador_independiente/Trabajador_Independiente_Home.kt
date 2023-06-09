package cr.una.buildify.trabajador_independiente

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import cr.una.buildify.R


class Trabajador_Independiente_Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trabajador_independiente_home)

        val cardViewCrearServicio= findViewById<CardView>(R.id.CardViewCrearServicio)
        cardViewCrearServicio.setOnClickListener{
            CrearServicioIndependiente()
        }

    }


    private fun CrearServicioIndependiente(){
        val paginaPrincipal = Intent(this, RegistrarServicio::class.java)
        startActivity(paginaPrincipal)
    }
}