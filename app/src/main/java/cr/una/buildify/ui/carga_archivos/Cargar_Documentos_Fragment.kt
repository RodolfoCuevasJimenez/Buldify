package cr.una.buildify.ui.carga_archivos

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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarDocumentosBinding
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

lateinit var cd_documentos: CardView
lateinit var btn_back_doc: Button
lateinit var btn_save_doc: Button
lateinit var tv_documento_cargado: TextView
lateinit var tv_archivo: TextView
lateinit var et_filename_doc: EditText
private lateinit var pdfUri: Uri
private lateinit var storage_doc: FirebaseStorage

// Define un código de solicitud para el Intent de selección de archivo
val PICK_PDF_REQUEST_DOC = 1

class Cargar_Documentos_Fragment : Fragment() {

    private var _binding: FragmentCargarDocumentosBinding? = null

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

        _binding = FragmentCargarDocumentosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cd_documentos = binding.cdDocumentos
        btn_save_doc = binding.btnSaveDoc
        tv_documento_cargado = binding.tvDocumentoCargado
        tv_archivo = binding.tvArchivo

        btn_back_doc = binding.btnBackDoc
        btn_back_doc.setOnClickListener {
            volver(view)
        }

        storage_doc = FirebaseStorage.getInstance()
        et_filename_doc = binding.nameDocumentDocumentos

       btn_save_doc.setOnClickListener {
            val text = et_filename_doc.text.toString()
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
        cd_documentos.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            startActivityForResult(intent, PICK_PDF_REQUEST_DOC)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                pdfUri = uri // Asignar el valor de uri a pdfUri
                tv_archivo.visibility = View.GONE
                tv_documento_cargado.visibility = View.VISIBLE
            }
        }
    }

    private fun savePdf(pdfUri: Uri, text: String) {
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )
        val fileName = "$text.pdf"
        val storageRef: StorageReference = storage_doc.reference.child("Permisos Usuarios/$fileName")

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
                db.collection("Carga_Documentos_Documentos")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(activity, "PDF y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }
            }

            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Log.e("Cargar_Documentos", "Error al cargar el PDF: $exception")
                Toast.makeText(activity, "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
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