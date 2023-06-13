package cr.una.buildify.visualizacionInc_eva_obs

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.databinding.FragmentVisualizacionIncEvaObsBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class Visualizacion_inc_eva_obs_Fragment : Fragment() {
    //Inicializa y declara varias variables para su uso en la visualización de datos.
    private var _binding: FragmentVisualizacionIncEvaObsBinding? = null
    private lateinit var recyclerViewIncEvaObs: RecyclerView
    private lateinit var visualizacionAdapter: VisualizacionAdapter
    private lateinit var db: FirebaseFirestore
    private var visualizacionList: MutableList<Visualizacion> = mutableListOf()

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

        _binding = FragmentVisualizacionIncEvaObsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewIncEvaObs = binding.recyclerViewIncEvaObs
        recyclerViewIncEvaObs.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()
        val visualizacionRef = db.collection("Visualizacion")

        visualizacionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //Log.w(TAG, "Error al obtener la visualizacion", e)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}