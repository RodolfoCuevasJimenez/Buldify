package cr.una.buildify.solicitud_Detalle

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentVisualizacionSolicitudDetalleBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class VisualizacionSolicitudDetalleFragment : Fragment() {

    private var _binding: FragmentVisualizacionSolicitudDetalleBinding? = null

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

        _binding = FragmentVisualizacionSolicitudDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var db: FirebaseFirestore


        var datos =""
        var serviciosList: MutableList<solicitudDetalle> = mutableListOf()

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD


        db.collection("Solicitud_Detalle")
            .get()
            .addOnSuccessListener {
                    resultado ->
                for (documento in resultado){
                    datos = "${documento.id}:${documento.data}\n "
                    val tabla = documento.toObject(solicitudDetalle::class.java)
                    if (tabla != null) {
                        serviciosList.add(tabla)

//RECYCLER VIEW
                        val recyclerView = binding.recyclerView
                        val adapter = solicitudDetalleAdapter(serviciosList)
                        recyclerView.adapter = adapter

                        recyclerView.layoutManager = LinearLayoutManager(requireContext())

                        val itemSpacing = resources.getDimensionPixelSize(R.dimen.item_spacing)

// Crea una instancia de RecyclerViewItemDecoration y AGREGAMOS RecyclerView
                        val itemDecoration = RecyclerViewItemDecoration(itemSpacing)
                        recyclerView.addItemDecoration(itemDecoration)



                    }
                }
            }

            .addOnFailureListener{exception ->
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}