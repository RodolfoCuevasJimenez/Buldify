package cr.una.buildify.director_proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.CrearProyecto
import cr.una.buildify.visualizacionDeProyectos.VisualizacionDeProyectos

enum class ProviderType{
    BASIC,
    GOOGLE
}

lateinit var btnCrearProyecto: CardView
lateinit var btnVisualizarProyectos: CardView

class Director_Proyecto_Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_proyecto_home)

        btnCrearProyecto = findViewById(R.id.btnCrearProyecto)
        btnVisualizarProyectos = findViewById(R.id.cvVisualizarProyectos)

        btnCrearProyecto.setOnClickListener {
            val uid = intent.getStringExtra("UID")
            val intent = Intent(this, CrearProyecto::class.java).apply {

                putExtra("UID", uid)
            }
            startActivity(intent)
            finish()
        }

        btnVisualizarProyectos.setOnClickListener {
            val uid = intent.getStringExtra("UID")
            val intent = Intent(this, VisualizacionDeProyectos::class.java).apply {

                putExtra("UID", uid)
            }
            startActivity(intent)
            finish()
        }
    }
}