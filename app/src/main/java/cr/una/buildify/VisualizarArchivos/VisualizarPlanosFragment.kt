package cr.una.buildify.ui.VisualizarArchivos

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import cr.una.buildify.R
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.databinding.FragmentCargarArchivosBinding

class VisualizarPlanosFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var planosAdapter: PlanosAdapter
    private lateinit var db: FirebaseFirestore
    private var planosDocumentos: MutableList<Planos_Detalle> = mutableListOf()
    private lateinit var btn_next: Button
    private var _binding: FragmentCargarArchivosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_visualizar_planos,
            container,
            false
        )

        //Boton en el menu, redirige a renders visualizar
        btn_next = view.findViewById(R.id.next)
        btn_next.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.visualizarRendersFragment)
        }

        recyclerView = view.findViewById(R.id.recycle_planos)
        planosAdapter = PlanosAdapter(planosDocumentos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        // Obtener los documentos de la colección "Carga_Documentos_Planos"
        db.collection("Carga_Documentos_Planos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val planosDocumentos = mutableListOf<Planos_Detalle>()
                for (document in querySnapshot) {
                    val nombre = document.getString("nombre")

                    if (nombre != null) {
                        val planosDocumento = Planos_Detalle(nombre)
                        planosDocumentos.add(planosDocumento)
                    }
                }
                // Configurar los datos en el adaptador de planos
                planosAdapter.setData(planosDocumentos)

                // Establecer el adaptador en el RecyclerView
                recyclerView.adapter = planosAdapter
            }
            .addOnFailureListener { exception ->
                // Manejar la falla en la obtención de los datos
                Toast.makeText(requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT)
                    .show()
            }

        return view
    }
}