package cr.una.buildify.ui.carga_archivos

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import cr.una.buildify.databinding.FragmentCargarProgresoBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.navigation.Navigation
import cr.una.buildify.R


lateinit var cd_progreso: CardView
lateinit var et_filename_pr: EditText
lateinit var btn_back_pr: Button
lateinit var tv_archivo_pr: TextView
lateinit var progreso: ImageView
lateinit var btn_save_pr: Button

lateinit var iv_select: ImageView
lateinit var vv_select: VideoView
private lateinit var storage_pr: FirebaseStorage
private lateinit var uriProgreso: Uri
private lateinit var currentPhotoUri: Uri
private var fileExtension: String = "jpg"

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_PICK_IMAGE = 2
private const val REQUEST_PICK_VIDEO = 3


class Cargar_Progreso_Fragment : Fragment() {

    private var _binding: FragmentCargarProgresoBinding? = null

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

        _binding = FragmentCargarProgresoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_back_pr = binding.btnBackPr
        btn_back_pr.setOnClickListener {
            volver(view)
        }

        storage_pr = FirebaseStorage.getInstance()

        cd_progreso = binding.cdProgreso
        progreso = binding.progreso
        iv_select = binding.ivSelect
        vv_select = binding.vvSelect

        cd_progreso.setOnClickListener {
            ///render.visibility = View.VISIBLE
            Log.i("PRUEBA","VISIBLE BOTON")
            openMediaChooser()
        }

        btn_save_pr = binding.btnSavePr
        et_filename_pr = binding.etFilenamePr
        btn_save_pr.setOnClickListener {
            val text = et_filename_pr.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (uriProgreso != null) {
                    uploadImageToFirebaseStorage(uriProgreso!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(activity, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openMediaChooser() {
        val options = arrayOf<CharSequence>( "Elegir Foto de Galería", "Elegir Video de Galería", "Cancelar")
        val builder = activity?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Selecccionar Evidencia")
        builder?.setItems(options) { dialog, item ->
            when {
                options[item] == "Elegir Foto de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,
                        REQUEST_PICK_IMAGE
                    )
                }
                options[item] == "Elegir Video de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,
                        REQUEST_PICK_VIDEO
                    )
                }
                options[item] == "Cancelar" -> {
                    dialog.dismiss()
                }
            }
        }
        builder?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when{
            requestCode == REQUEST_PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && data != null-> {
                uriProgreso = data.data!!
                iv_select.setImageURI(uriProgreso)
                fileExtension = "jpg"
                Log.i("PRUEBA","IMAGE SELECT")
            }
            requestCode == REQUEST_PICK_VIDEO && resultCode == AppCompatActivity.RESULT_OK && data != null-> {
                uriProgreso = data.data!!

                // Muestra el video seleccionado en un VideoView
                vv_select.setVideoURI(uriProgreso)
                vv_select.start()
                fileExtension = "mp4"
                Log.i("PRUEBA","VIDEO CAPTURE")
            }
            else -> {
                // código para manejar otros casos, si es necesario
            }
        }
    }

    private fun uploadImageToFirebaseStorage(uriProgreso: Uri, fileName: String) {
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )
        val fileName = "$fileName.${fileExtension}"
        val storageRef: StorageReference = storage_pr.reference.child("Evidencia Usuarios/$fileName")

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando evidencia...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(uriProgreso)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Evidencia cargada exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to fileName,
                    "ruta" to uriProgreso.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Progreso")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Evidencia y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }
    }

    private fun volver(view: View?){
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Archivos_Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}