package cr.una.buildify.director_proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import cr.una.buildify.R
import cr.una.buildify.servicios.BuscarServicios
import cr.una.buildify.visualizacionInc_eva_obs.Visualizacion_inc_eva_obs

enum class ProviderType{
    BASIC,
    GOOGLE
}

class Director_Proyecto_Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_proyecto_home)

        val cardViewTablaCost = findViewById<CardView>(R.id.CardViewServicios)
        cardViewTablaCost.setOnClickListener {
            navegarServiciosIndependientes()
        }

        val cardViewSolicitud = findViewById<CardView>(R.id.CardViewIncidentes)
        cardViewSolicitud.setOnClickListener {
            navegarIncidentes()
        }


    }
    private fun navegarServiciosIndependientes(){
        val paginaPrincipal = Intent(this,BuscarServicios::class.java)
        startActivity(paginaPrincipal)
    }

    private fun navegarIncidentes(){
        val paginaPrincipal = Intent(this, Visualizacion_inc_eva_obs::class.java)
        startActivity(paginaPrincipal)
    }
}