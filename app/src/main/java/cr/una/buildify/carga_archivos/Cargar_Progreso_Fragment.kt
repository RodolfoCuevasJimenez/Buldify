package cr.una.buildify.carga_archivos

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
private var uriProgreso: Uri? = null
private var fileExtension: String = "jpg" //Selecciona la extension de la imagen en jpg
// Define un código de solicitud para el Intent de selección de archivo
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
        //Variables del layout
        cd_progreso = binding.cdProgreso
        progreso = binding.progreso
        iv_select = binding.ivSelect
        vv_select = binding.vvSelect

        //Boton de regresar al menu de cargar Archivos
        btn_back_pr = binding.btnBackPr
        btn_back_pr.setOnClickListener {
            volver(view)
        }
        //Crea instancia de Firebase Storage para obtener la media
        storage_pr = FirebaseStorage.getInstance()

        //Botón para abrir el selector de Media
        cd_progreso.setOnClickListener {
            openMediaChooser()
        }

        btn_save_pr = binding.btnSavePr
        et_filename_pr = binding.etFilenamePr
        //Boton guardar, guardar en Firebase la información del proyecto en Firebase Database y Storage la media
        btn_save_pr.setOnClickListener {
            val text = et_filename_pr.text.toString()
                if (text.isEmpty()) {
                    Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
                } else if (uriProgreso == null) {
                    Toast.makeText(activity, "Selecciona la Evidencia", Toast.LENGTH_SHORT).show()
                } else {
                    uploadImageToFirebaseStorage(uriProgreso!!, text)
                }
            }
        }

//Método para elegir el tipo de archivo a cargar, si es video o una fotografía
    private fun openMediaChooser() {
        val options = arrayOf<CharSequence>( "Elegir Foto de Galería", "Elegir Video de Galería", "Cancelar")
        val builder = activity?.let { AlertDialog.Builder(it) }//Muestra un dialog con la opcion de elgir una foto o un video
        builder?.setTitle("Selecccionar Evidencia")
        builder?.setItems(options) { dialog, item ->
            when {
                options[item] == "Elegir Foto de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,
                        REQUEST_PICK_IMAGE
                    )//Seleccionar una fotografía de galería
                }
                options[item] == "Elegir Video de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,
                        REQUEST_PICK_VIDEO
                    )//seleccionar un video de galería
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
            requestCode == REQUEST_PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && data != null-> { //Saber si seleccionaó la opción de cargar una imagen
                uriProgreso = data.data!!
                iv_select.setImageURI(uriProgreso) // Asignar el valor de uri a uriProgreso
                fileExtension = "jpg" //Extension de una imagen
                Log.i("PRUEBA","IMAGE SELECT")
            }
            requestCode == REQUEST_PICK_VIDEO && resultCode == AppCompatActivity.RESULT_OK && data != null-> { //Saber si seleccionaó la opción de cargar un video
                uriProgreso = data.data!!

                // Muestra el video seleccionado en un VideoView
                vv_select.setVideoURI(uriProgreso)
                vv_select.start()
                fileExtension = "mp4"
                Log.i("PRUEBA","VIDEO CAPTURE")
            }
            else -> {
                Log.e("Seleccionar ", "Error al seleccionar una opción ");
            }
        }
    }

    private fun uploadImageToFirebaseStorage(uriProgreso: Uri, fileName: String) {
        //Extra de BD la información de donde se encuentre la imagen
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )
        val fileName = "$fileName.${fileExtension}" //asignar la extension al archivo .jpg para fotos y .mp4 para videos
        val storageRef: StorageReference = storage_pr.reference.child("Evidencia Usuarios/$fileName")//Crea una referencia de Storage la Colección de donde se encuentran las evidencias

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando evidencia...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(uriProgreso)//Carga la evidencia a Storage
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()//Mensaje para manejar la evidencia cargada
                Toast.makeText(activity, "Evidencia cargada exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to fileName,
                    "ruta" to uriProgreso.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore Database
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Progreso")
                    .add(imageInfo)
                    .addOnSuccessListener {//Mensaje de que se guardo la información en Database
                        Toast.makeText(activity, "Evidencia y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()//Manejo de errores si existe un error
                Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }
    }

    private fun volver(view: View?){ //Método para regresar al menu Principal
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.director_Proyecto_Main)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}