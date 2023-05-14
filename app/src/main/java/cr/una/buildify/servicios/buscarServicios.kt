package cr.una.buildify.servicios

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R

class BuscarServicios : AppCompatActivity() {

    private lateinit var recyclerViewServicios: RecyclerView
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var db: FirebaseFirestore
    private var serviciosList: MutableList<Servicio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_servicios)
        recyclerViewServicios = findViewById(R.id.recyclerServicios)
        recyclerViewServicios.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()

        val tipo = intent.getStringExtra("tipo") ?: ""
        filterServices(tipo)
    }
    private fun filterServices(tipo: String) {
        val serviciosRef = db.collection("Servicios")
        serviciosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            serviciosList = mutableListOf<Servicio>()
            for (doc in snapshot?.documents ?: emptyList()) {
                val servicio = doc.toObject(Servicio::class.java)
                if (servicio != null && servicio.tipo.contains(tipo, ignoreCase = true)) {
                    serviciosList.add(servicio)
                }
            }

            serviciosAdapter = ServiciosAdapter(serviciosList)
            recyclerViewServicios.adapter = serviciosAdapter
        }
    }
}


