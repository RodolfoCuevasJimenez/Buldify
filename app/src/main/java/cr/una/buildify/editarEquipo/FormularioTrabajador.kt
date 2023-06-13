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

        // Inicializar vistas
        tvTituloFormularioTrabajador = findViewById(R.id.tvTituloFormularioTrabajador)
        etNombre = findViewById(R.id.etNombre)
        etRol = findViewById(R.id.etRol)
        etCorreo = findViewById(R.id.etCorreo)
        btnAccion = findViewById(R.id.btnAccion)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Obtener datos del Intent
        tipo = intent.getStringExtra("Tipo")!!
        uidProyecto = intent.getStringExtra("uidProyecto")!!

        // Configurar el botón Cancelar para finalizar la actividad
        btnCancelar.setOnClickListener {
            finish()
        }

        // Configurar el formulario según el tipo (Agregar o Editar)
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
                // Establecer los datos actuales del trabajador en los campos de texto
                etNombre.setText(intent.getStringExtra("nombreTrabajador"))
                etRol.setText(intent.getStringExtra("rolTrabajador"))
                etCorreo.setText(intent.getStringExtra("correoTrabajador"))
                btnAccion.setOnClickListener{
                    editarTrabajador()
                }
            }
        }

        // Actualizar el título y el texto del botón de acción según el tipo de formulario
        tvTituloFormularioTrabajador.text = titulo
        btnAccion.text = accion
    }

    private fun agregarTrabajador(){
        // Verificar si el formulario es válido antes de agregar un nuevo trabajador
        if(verificarFormulario()){
            val nuevoTrabajador = Trabajador (
                nombre = etNombre.text.toString(),
                rol = etRol.text.toString(),
                correo = etCorreo.text.toString()
            )
            // Actualizar el campo "equipo" en Firestore con el nuevo trabajador
            db.collection("Proyectos").document(uidProyecto).update("equipo", FieldValue.arrayUnion(nuevoTrabajador))
                .addOnSuccessListener { Toast.makeText(this, "Trabajador agregado.", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener { Toast.makeText(this, "Error al agregar al nuevo trabajador.", Toast.LENGTH_SHORT).show() }
            finish()
        }
    }

    private fun editarTrabajador(){
        // Obtener la posición del trabajador en la lista
        val posicion = intent.getIntExtra("posicionTrabajador", -1)
        // Verificar si el formulario es válido antes de editar el trabajador
        if(verificarFormulario()) {
            // Verificar si se han realizado cambios en los datos del trabajador
            if(comprobarCambios()) {
                val coleccion = db.collection("Proyectos")
                val documento = coleccion.document(uidProyecto)
                documento.get()
                    .addOnSuccessListener {
                        val equipo = it.get("equipo") as? List<HashMap<String, String>>

                        if (equipo != null && equipo.size > posicion) {
                            // Crear un nuevo mapa con los datos editados del trabajador
                            val trabajadorEditado = hashMapOf<String, String>(
                                "nombre" to etNombre.text.toString(),
                                "rol" to etRol.text.toString(),
                                "correo" to etCorreo.text.toString()
                            )
                            // Actualizar el mapa del trabajador en la posición correspondiente de la lista
                            equipo[posicion].putAll(trabajadorEditado)

                            // Actualizar el campo "equipo" en Firestore con la lista actualizada de trabajadores
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
        // Verificar si los campos del formulario no están vacíos
        return UtilesFormularios.verificarCampo(etNombre) and UtilesFormularios.verificarCampo(etRol) and UtilesFormularios.verificarCampo(etCorreo)
    }

    private fun comprobarCambios(): Boolean {
        // Verificar si se han realizado cambios en los campos de texto en comparación con los datos actuales del trabajador
        return intent.getStringExtra("nombreTrabajador")!! != etNombre.text.toString()
                || intent.getStringExtra("rolTrabajador")!! != etRol.text.toString()
                || intent.getStringExtra("correoTrabajador") != etCorreo.text.toString()
    }
}
