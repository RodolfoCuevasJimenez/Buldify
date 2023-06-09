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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarRendersBinding
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

private lateinit var btn_save: Button
lateinit var btn_next_re: ImageView
lateinit var btn_previous_re: ImageView
lateinit var btn_back_re: Button
private lateinit var et_filename_ren: EditText
private lateinit var render: ImageView
private lateinit var storage_ren: FirebaseStorage
private lateinit var uriImage: Uri
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

        btn_previous_re = binding.btnPreviousRe
        btn_previous_re.setOnClickListener {
            navegarAnterior(view)
        }

        btn_back_re = binding.back
        btn_back_re.setOnClickListener {
            volver(view)
        }

        storage_ren = FirebaseStorage.getInstance()

        render = binding.imageViewCargar
        btn_save = binding.save
        et_filename_ren = binding.etFilename

        render.visibility = View.GONE

        val btn_cargar_archivo = binding.btnCargarArchivoRe
        btn_cargar_archivo.setOnClickListener {
            openMediaChooser()
        }

        btn_save.setOnClickListener {
            val text = et_filename_ren.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (uriImage != null) {
                    uploadImageToFirebaseStorage(uriImage!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(activity, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )

        val fileName = "$fileName.jpg"
        val storageRef: StorageReference = storage_ren.reference.child("Renders Usuarios/$fileName")

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando imagen...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Imagen cargada exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to fileName,
                    "ruta" to uriImage.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Renders")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "Imagen y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }

    }


    private fun navegarAnterior(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Planos_Fragment)
        }
    }

    private fun navegarSiguiente(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Recorridos_Fragment)
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