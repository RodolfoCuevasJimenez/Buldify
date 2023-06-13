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
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentVisualizacionProyectosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = activity?.intent!!.getStringExtra("UID")

        // Inicialización de vistas y asignación de listeners
        val etBusqueda: EditText = binding.etBusqueda
        val btnBusqueda: Button = binding.btnBusqueda
        rvProyectos = binding.rvProyectos
        rvProyectos.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()

        // Filtrar servicios al cargar la vista
        if (uid != null) {
            filtrarProyectos("", uid)
        }

        btnBusqueda.setOnClickListener{
            val busqueda = etBusqueda.text.toString().trim().lowercase()

            // Filtrar proyectos al hacer clic en el botón de búsqueda
            if (uid != null) {
                filtrarProyectos(busqueda, uid)
            }
        }
    }

    private fun filtrarProyectos(busqueda: String, uid:String) {
        // Consulta a Firestore para obtener los proyectos filtrados por el ID del director
        val proyectosRef = db.collection("Proyectos").whereEqualTo("idDirector", uid)
        proyectosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            proyectosList = mutableListOf<Proyecto>()
            for (doc in snapshot?.documents ?: emptyList()) {
                // Obtener el objeto Proyecto a partir de los documentos obtenidos
                val proyecto = doc.toObject(Proyecto::class.java)

                // Verificar si el proyecto no es nulo y cumple con el filtro de búsqueda
                if (proyecto != null && proyecto.nombre.contains(busqueda, ignoreCase = true) && proyecto.idDirector.contains(uid, ignoreCase = true)) {
                    proyecto.id = doc.id
                    proyectosList.add(proyecto)
                }
            }

            // Asignar la lista de proyectos al adaptador y mostrarlos en el RecyclerView
            proyectosAdapter = ProyectosAdapter(proyectosList)
            rvProyectos.adapter = proyectosAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
