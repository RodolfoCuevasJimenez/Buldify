package cr.una.buildify.carga_archivos

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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarRendersBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

private lateinit var btn_save: Button
lateinit var btn_next_re: ImageView
lateinit var btn_previous_re: ImageView
lateinit var btn_back_re: Button
private lateinit var et_filename_ren: EditText
private lateinit var render: ImageView
private lateinit var storage_ren: FirebaseStorage
private var uriImage: Uri? = null
// Define un código de solicitud para el Intent de selección de archivo
private val PICK_IMAGE_REQUEST_CODE = 1

class Cargar_Renders_Fragment : Fragment() {

    private var _binding: FragmentCargarRendersBinding? = null

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

        _binding = FragmentCargarRendersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_next_re = binding.btnNextRe
        btn_next_re.setOnClickListener {
            navegarSiguiente(view)
        }
        //Boton de navegar a Recorridos
        btn_previous_re = binding.btnPreviousRe
        btn_previous_re.setOnClickListener {
            navegarAnterior(view)
        }
        //Boton de regresar al menu de cargar Archivos
        btn_back_re = binding.backMenuRec
        btn_back_re.setOnClickListener {
            volver(view)
        }
        //Crea instancia de Firebase Storage para obtener la media
        storage_ren = FirebaseStorage.getInstance()

        //Variables del layout
        render = binding.imageViewCargar
        btn_save = binding.save
        et_filename_ren = binding.etFilename

        render.visibility = View.GONE

        //Botón para abrir el selector de Media
        val btn_cargar_archivo = binding.btnCargarArchivoRe
        btn_cargar_archivo.setOnClickListener {
            openMediaChooser()
        }

        //Boton guardar, guardar en Firebase la información del proyecto en Firebase Database y Storage la media
        btn_save.setOnClickListener {
            val text = et_filename_ren.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else if (uriImage == null) {
                Toast.makeText(activity, "Selecciona la Evidencia", Toast.LENGTH_SHORT).show()
            } else {
                uploadImageToFirebaseStorage(uriImage!!, text)
            }
        }
    }

    //Método para elegir el tipo de archivo a cargar, Imagen
    private fun openMediaChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            uriImage = data.data!!
            render.setImageURI(uriImage)
            render.visibility = View.VISIBLE
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, fileName: String) {
        //Extra de BD la información de donde se encuentre la imagen
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )

        val fileName = "$fileName.jpg" //asignar la extension al archivo .jpg para fotos
        val storageRef: StorageReference = storage_ren.reference.child("Renders Usuarios/$fileName")//Crea una referencia de Storage la Colección de donde se encuentran los renders

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando imagen...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(imageUri)//Carga la evidencia a Storage
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()//Mensaje para manejar la evidencia cargada
                Toast.makeText(activity, "Imagen cargada exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to fileName,
                    "ruta" to uriImage.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore Database
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Renders")
                    .add(imageInfo)
                    .addOnSuccessListener {//Mensaje de que se guardo la información en Database
                        Toast.makeText(activity, "Imagen y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()//Manejo de errores si existe un error
                Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }
    }


    private fun navegarAnterior(view: View?) { //Método para regresar a la pantalla anterior Planos
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Planos_Fragment)
        }
    }

    private fun navegarSiguiente(view: View?) { //Método para navegar a la pantalla de recorridos
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Recorridos_Fragment)
        }
    }

    private fun volver(view: View?){ //Método para regresar al menu Principal
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Archivos_Fragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}