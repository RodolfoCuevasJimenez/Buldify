package cr.una.buildify.editarEquipo

import SpinAdapterDirectores
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.DataClasses.Usuario
import cr.una.buildify.R

class CambiarDirectorActivity : AppCompatActivity() {
    private lateinit var uidProyecto: String
    private lateinit var tvSpnDirectores: TextView
    private lateinit var chkbxConfirmarCambio: CheckBox
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelarCambioDirector: Button
    private lateinit var dialog: Dialog

    private var nuevoDirector: Usuario? = null

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_director)

        uidProyecto = intent.getStringExtra("UIDProyecto") ?: ""

        tvSpnDirectores = findViewById(R.id.tvSpnDirectores)
        chkbxConfirmarCambio = findViewById(R.id.chkbxConfirmarCambio)
        btnAceptar = findViewById(R.id.btnAceptar)
        btnCancelarCambioDirector = findViewById(R.id.btnCancelarCambioDirector)

        tvSpnDirectores.setOnClickListener{
            configSpinnerConBusqueda()
        }

        btnAceptar.setOnClickListener {
            cambiarDirector(uidProyecto)
        }

        btnCancelarCambioDirector.setOnClickListener {
            finish()
        }
    }

    private fun cambiarDirector(uidProyecto: String) {
        if (nuevoDirector != null) {
            if (chkbxConfirmarCambio.isChecked) {
                // Se actualiza el campo "idDirector" del proyecto en Firestore con el ID del nuevo director seleccionado.
                db.collection("Proyectos").document(uidProyecto)
                    .update("idDirector", nuevoDirector?.idUsuario)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Director del proyecto cambiado!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
            } else {
                chkbxConfirmarCambio.error = "Debe seleccionar esta casilla."
            }
        } else {
            tvSpnDirectores.error = "Debe elegir un nuevo Director de Proyecto"
        }
    }

    private fun configSpinnerConBusqueda() {
        val listaUsuarios: MutableList<Usuario> = mutableListOf()

        dialog = Dialog(this@CambiarDirectorActivity)
        dialog.setContentView(R.layout.dialog_searchable_spinner)
        dialog.window?.setLayout(650, 800)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val etBuscarDirector = dialog.findViewById<EditText>(R.id.etBuscarDirector)
        val lvDirectores = dialog.findViewById<ListView>(R.id.lvDirectores)

        // Se obtienen los usuarios con el tipo "Director de Proyecto" desde la colección "Usuarios" en Firestore.
        db.collection("Usuarios")
            .whereEqualTo("tipo", "Director de Proyecto")
            .get()
            .addOnSuccessListener { documents ->
                for (usuario in documents) {
                    val idUsuario = usuario.get("idUsuario") as String?
                    val nombre = usuario.get("nombre") as String?
                    val tipo = usuario.get("tipo") as String?
                    if (idUsuario != null && nombre != null && tipo != null) {
                        val nuevoUsuario = Usuario(idUsuario, nombre, tipo)
                        listaUsuarios.add(nuevoUsuario)
                    }
                }
                val adapter = SpinAdapterDirectores(this, android.R.layout.simple_list_item_1, listaUsuarios.toTypedArray())
                lvDirectores.adapter = adapter

                etBuscarDirector.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // Este método se llama antes de que el texto en el EditText cambie.
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // Este método se llama durante el cambio de texto en el EditText.
                        adapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Este método se llama después de que el texto en el EditText ha cambiado.
                    }
                })

                lvDirectores.setOnItemClickListener { _, _, i, _ ->
                    // Se muestra el nombre del nuevo director seleccionado en el TextView correspondiente y se guarda en la variable "nuevoDirector".
                    tvSpnDirectores.text = adapter.getItem(i).nombre
                    nuevoDirector = adapter.getItem(i)
                    dialog.dismiss()
                }
            }
    }
}
