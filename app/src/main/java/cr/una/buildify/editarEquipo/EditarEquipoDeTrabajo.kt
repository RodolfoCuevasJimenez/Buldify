package cr.una.buildify.editarEquipo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Trabajador

@Suppress("UNCHECKED_CAST")
class EditarEquipoDeTrabajo : AppCompatActivity() {

    private lateinit var rvProyectos: RecyclerView
    private lateinit var trabajadoresAdapter: TrabajadoresAdapter
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var db: FirebaseFirestore
    private var trabajadoresList: MutableList<Trabajador> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_equipo)
        val uidProyecto = intent.getStringExtra("UIDPROYECTO")!!

        // Inicializar vistas
        val etBusqueda: EditText = findViewById(R.id.etBusqueda)
        val btnBusqueda: Button = findViewById(R.id.btnBusqueda)
        fabAgregar = findViewById(R.id.fabAgregar)
        rvProyectos = findViewById(R.id.rvProyectos)
        rvProyectos.layoutManager = LinearLayoutManager(this)

        // Inicializar base de datos Firestore
        db = FirebaseFirestore.getInstance()

        // Filtrar y mostrar el equipo inicial
        filtrarEquipo("", uidProyecto)

        // Configurar el botón de búsqueda
        btnBusqueda.setOnClickListener {
            val busqueda = etBusqueda.text.toString().trim().lowercase()
            filtrarEquipo(busqueda, uidProyecto)
        }

        // Configurar el botón de agregar trabajador
        fabAgregar.setOnClickListener {
            intent = Intent(this, FormularioTrabajador::class.java).apply {
                putExtra("Tipo", "Agregar")
                putExtra("uidProyecto", uidProyecto)
            }
            startActivity(intent)
        }
    }

    private fun filtrarEquipo(busqueda: String, uidProyecto: String) {
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
                    matrizTrabajadores?.forEach { fila ->
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
                // Crear y asignar el adaptador de trabajadores al RecyclerView
                trabajadoresAdapter = TrabajadoresAdapter(trabajadoresList, this, uidProyecto)
                rvProyectos.adapter = trabajadoresAdapter
            }
        }
    }
}
