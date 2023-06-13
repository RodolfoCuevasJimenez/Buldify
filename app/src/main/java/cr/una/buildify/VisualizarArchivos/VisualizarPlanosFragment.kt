package cr.una.buildify.ui.VisualizarArchivos

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import cr.una.buildify.R
import com.bumptech.glide.request.target.Target
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentCargarProgresoBinding
import cr.una.buildify.databinding.FragmentVisualizarPlanosBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class VisualizarPlanosFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var planosAdapter: PlanosAdapter
    private lateinit var db: FirebaseFirestore
    private var planosDocumentos: MutableList<Planos_Detalle> = mutableListOf()

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