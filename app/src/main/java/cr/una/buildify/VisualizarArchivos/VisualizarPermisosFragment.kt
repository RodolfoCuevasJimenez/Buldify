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
import androidx.navigation.Navigation

class VisualizarPermisosFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var permisosAdapter: PermisosAdapter
    private lateinit var db: FirebaseFirestore
    private var permisosDocumentos: MutableList<Permisos_Detalle> = mutableListOf()
    private lateinit var btn_next: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(
            R.layout.fragment_visualizar_permisos,
            container,
            false
        )

        // Obtener una referencia al RecyclerView
        recyclerView = view.findViewById(R.id.recycle_permisos)

        // Crear una instancia del adaptador de permisos
        permisosAdapter = PermisosAdapter(permisosDocumentos)

        // Configurar el administrador de diseño del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener una referencia al Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference

        // Obtener una instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        // Obtener los documentos de la colección "Carga_Documentos_Documentos"
        db.collection("Carga_Documentos_Documentos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Crear una lista mutable de objetos Permisos_Detalle
                val permisosDocumentos = mutableListOf<Permisos_Detalle>()

                // Recorrer los documentos obtenidos de la consulta
                for (document in querySnapshot) {
                    val nombre = document.getString("nombre")

                    if (nombre != null) {
                        // Crear un objeto Permisos_Detalle y agregarlo a la lista
                        val planosDocumento = Permisos_Detalle(nombre)
                        permisosDocumentos.add(planosDocumento)
                    }
                }

                // Configurar los datos en el adaptador de permisos
                permisosAdapter.setData(permisosDocumentos)

                // Establecer el adaptador en el RecyclerView
                recyclerView.adapter = permisosAdapter
            }
            .addOnFailureListener { exception ->
                // Manejar la falla en la obtención de los datos
                Toast.makeText(requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show()            }

        return view
    }
}