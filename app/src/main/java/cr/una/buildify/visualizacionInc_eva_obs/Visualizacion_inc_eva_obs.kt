package cr.una.buildify.visualizacionInc_eva_obs

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R

class Visualizacion_inc_eva_obs : AppCompatActivity() {
    private lateinit var recyclerViewIncEvaObs: RecyclerView
    private lateinit var visualizacionAdapter: VisualizacionAdapter
    private lateinit var db: FirebaseFirestore
    private var visualizacionList: MutableList<Visualizacion> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizacion_inc_eva_obs)
        recyclerViewIncEvaObs = findViewById(R.id.recyclerViewIncEvaObs)
        recyclerViewIncEvaObs.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        val visualizacionRef = db.collection("Visualizacion")

        visualizacionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Error al obtener la visualizacion", e)
                return@addSnapshotListener
            }

            visualizacionList.clear()
            for (doc in snapshot?.documents ?: emptyList()) {
                val visualizacion = doc.toObject(Visualizacion::class.java)
                if (visualizacion != null) {
                    visualizacionList.add(visualizacion)
                }
            }

            visualizacionAdapter = VisualizacionAdapter(visualizacionList)
            recyclerViewIncEvaObs.adapter = visualizacionAdapter
        }
    }
}
