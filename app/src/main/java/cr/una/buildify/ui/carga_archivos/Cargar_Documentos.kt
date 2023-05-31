package cr.una.buildify.carga_archivos


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import cr.una.buildify.R
import java.io.ByteArrayOutputStream
import java.io.File



lateinit var cd_documentos: CardView
lateinit var btn_back_doc: Button
lateinit var btn_save_doc: Button
lateinit var tv_documento_cargado:TextView
lateinit var tv_archivo:TextView
lateinit var et_filename_doc: EditText
private lateinit var pdfUri: Uri
private lateinit var storage_doc: FirebaseStorage

// Define un código de solicitud para el Intent de selección de archivo
val PICK_PDF_REQUEST_DOC = 1

@Suppress("DEPRECATION")
class Cargar_Documentos : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_documentos)


            cd_documentos = findViewById(R.id.cd_documentos)
            btn_save_doc = findViewById(R.id.btn_save_doc)
            tv_documento_cargado = findViewById(R.id.tv_documento_cargado)
            tv_archivo = findViewById(R.id.tv_archivo)

            btn_back_doc = findViewById(R.id.btn_back_doc)
            btn_back_doc.setOnClickListener {
                volver()
            }

            storage_doc = FirebaseStorage.getInstance()
            et_filename_doc = findViewById(R.id.name_document_documentos)

            btn_save_doc.setOnClickListener {
                val text = et_filename_doc.text.toString()
                if (text.isEmpty()) {
                    Toast.makeText(this, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
                } else {
                    // Llamar a la función savePdf() pasando pdfUri
                    if (pdfUri != null) {
                        savePdf(pdfUri!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                    } else {
                        Toast.makeText(this, "Selecciona un archivo PDF", Toast.LENGTH_SHORT).show()
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

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando PDF...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val uploadTask: UploadTask = storageRef.putFile(pdfUri)

        uploadTask
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "PDF cargado exitosamente", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this, "PDF y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }
            }

            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Log.e("Cargar_Documentos", "Error al cargar el PDF: $exception")
                Toast.makeText(this, "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
            }
    }

    private fun volver(){
        val volver = Intent(this, Cargar_Archivos::class.java)
        startActivity(volver)
    }
}
