package cr.una.buildify.creacionProyecto

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.carga_archivos.btn_Progreso
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentCrearProyectoBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.utiles.UtilesFormularios

class CrearProyectoFragment : Fragment() {

    private var _binding: FragmentCrearProyectoBinding? = null
    private lateinit var spnTipoProyecto: Spinner
    private lateinit var spnTipoMoneda: Spinner
    private lateinit var etNombreProyecto: EditText
    private lateinit var etPresupuestoProyecto: EditText
    private lateinit var etMtlDescripcionProyecto: EditText
    private lateinit var btnCrearProyecto: Button
    private var db = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentCrearProyectoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etNombreProyecto = binding.etNombreProyecto

        spnTipoProyecto = binding.spTipoProyecto
        val adapterTipo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoProyecto))
        spnTipoProyecto.adapter = adapterTipo
        spnTipoMoneda = binding.spTipoMoneda
        val adapterMoneda = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoMoneda))
        spnTipoMoneda.adapter = adapterMoneda

        etPresupuestoProyecto = binding.etPresupuestoProyecto
        etMtlDescripcionProyecto =binding.etMtlDescripcionProyecto
        btnCrearProyecto = binding.btnCrearProyecto

        btnCrearProyecto.setOnClickListener { activity?.intent!!?.getStringExtra("UID")
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
                .addOnSuccessListener { Toast.makeText(context, "Proyecto creado.", Toast.LENGTH_SHORT).show() }
                .addOnFailureListener() { Toast.makeText(context, "No se ha creado el proyecto.", Toast.LENGTH_SHORT).show() }
            view?.let { Navigation.findNavController(it).navigate(R.id.director_Proyecto_Main) }
        }
    }

    private fun verificarFormulario(): Boolean {
        return UtilesFormularios.verificarCampo(etNombreProyecto) and
                UtilesFormularios.verificarCampo(etPresupuestoProyecto) and
                UtilesFormularios.verificarCampo(etMtlDescripcionProyecto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}