package cr.una.buildify.editarEquipo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Trabajador
import cr.una.buildify.utiles.UtilesFormularios

@Suppress("UNCHECKED_CAST")
class FormularioTrabajador : AppCompatActivity() {

    private lateinit var tvTituloFormularioTrabajador: TextView
    private lateinit var etNombre: EditText
    private lateinit var etRol: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnAccion: Button
    private lateinit var btnCancelar: Button
    private lateinit var uidProyecto:String
    private lateinit var tipo:String
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formulario_trabajador)

        tvTituloFormularioTrabajador = findViewById(R.id.tvTituloFormularioTrabajador)
        etNombre = findViewById(R.id.etNombre)
        etRol = findViewById(R.id.etRol)
        etCorreo = findViewById(R.id.etCorreo)
        btnAccion = findViewById(R.id.btnAccion)
        btnCancelar = findViewById(R.id.btnCancelar)

        tipo = intent.getStringExtra("Tipo")!!
        uidProyecto = intent.getStringExtra("uidProyecto")!!

        btnCancelar.setOnClickListener {
            finish()
        }

        tipoFormulario()
    }

    private fun tipoFormulario(){
        var titulo = ""
        var accion = ""

        when(tipo){
            "Agregar" -> {
                titulo = "Agregar Trabajador"
                accion = "Agregar"
                btnAccion.setOnClickListener{
                    agregarTrabajador()
                }
            }
            "Editar" ->{
                titulo = "Editar Trabajador"
                accion = "Guardar"
                etNombre.setText(intent.getStringExtra("nombreTrabajador"))
                etRol.setText(intent.getStringExtra("rolTrabajador"))
                etCorreo.setText(intent.getStringExtra("correoTrabajador"))
                btnAccion.setOnClickListener{
                    editarTrabajador()
                }
            }
        }

        tvTituloFormularioTrabajador.text = titulo
        btnAccion.text = accion
    }

    private fun agregarTrabajador(){
        if(verificarFormulario()){
            val nuevoTrabajador = Trabajador (
                nombre = etNombre.text.toString(),
                rol = etRol.text.toString(),
                correo = etCorreo.text.toString()
            )
            db.collection("Proyectos").document(uidProyecto).update("equipo", FieldValue.arrayUnion(nuevoTrabajador))
                .addOnSuccessListener { Toast.makeText(this, "Trabajador agregado.", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(this, "Error al agregar al nuevo trabajador.", Toast.LENGTH_SHORT).show() }
            finish()
        }
    }

    private fun editarTrabajador(){
        val posicion = intent.getIntExtra("posicionTrabajador", -1)
        if(verificarFormulario()) {
            if(comprobarCambios()) {
                val coleccion = db.collection("Proyectos")
                val documento = coleccion.document(uidProyecto)
                documento.get()
                    .addOnSuccessListener {
                        val equipo = it.get("equipo") as? List<HashMap<String, String>>

                        if (equipo != null && equipo.size > posicion) {
                            val trabajadorEditado = hashMapOf<String, String>(
                                "nombre" to etNombre.text.toString(),
                                "rol" to etRol.text.toString(),
                                "correo" to etCorreo.text.toString()
                            )
                            equipo[posicion].putAll(trabajadorEditado)

                            documento.update("equipo", equipo)
                                .addOnSuccessListener { Toast.makeText(this, "Se ha editado el trabajador.", Toast.LENGTH_SHORT).show() }
                                .addOnFailureListener { Toast.makeText(this, "No se ha podido editar el trabajador.", Toast.LENGTH_SHORT).show() }
                        }
                    }
                    .addOnFailureListener{Toast.makeText(this, "No se ha podido obtener el equipo de trabajo", Toast.LENGTH_SHORT).show()}
            }
            else{
                Toast.makeText(this, "No se ha hecho ningún cambio.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun verificarFormulario(): Boolean {
        return UtilesFormularios.verificarCampo(etNombre) and UtilesFormularios.verificarCampo(etRol) and UtilesFormularios.verificarCampo(etCorreo)
    }

    private fun comprobarCambios(): Boolean {
        return intent.getStringExtra("nombreTrabajador")!! != etNombre.text.toString()
                || intent.getStringExtra("rolTrabajador")!! != etRol.text.toString()
                || intent.getStringExtra("correoTrabajador") != etCorreo.text.toString()
    }
}