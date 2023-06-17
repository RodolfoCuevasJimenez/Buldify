package cr.una.buildify.servicios


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.databinding.FragmentBuscarServicioBinding
import cr.una.buildify.servicio.ServiciosAdapter
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class buscarServicioFragment : Fragment() {

    private var _binding: FragmentBuscarServicioBinding? = null
    private lateinit var recyclerViewServicios: RecyclerView
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var db: FirebaseFirestore
    private var serviciosList: MutableList<Servicio> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentBuscarServicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBusqueda: Button
        val etBusqueda: EditText
        etBusqueda = binding.etBusqueda
        btnBusqueda = binding.btnBusqueda
        recyclerViewServicios = binding.rvServicios
        recyclerViewServicios.layoutManager = LinearLayoutManager(activity)

        db = FirebaseFirestore.getInstance()

        filterServices("")
        btnBusqueda.setOnClickListener{
            val tipo = etBusqueda.text.toString().trim().lowercase()
            filterServices(tipo)
        }

    }
    private fun filterServices(tipo: String) {
        val serviciosRef = db.collection("Servicios")
        serviciosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            serviciosList = mutableListOf<Servicio>()
            for (doc in snapshot?.documents ?: emptyList()) {
                val servicio = doc.toObject(Servicio::class.java)
                if (servicio != null && servicio.tipo.contains(tipo, ignoreCase = true)) {
                    servicio.id=doc.id
                    serviciosList.add(servicio)
                }
            }
            // Obtención de los valores de "Tipo" y "Email" desde el intent de la actividad
            val tipo=activity?.intent?.getStringExtra("Tipo")?:""
            val idUsuario=activity?.intent?.getStringExtra("Email")?:""
            // Creación del adaptador de servicios y configuración en el RecyclerView
            serviciosAdapter = ServiciosAdapter(serviciosList, db, tipo, idUsuario)
            recyclerViewServicios.adapter = serviciosAdapter
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}