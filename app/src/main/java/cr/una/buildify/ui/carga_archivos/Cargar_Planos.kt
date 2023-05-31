package cr.una.buildify.carga_archivos


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import cr.una.buildify.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


lateinit var button_back_p: Button
lateinit var btn_next_p: ImageView
lateinit var btn_cargar_archivo: CardView
lateinit var archive:TextView
lateinit var cargado:TextView
private lateinit var btn_save: Button
private lateinit var et_filename: EditText
private lateinit var pdfUri: Uri
private lateinit var storage: FirebaseStorage
// Define un código de solicitud para el Intent de selección de archivo
val PICK_PDF_REQUEST = 1

@Suppress("DEPRECATION")
class Cargar_Planos : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_planos)

        btn_next_p = findViewById(R.id.btn_next_p)
        btn_next_p.setOnClickListener {
            navegarSiguiente()
        }

        button_back_p = findViewById(R.id.back)
        button_back_p.setOnClickListener {
            volver()
        }

        storage = FirebaseStorage.getInstance()
        et_filename = findViewById(R.id.name_document_planos)
        archive = findViewById(R.id.archive)
        cargado = findViewById(R.id.cargado)

        btn_save = findViewById(R.id.save)
            btn_save.setOnClickListener {
                val text = et_filename.text.toString()
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


            val btn_cargar_archivo = findViewById<CardView>(R.id.btn_cargar_archivo)
        btn_cargar_archivo.setOnClickListener {

           val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
           }
            startActivityForResult(intent, PICK_PDF_REQUEST)
        }

        }
    private fun savePdf(pdfUri: Uri, text: String) {
        data class ImageInfo(
            val Ruta: String,
            val Nombre: String,
            val idProyecto: String
        )
        val fileName = "$text.pdf"
        val storageRef: StorageReference = storage.reference.child("Planos Usuarios/$fileName")

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
                db.collection("Carga_Documentos_Planos")
                    .add(imageInfo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "PDF y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }
                }

            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Log.e("Cargar_Planos", "Error al cargar el PDF: $exception")
                Toast.makeText(this, "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
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
    private fun navegarSiguiente() {
        val siguiente = Intent(this, Cargar_Renders::class.java)
        startActivity(siguiente)
    }

    private fun volver() {
        val volver = Intent(this, Cargar_Archivos::class.java)
        startActivity(volver)
    }
}
