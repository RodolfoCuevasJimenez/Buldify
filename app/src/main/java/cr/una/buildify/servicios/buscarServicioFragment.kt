package cr.una.buildify.servicios

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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.carga_archivos.btn_Progreso
import cr.una.buildify.databinding.FragmentBuscarServicioBinding
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
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
        var btnBusqueda: Button
        var etBusqueda: EditText
        etBusqueda = binding.etBusqueda
        btnBusqueda = binding.btnBusqueda
        recyclerViewServicios = binding.rvServicios
        recyclerViewServicios.layoutManager = LinearLayoutManager(activity)

        db = FirebaseFirestore.getInstance()

        filterServices("")
        btnBusqueda.setOnClickListener{
            var tipo = etBusqueda.text.toString().trim().lowercase()
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
                    serviciosList.add(servicio)
                }
            }

            serviciosAdapter = ServiciosAdapter(serviciosList)
            recyclerViewServicios.adapter = serviciosAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}