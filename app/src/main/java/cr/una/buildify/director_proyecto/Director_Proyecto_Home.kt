package cr.una.buildify.director_proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import android.content.Intent
import cr.una.buildify.ProviderType


import cr.una.buildify.R
import cr.una.buildify.director_proyecto.solicitud_Detalle.solicitudDetalleActivity
import cr.una.buildify.director_proyecto.tabla_costo.tablaCostoActivity

enum class ProviderType{
    BASIC,
    GOOGLE
}

class Director_Proyecto_Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_proyecto_home)

        val cardViewTablaCost = findViewById<CardView>(R.id.cardViewTablaCosto)
        cardViewTablaCost.setOnClickListener {
            navegarTablaCosto()
        }

        val cardViewSolicitud = findViewById<CardView>(R.id.cardViewSolicitudDetalle)
        cardViewSolicitud.setOnClickListener {
            navegarSolicitudDetalle()
        }


    }



    private fun navegarTablaCosto(){
        val paginaPrincipal = Intent(this,tablaCostoActivity::class.java)
        startActivity(paginaPrincipal)
    }

    private fun navegarSolicitudDetalle(){
        val paginaPrincipal = Intent(this,solicitudDetalleActivity::class.java)
        startActivity(paginaPrincipal)
    }
}