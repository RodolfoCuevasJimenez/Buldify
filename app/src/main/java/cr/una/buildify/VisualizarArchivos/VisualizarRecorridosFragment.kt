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

class VisualizarRecorridosFragment : Fragment(), RecorridoAdapter.OnImageClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recorridoAdapter: RecorridoAdapter
    private lateinit var db: FirebaseFirestore
    private var recorridoDocumentos: MutableList<Recorrido_Detalle> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(
            R.layout.fragment_visualizar_recorridos,
            container,
            false
        )

        // Obtener la referencia al RecyclerView en el diseño del fragmento
        recyclerView = view.findViewById(R.id.recycle_recorrido)

        // Crear una instancia del adaptador de Recorrido
        recorridoAdapter = RecorridoAdapter(recorridoDocumentos)

        // Establecer el listener de clic en la imagen en el adaptador
        recorridoAdapter.setOnImageClickListener(this)

        // Establecer el administrador de diseño del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener la referencia al Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference

        // Obtener una instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        // Obtener la colección "Carga_Documentos_Recorridos" de Firestore
        db.collection("Carga_Documentos_Recorridos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Crear una lista mutable de Recorrido_Detalle
                val recorridoDocumentos = mutableListOf<Recorrido_Detalle>()

                // Iterar sobre los documentos obtenidos de la colección
                for (document in querySnapshot) {
                    // Obtener el nombre del documento
                    val nombre = document.getString("nombre")

                    // Verificar si el nombre no es nulo
                    if (nombre != null) {
                        // Crear una instancia de Recorrido_Detalle con el nombre obtenido
                        val recorridoDocumento = Recorrido_Detalle(nombre)

                        // Construir la referencia al archivo de video en Firebase Storage
                        val videoRef = storageReference.child("Recorridos Usuarios/$nombre")

                        // Obtener la URL de descarga del video
                        videoRef.downloadUrl.addOnSuccessListener { uri ->
                            val videoUrl = uri.toString()
                            recorridoDocumento.ruta = videoUrl

                            // Agregar el documento de Recorrido a la lista
                            recorridoDocumentos.add(recorridoDocumento)

                            // Verificar si se han obtenido todos los documentos
                            if (recorridoDocumentos.size == querySnapshot.size()) {
                                // Establecer los datos en el adaptador y configurar el adaptador en el RecyclerView
                                recorridoAdapter.setData(recorridoDocumentos)
                                recyclerView.adapter = recorridoAdapter
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar la falla en la obtención de los datos
                Toast.makeText(requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT)
                    .show()
            }

        // Configurar el adaptador en el RecyclerView
        recyclerView.adapter = recorridoAdapter

        return view
    }


    override fun onImageClick(videoUrl: String) {
        // Inflar el diseño del diálogo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_video_view, null)

        // Obtener la referencia al VideoView en el diseño del diálogo
        val videoView = dialogView.findViewById<VideoView>(R.id.player_view)

        // Crear el diálogo
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Convertir la URL del video en una Uri
        val videoUri = Uri.parse(videoUrl)

        // Establecer la Uri en el VideoView y comenzar a reproducir el video
        videoView.setVideoURI(videoUri)
        videoView.start()

        // Mostrar el diálogo
        dialog.show()
    }
}