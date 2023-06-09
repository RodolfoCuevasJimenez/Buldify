package cr.una.buildify.creacionProyecto

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cr.una.buildify.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.utiles.UtilesFormularios

class CrearProyecto : AppCompatActivity() {



    private lateinit var spnTipoProyecto: Spinner
    private lateinit var spnTipoMoneda: Spinner
    private lateinit var etNombreProyecto: EditText
    private lateinit var etPresupuestoProyecto: EditText
    private lateinit var etMtlDescripcionProyecto: EditText
    private lateinit var btnCrearProyecto: Button

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_crear_proyecto)

        etNombreProyecto = findViewById(R.id.etNombreProyecto)

        spnTipoProyecto = findViewById(R.id.spTipoProyecto)
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoProyecto))
        spnTipoProyecto.adapter = adapterTipo
        spnTipoMoneda = findViewById(R.id.spTipoMoneda)
        val adapterMoneda = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoMoneda))
        spnTipoMoneda.adapter = adapterMoneda

        etPresupuestoProyecto = findViewById(R.id.etPresupuestoProyecto)
        etMtlDescripcionProyecto = findViewById(R.id.etMtlDescripcionProyecto)
        btnCrearProyecto = findViewById(R.id.btnCrearProyecto)
        btnCrearProyecto.setOnClickListener { intent.getStringExtra("UID")
            ?.let { it1 -> agregarProyecto(it1) } }
    }

    private fun agregarProyecto(uid: String) {
        val equipo = arrayListOf<Trabajador>()
        if(verificarFormulario()){
            val nuevoProyecto = Proyecto(
                idDirector = uid,
                nombre = etNombreProyecto.text.toString().trim().lowercase(),
                tipo = spnTipoProyecto.selectedItem.toString(),
                moneda = spnTipoMoneda.selectedItem.toString(),
                presupuesto = etPresupuestoProyecto.text.toString().toDouble(),
                descripcion = etMtlDescripcionProyecto.text.toString(),
                equipo = equipo
            )
            db.collection("Proyectos")
                .add(nuevoProyecto)
                .addOnSuccessListener { Toast.makeText(this, "Proyecto creado.", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener() { Toast.makeText(this, "No se ha creado el proyecto.", Toast.LENGTH_SHORT).show() }
            finish()
        }
    }

    private fun verificarFormulario(): Boolean {
        return UtilesFormularios.verificarCampo(etNombreProyecto) and
                UtilesFormularios.verificarCampo(etPresupuestoProyecto) and
                UtilesFormularios.verificarCampo(etMtlDescripcionProyecto)
    }
}