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
/*Declaraciones*/
        lateinit var db: FirebaseFirestore
        var datos = ""
        var serviciosList: MutableList<solicitudDetalle> = mutableListOf()
        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD

/*Extraemos los datos que se encuentran en la coleccion "Solicitud_Detalle" con el GET()*/
        db.collection("Solicitud_Detalle")
            .get()
            .addOnSuccessListener { resultado ->
                for (documento in resultado) {  /*Utilizamos el For para recorrer los datos BD*/
                    datos =
                        "${documento.id}:${documento.data}\n "     /*Los datos que extraemos lo asignamos a la variable datos*/
                    val tabla =
                        documento.toObject(solicitudDetalle::class.java)   /*Los datos lo convertimos a objeto "solicitudDetalle"*/
                    if (tabla != null) {
                        serviciosList.add(tabla)  /*Agregamos los objetos a la lista*/

//RECYCLER VIEW
                        val recyclerView = binding.recyclerView
                        /*Se crea una instancia del adaptador solicitudDetalleAdapter y se pasa una lista de serviciosList
                                                                    como parámetro al constructor del adaptador. La lista de elementos que se mostrarán en el RecyclerView.*/

                        val adapter = solicitudDetalleAdapter(serviciosList)

/*Se asigna el adaptador creado al RecyclerView. Esto establece el adaptador como responsable de proporcionar los datos y las vistas
 para cada elemento en el RecyclerView*/
                        recyclerView.adapter = adapter
/*organizar los elementos del RecyclerView en una lista vertical, uno debajo del otro. */
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())

// Crea una instancia de RecyclerViewItemDecoration y AGREGAMOS RecyclerView para agregar un espacio entre los item
                        val itemSpacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
                        val itemDecoration = RecyclerViewItemDecoration(itemSpacing)
                        recyclerView.addItemDecoration(itemDecoration)


                    }
                }
            }

            .addOnFailureListener { exception ->
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}