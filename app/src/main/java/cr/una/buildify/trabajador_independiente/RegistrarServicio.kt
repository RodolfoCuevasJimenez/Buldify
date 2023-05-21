package cr.una.buildify.trabajador_independiente


import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.servicios.Servicio

class RegistrarServicio : AppCompatActivity() {

    private lateinit var etTituloServicio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etTipoServicio: EditText
    private lateinit var etNombrePersona: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etCorreoElectronico: EditText
    private lateinit var btnGuardar: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_servicio)
        etTituloServicio = findViewById(R.id.etTituloServicio)
        etDescripcion = findViewById(R.id.etDescripción)
        etTipoServicio = findViewById(R.id.etTipoServicio)
        etNombrePersona = findViewById(R.id.etNombrePersona)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etCorreoElectronico = findViewById(R.id.etCorreoElectronico)
        btnGuardar = findViewById(R.id.btnGuardar)
        db = FirebaseFirestore.getInstance()
        btnGuardar.setOnClickListener {
            guardar()
        }


    }

    private fun guardar() {

        val titulo = etTituloServicio.text.toString()
        val descripcion = etDescripcion.text.toString()
        val tipo = etTipoServicio.text.toString()
        val nombre = etNombrePersona.text.toString()
        val direccion = etDireccion.text.toString()
        val telefono = etTelefono.text.toString()
        val correo = etCorreoElectronico.text.toString()


        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        if (!correo.matches(emailPattern)) {
            Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoServicio = Servicio(
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo,
            nombrePersona = nombre,
            direccion = direccion,
            telefono = telefono,
            correoElectronico = correo
        )

        val serviciosRef = db.collection("Servicios")
        serviciosRef.add(nuevoServicio)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Documento agregado con ID: ${documentReference.id}")
                Toast.makeText(this, "Se registró su servicio exitosamente", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar el documento", e)
                Toast.makeText(this, "No se pudo registrar su servicio", Toast.LENGTH_SHORT).show()
            }
    }
    }



