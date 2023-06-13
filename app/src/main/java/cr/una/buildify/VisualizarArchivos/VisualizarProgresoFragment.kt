package cr.una.buildify.ui.VisualizarArchivos

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
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

class VisualizarProgresoFragment : Fragment(), ProgresoAdapter.OnImageClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progresoAdapter: ProgresoAdapter
    private lateinit var db: FirebaseFirestore
    private var progresoDocumentos: MutableList<Progreso_Detalle> = mutableListOf()

     override fun onImageClick(imageUrl: String, tipo: String) {
        if(tipo == "mp4"){
            //Mostrar el video en un diálogo
            Log.i("tipo:",tipo)
            onVideoClick(imageUrl)
        }
         else{
         //Mostrar la imagen en un diálogo
            mostrarImagenEnDialogo(imageUrl)
        }
    }

    private fun mostrarImagenEnDialogo(imageUrl: String) {
        // Inflar la vista del diálogo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_view, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.iv_dialog_image)

        // Cargar la imagen utilizando Glide en el ImageView del diálogo
        Glide.with(requireContext())
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Manejar el error al cargar la imagen
                    Log.e("Glide", "Error loading image: ${e?.message}")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(imageView)

        // Crear el diálogo personalizado
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Mostrar el diálogo
        dialog.show()
    }

    private fun onVideoClick(videoUrl: String) {
        // Inflar la vista del diálogo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_video_view, null)
        val videoView = dialogView.findViewById<VideoView>(R.id.player_view)

        // Crear el diálogo personalizado
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Obtener la URI del video a partir de la URL
        val videoUri = Uri.parse(videoUrl)

        // Configurar el VideoView con la URI y comenzar la reproducción
        videoView.setVideoURI(videoUri)
        videoView.start()

        // Mostrar el diálogo
        dialog.show()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(
            R.layout.fragment_visualizar_progreso,
            container,
            false
        )

        // Obtener la referencia al RecyclerView y crear el adaptador
        recyclerView = view.findViewById(R.id.recycle_progreso)
        progresoAdapter = ProgresoAdapter(progresoDocumentos)
        progresoAdapter.setOnImageClickListener(this)

        // Configurar el diseño del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener la referencia al Firebase Storage y Firestore
        val storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()

        // Obtener los datos de Firestore
        db.collection("Carga_Documentos_Progreso")
            .get()
            .addOnSuccessListener { querySnapshot ->
                progresoDocumentos.clear() // Limpiar los datos antiguos

                for (document in querySnapshot) {
                    val nombre = document.getString("nombre")
                    if (nombre != null) {
                        val progresoDocumento = Progreso_Detalle(nombre)
                        Log.i("aaa", nombre)

                        // Construir la referencia al archivo en Firebase Storage
                        val imageRef = storageReference.child("Evidencia Usuarios/$nombre")
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            progresoDocumento.ruta = imageUrl
                            progresoDocumentos.add(progresoDocumento)

                            // Verificar si se han obtenido todos los documentos
                            if (progresoDocumentos.size == querySnapshot.size()) {
                                progresoAdapter.setData(progresoDocumentos)
                                recyclerView.adapter = progresoAdapter
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

        // Configurar los datos iniciales en el adaptador y el RecyclerView
        progresoAdapter.setData(progresoDocumentos)
        recyclerView.adapter = progresoAdapter

        return view
    }
}
