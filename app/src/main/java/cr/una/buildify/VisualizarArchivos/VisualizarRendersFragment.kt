package cr.una.buildify.ui.VisualizarArchivos

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

class VisualizarRendersFragment : Fragment(), RenderAdapter.OnImageClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var renderAdapter: RenderAdapter
    private lateinit var db: FirebaseFirestore
    private var renderDocumentos: MutableList<Render_Detalle> = mutableListOf()

    override fun onImageClick(imageUrl: String) {
        // Mostrar la imagen en un diálogo
        mostrarImagenEnDialogo(imageUrl)
    }

    private fun mostrarImagenEnDialogo(imageUrl: String) {
        // Inflar el diseño del diálogo
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
                    // Manejar el error de carga de la imagen
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_visualizar_renders, container, false)

        // Obtener la referencia al RecyclerView
        recyclerView = view.findViewById(R.id.recycle_renders)

        // Crear el adaptador de Renders
        renderAdapter = RenderAdapter(renderDocumentos)

        // Establecer el listener de clic en imagen para el adaptador
        renderAdapter.setOnImageClickListener(this)

        // Configurar el LinearLayoutManager en el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtener la referencia al Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference

        // Obtener la referencia a Firestore
        db = FirebaseFirestore.getInstance()

        // Obtener la colección de documentos de Renders
        db.collection("Carga_Documentos_Renders")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val renderDocumentos = mutableListOf<Render_Detalle>()

                // Recorrer los documentos obtenidos de Firestore
                for (document in querySnapshot) {
                    val nombre = document.getString("nombre")
                    if (nombre != null) {
                        val renderDocumento = Render_Detalle(nombre)
                        Log.i("aaa", nombre)

                        // Construir la referencia al archivo en Firebase Storage
                        val imageRef = storageReference.child("Renders Usuarios/$nombre")

                        // Obtener la URL de descarga del archivo
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            renderDocumento.ruta = imageUrl
                            renderDocumentos.add(renderDocumento)

                            // Verificar si se han obtenido todos los documentos de Renders
                            if (renderDocumentos.size == querySnapshot.size()) {
                                renderAdapter.setData(renderDocumentos)
                                recyclerView.adapter = renderAdapter
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar la falla en la obtención de los datos
            }

        recyclerView.adapter = renderAdapter // Configurar el adaptador aquí

        return view
    }
}
