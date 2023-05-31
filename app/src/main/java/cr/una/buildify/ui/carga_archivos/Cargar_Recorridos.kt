package cr.una.buildify.carga_archivos

import cr.una.buildify.R
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

private lateinit var btn_back: Button
private lateinit var btn_previuos: ImageView
private lateinit var videoView: VideoView
private lateinit var storage_rec: FirebaseStorage
private lateinit var videoUri: Uri
private val PICK_VIDEO_REQUEST_CODE = 2
private lateinit var btn_save: Button
private lateinit var et_filename_rec: EditText


@Suppress("DEPRECATION")
class Cargar_Recorridos : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_recorridos)

        btn_previuos = findViewById(R.id.btn_previous)
        btn_previuos.setOnClickListener {
            navigatePrevious()
        }

        btn_back = findViewById(R.id.back)
        btn_back.setOnClickListener {
            navigateBack()
        }

        storage_rec = FirebaseStorage.getInstance()

        btn_save = findViewById(R.id.save)
        videoView = findViewById(R.id.videoView)
        et_filename_rec = findViewById(R.id.name_document_recorrido)
        videoView.visibility = View.GONE
        val btn_cargar_archivo = findViewById<CardView>(R.id.btn_cargar_archivos)
        btn_cargar_archivo.setOnClickListener {
            openVideoChooser()
        }

        val btn_save = findViewById<Button>(R.id.save)
        btn_save.setOnClickListener {
            val text = et_filename_rec.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (videoUri != null) {
                    uploadVideoToFirebaseStorage(videoUri!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(this, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
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
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando video...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(videoUri)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                videoView.setVideoURI(videoUri)
                videoView.start()
                Toast.makeText(this, "Video cargado exitosamente", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this, "Video y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }

    }
    private fun openVideoChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            videoUri = data.data!!
            videoView.setVideoURI(videoUri)
            videoView.visibility = View.VISIBLE
            videoView.start()
        }
    }

    private fun navigateBack() {
        val back = Intent(this, Cargar_Archivos::class.java)
        startActivity(back)
    }

    private fun navigatePrevious() {
        val previous = Intent(this, Cargar_Renders::class.java)
        startActivity(previous)
    }
}