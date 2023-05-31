package cr.una.buildify.carga_archivos

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
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import cr.una.buildify.R

class Cargar_Renders : AppCompatActivity() {

    private lateinit var btn_save: Button
    lateinit var btn_next_re: ImageView
    lateinit var btn_previous_re: ImageView
    lateinit var btn_back_re: Button
    private lateinit var et_filename_ren: EditText
    private lateinit var render: ImageView
    private lateinit var storage_ren: FirebaseStorage
    private lateinit var uriImage: Uri
    private val PICK_IMAGE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_renders)

        btn_next_re = findViewById(R.id.btn_next_re)
        btn_next_re.setOnClickListener {
            navegarSiguiente()
        }

        btn_previous_re = findViewById(R.id.btn_previous_re)
        btn_previous_re.setOnClickListener {
            navegarAnterior()
        }

        btn_back_re = findViewById(R.id.back)
        btn_back_re.setOnClickListener {
            volver()
        }

        storage_ren = FirebaseStorage.getInstance()

        render = findViewById(R.id.imageView_cargar)
        btn_save = findViewById(R.id.save)
        et_filename_ren = findViewById(R.id.et_filename)

        render.visibility = View.GONE

        val btn_cargar_archivo = findViewById<CardView>(R.id.btn_cargar_archivo_re)
        btn_cargar_archivo.setOnClickListener {
            openMediaChooser()
        }

        btn_save.setOnClickListener {
            val text = et_filename_ren.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (uriImage != null) {
                    uploadImageToFirebaseStorage(uriImage!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(this, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
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

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
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
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando imagen...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                Toast.makeText(this, "Imagen cargada exitosamente", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this, "Imagen y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener { exception ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
                }

    }


    private fun navegarAnterior() {
        val anterior = Intent(this, Cargar_Planos::class.java)
        startActivity(anterior)
    }

    private fun navegarSiguiente() {
        val siguiente = Intent(this, Cargar_Recorridos::class.java)
        startActivity(siguiente)
    }

    private fun volver(){
        val volver = Intent(this, Cargar_Archivos::class.java)
        startActivity(volver)
    }

}


