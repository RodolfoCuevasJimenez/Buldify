package cr.una.buildify.carga_archivos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarPlanosBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

lateinit var button_back_p: Button
lateinit var btn_next_p: ImageView
lateinit var btn_cargar_archivo: CardView
lateinit var archive: TextView
lateinit var cargado: TextView
private lateinit var btn_save: Button
private lateinit var et_filename: EditText
private lateinit var pdfUri: Uri
private lateinit var storage: FirebaseStorage
// Define un código de solicitud para el Intent de selección de archivo
val PICK_PDF_REQUEST = 1

class Cargar_Planos_Fragment : Fragment() {

    private var _binding: FragmentCargarPlanosBinding? = null

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

        _binding = FragmentCargarPlanosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        btn_next_p =  binding.btnNextP
        btn_next_p.setOnClickListener {
            navegarSiguiente(view)
        }

        button_back_p = binding.back
        button_back_p.setOnClickListener {
            volver(view)
        }

        storage = FirebaseStorage.getInstance()
        et_filename = binding.nameDocumentPlanos
        archive = binding.archive
        cargado = binding.cargado

        btn_save = binding.save
        btn_save.setOnClickListener {
            val text = et_filename.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (pdfUri != null) {
                    savePdf(pdfUri!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(activity, "Selecciona un archivo PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }


        val btn_cargar_archivo = binding.btnCargarArchivo
        btn_cargar_archivo.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            startActivityForResult(intent, PICK_PDF_REQUEST)
        }


        return root
    }

    private fun savePdf(pdfUri: Uri, text: String) {
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )
        val fileName = "$text.pdf"
        val storageRef: StorageReference = storage.reference.child("Planos Usuarios/$fileName")

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Cargando PDF...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val uploadTask: UploadTask = storageRef.putFile(pdfUri)

        uploadTask
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(activity, "PDF cargado exitosamente", Toast.LENGTH_SHORT).show()

                // Crear un objeto imageInfo con la información a guardar
                val imageInfo = hashMapOf(
                    "nombre" to text,
                    "ruta" to pdfUri.toString(),
                    "idProyecto" to "ID_DEL_PROYECTO"
                )

                // Guardar la información en Firestore
                val db = Firebase.firestore
                db.collection("Carga_Documentos_Planos")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "PDF y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }
            }

            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Log.e("Cargar_Planos", "Error al cargar el PDF: $exception")
                Toast.makeText(activity, "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                pdfUri = uri // Asignar el valor de uri a pdfUri
                archive.visibility = View.GONE
                cargado.visibility = View.VISIBLE
            }
        }
    }
    private fun navegarSiguiente(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Renders_Fragment)
        }
    }

    private fun volver(view: View?) {
        if (view != null) {
            Navigation.findNavController(view).navigate(R.id.cargar_Archivos_Fragment)
        }
    }


}