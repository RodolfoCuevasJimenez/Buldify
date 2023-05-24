package cr.una.buildify.visualizacionDeProyectos

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Proyecto

class VisualizacionDeProyectos : AppCompatActivity() {

    private lateinit var rvProyectos: RecyclerView
    private lateinit var proyectosAdapter: ProyectosAdapter
    private lateinit var db: FirebaseFirestore
    private var proyectosList: MutableList<Proyecto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizacion_de_proyectos)
        val uid = intent.getStringExtra("UID")


        val etBusqueda: EditText = findViewById(R.id.etBusqueda)
        val btnBusqueda: Button = findViewById(R.id.btnBusqueda)
        rvProyectos = findViewById(R.id.rvProyectos)
        rvProyectos.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        if (uid != null) {
            filterServices("", uid)
        }
        btnBusqueda.setOnClickListener{
            val busqueda = etBusqueda.text.toString().trim().lowercase()
            if (uid != null) {
                filterServices(busqueda, uid)
            }
        }
    }

    private fun filterServices(busqueda: String, uid:String) {
        val proyectosRef = db.collection("Proyectos").whereEqualTo("idDirector", uid)
        proyectosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            proyectosList = mutableListOf<Proyecto>()
            for (doc in snapshot?.documents ?: emptyList()) {

                val proyecto = doc.toObject(Proyecto::class.java)
                if (proyecto != null && proyecto.nombre.contains(busqueda, ignoreCase = true) && proyecto.idDirector.contains(uid, ignoreCase = true)) {
                    proyecto.id = doc.id
                    proyectosList.add(proyecto)
                }
            }

            proyectosAdapter = ProyectosAdapter(proyectosList)
            rvProyectos.adapter = proyectosAdapter
        }
    }
}