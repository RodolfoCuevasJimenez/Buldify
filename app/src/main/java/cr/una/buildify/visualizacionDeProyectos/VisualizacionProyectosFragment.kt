package cr.una.buildify.visualizacionDeProyectos

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import cr.una.buildify.creacionProyecto.Proyecto
import cr.una.buildify.databinding.FragmentVisualizacionProyectosBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class VisualizacionProyectosFragment : Fragment() {

    private var _binding: FragmentVisualizacionProyectosBinding? = null
    private lateinit var rvProyectos: RecyclerView
    private lateinit var proyectosAdapter: ProyectosAdapter
    private lateinit var db: FirebaseFirestore
    private var proyectosList: MutableList<Proyecto> = mutableListOf()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[DirectorProyectoMainViewModel::class.java]

        _binding = FragmentVisualizacionProyectosBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = activity?.intent!!.getStringExtra("Email")
        val tipo = activity?.intent!!.getStringExtra("Tipo")

        // Inicialización de vistas y asignación de listeners
        val etBusqueda: EditText = binding.etBusqueda
        val btnBusqueda: Button = binding.btnBusqueda
        rvProyectos = binding.rvProyectos
        rvProyectos.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()

        // Filtrar servicios al cargar la vista
        if (id != null && tipo != null) {
            filtrarProyectos("", tipo ,id)
        }

        btnBusqueda.setOnClickListener{
            val busqueda = etBusqueda.text.toString().trim().lowercase()

            // Filtrar proyectos al hacer clic en el botón de búsqueda
            if (id != null && tipo != null) {
                filtrarProyectos(busqueda, tipo ,id)
            }
        }
    }

    private fun filtrarProyectos(busqueda: String, tipo: String, id:String) {
        // Consulta a Firestore para obtener los proyectos filtrados por el ID del director
        var proyectosRef: Query? = null
        when(tipo) {
            "Director de Proyecto" -> proyectosRef = db.collection("Proyectos").whereEqualTo("idDirector", id)
            "Dueño de la Obra" -> proyectosRef = db.collection("Proyectos").whereEqualTo("idCliente", id)
        }


        proyectosRef?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            proyectosList = mutableListOf()
            for (doc in snapshot?.documents ?: emptyList()) {
                // Obtener el objeto Proyecto a partir de los documentos obtenidos
                val proyecto = doc.toObject(Proyecto::class.java)

                // Verificar si el proyecto no es nulo y cumple con el filtro de búsqueda
                if (proyecto != null && proyecto.nombre.lowercase().trim().contains(busqueda, ignoreCase = true) && (proyecto.idDirector.contains(id, ignoreCase = true) || proyecto.idCliente.contains(id, ignoreCase = true))) {
                    proyecto.id = doc.id
                    proyectosList.add(proyecto)
                }
            }

            // Asignar la lista de proyectos al adaptador y mostrarlos en el RecyclerView
            proyectosAdapter = ProyectosAdapter(proyectosList, tipo, db)
            rvProyectos.adapter = proyectosAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
