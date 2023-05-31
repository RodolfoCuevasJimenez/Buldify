package cr.una.buildify.carga_archivos

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import cr.una.buildify.BuildConfig
import cr.una.buildify.R
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

lateinit var cd_progreso: CardView
lateinit var et_filename_pr: EditText
lateinit var btn_back_pr: Button
lateinit var tv_archivo_pr: TextView
lateinit var progreso: ImageView
lateinit var btn_save_pr: Button

lateinit var iv_select:ImageView
lateinit var vv_select:VideoView
private lateinit var storage_pr: FirebaseStorage
private lateinit var uriProgreso: Uri
private lateinit var currentPhotoUri: Uri
private var fileExtension: String = "jpg"

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_PICK_IMAGE = 2
private const val REQUEST_PICK_VIDEO = 3


@Suppress("DEPRECATION")
class Cargar_Progreso : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.carga_progreso)

        btn_back_pr = findViewById(R.id.btn_back_pr)
        btn_back_pr.setOnClickListener {
            volver()
        }

        storage_pr = FirebaseStorage.getInstance()

        cd_progreso = findViewById(R.id.cd_progreso)
        progreso = findViewById(R.id.progreso)
        iv_select = findViewById(R.id.iv_select)
        vv_select = findViewById(R.id.vv_select)

        cd_progreso.setOnClickListener {
            ///render.visibility = View.VISIBLE
            Log.i("PRUEBA","VISIBLE BOTON")
            openMediaChooser()
        }

        btn_save_pr = findViewById(R.id.btn_save_pr)
        et_filename_pr = findViewById(R.id.et_filename_pr)
        btn_save_pr.setOnClickListener {
            val text = et_filename_pr.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre de archivo", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función savePdf() pasando pdfUri
                if (uriProgreso != null) {
                    uploadImageToFirebaseStorage(uriProgreso!!, text) // Asegúrate de que pdfUri no sea nulo antes de llamar a savePdf
                } else {
                    Toast.makeText(this, "Selecciona un archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openMediaChooser() {
        val options = arrayOf<CharSequence>( "Elegir Foto de Galería", "Elegir Video de Galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecccionar Evidencia")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Elegir Foto de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, REQUEST_PICK_IMAGE)
                }
                options[item] == "Elegir Video de Galería" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, REQUEST_PICK_VIDEO)
                }
                options[item] == "Cancelar" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    // Método para crear un archivo de imagen temporal
    private fun createImageFile(): File? {
        // Crea un nombre único para el archivo de imagen
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when{
            requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null-> {
                uriProgreso = data.data!!
                iv_select.setImageURI(uriProgreso)
                fileExtension = "jpg"
                Log.i("PRUEBA","IMAGE SELECT")
            }
            requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null-> {
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
        val fileName = "$fileName.$fileExtension"
        val storageRef: StorageReference = storage_pr.reference.child("Evidencia Usuarios/$fileName")

        // Mostrar un ProgressDialog mientras se carga la imagen
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando evidencia...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        storageRef.putFile(uriProgreso)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                Toast.makeText(this, "Evidencia cargada exitosamente", Toast.LENGTH_SHORT).show()

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
                        Toast.makeText(this, "Evidencia y datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show()
            }
    }

    private fun volver(){
        val volver = Intent(this, Cargar_Archivos::class.java)
        startActivity(volver)
    }
}