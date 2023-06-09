package cr.una.buildify.ui.carga_archivos

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarRecorridosBinding
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

private lateinit var btn_back: Button
private lateinit var btn_previuos: ImageView
private lateinit var videoView: VideoView
private lateinit var storage_rec: FirebaseStorage
private lateinit var videoUri: Uri
private val PICK_VIDEO_REQUEST_CODE = 2
private lateinit var btn_save: Button
private lateinit var et_filename_rec: EditText

class Cargar_Recorridos_Fragment : Fragment() {

    private var _binding: FragmentCargarRecorridosBinding? = null

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

        _binding = FragmentCargarRecorridosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_previuos = binding.btnPrevious
        btn_previuos.setOnClickListener {
            navigatePrevious(view)
        }

        btn_back = binding.back
        btn_back.setOnClickListener {
            navigateBack(view)
        }

        storage_rec = FirebaseStorage.getInstance()

        btn_save = binding.save
        videoView = binding.videoView
        et_filename_rec = binding.nameDocumentRecorrido
        videoView.visibility = View.GONE

        val btn_cargar_archivo = binding.btnCargarArchivos
        btn_cargar_archivo.setOnClickListener {
            openVideoChooser()
        }

        val btn_save = binding.save
        btn_save.setOnClickListener {
            val text = et_filename_rec.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (videoUri != null) {
                    uploadVideoToFirebaseStorage(videoUri!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(activity, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadVideoToFirebaseStorage(videoUri: Uri, fileName: String) {
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )

        val fileName = "$fileName.mp4"
        val storageRef: StorageReference = storage_rec.reference.child("Recorridos Usuarios/$fileName")

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando video...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(videoUri)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                videoView.setVideoURI(videoUri)
                videoView.start()
                Toast.makeText(activity, "Video cargado exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to fileName,
                    "ruta" to videoUri.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Recorridos")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Video y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }

    }
    private fun openVideoChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_VIDEO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            videoUri = data.data!!
            videoView.setVideoURI(videoUri)
            videoView.visibility = View.VISIBLE
            videoView.start()
        }
    }

    private fun navigateBack(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Archivos_Fragment)
        }

    }

    private fun navigatePrevious(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Renders_Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}