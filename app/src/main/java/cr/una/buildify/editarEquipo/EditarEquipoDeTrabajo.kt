package cr.una.buildify.editarEquipo

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
import cr.una.buildify.creacionProyecto.Trabajador

class EditarEquipoDeTrabajo : AppCompatActivity() {

    private lateinit var rvProyectos: RecyclerView
    private lateinit var trabajadoresAdapter: TrabajadoresAdapter
    private lateinit var db: FirebaseFirestore
    private var trabajadoresList: MutableList<Trabajador> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_equipo)
        val uidProyecto = intent.getStringExtra("UIDPROYECTO")


        val etBusqueda: EditText = findViewById(R.id.etBusqueda)
        val btnBusqueda: Button = findViewById(R.id.btnBusqueda)
        rvProyectos = findViewById(R.id.rvProyectos)
        rvProyectos.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        if (uidProyecto != null) {
            filterServices("", uidProyecto)
        }
        btnBusqueda.setOnClickListener {
            val busqueda = etBusqueda.text.toString().trim().lowercase()
            if (uidProyecto != null) {
                filterServices(busqueda, uidProyecto)
            }
        }
    }

    private fun filterServices(busqueda: String, uidProyecto: String) {
        val trabajadoresRef = db.collection("Proyectos").document(uidProyecto)
        trabajadoresRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.exists()) {
                    val data = snapshot.data
                    val matrizTrabajadores = data?.get("equipo") as? List<HashMap<String, String>>
                    trabajadoresList.clear()
                    var i = 0
                    matrizTrabajadores?.forEach() { fila ->
                        val id = i
                        val nombre = fila["nombre"]!!
                        val rol = fila["rol"]!!
                        val correo = fila["correo"]!!
                        val trabajador = Trabajador(id, nombre, rol, correo)
                        if (trabajador.nombre.contains(busqueda, ignoreCase = true)) {
                            trabajadoresList.add(trabajador)
                            i++
                        }
                    }
                }
                trabajadoresAdapter = TrabajadoresAdapter(trabajadoresList, this, uidProyecto)
                rvProyectos.adapter = trabajadoresAdapter
            }
        }
    }
}