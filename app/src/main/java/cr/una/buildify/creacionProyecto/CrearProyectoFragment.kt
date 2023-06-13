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

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se obtiene una instancia del ViewModel necesario para el fragmento.
        val homeViewModel =
            ViewModelProvider(this)[DirectorProyectoMainViewModel::class.java]

        _binding = FragmentCrearProyectoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se obtienen las referencias a los elementos de la interfaz de usuario.
        etNombreProyecto = binding.etNombreProyecto
        spnTipoProyecto = binding.spTipoProyecto
        spnTipoMoneda = binding.spTipoMoneda
        etPresupuestoProyecto = binding.etPresupuestoProyecto
        etMtlDescripcionProyecto = binding.etMtlDescripcionProyecto
        btnCrearProyecto = binding.btnCrearProyecto

        // Se configura el adaptador para el Spinner de tipo de proyecto.
        val adapterTipo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoProyecto))
        spnTipoProyecto.adapter = adapterTipo

        // Se configura el adaptador para el Spinner de tipo de moneda.
        val adapterMoneda = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoMoneda))
        spnTipoMoneda.adapter = adapterMoneda

        // Se configura el OnClickListener para el botón de crear proyecto.
        btnCrearProyecto.setOnClickListener {
            // Se obtiene el UID del director de proyecto desde la actividad anterior.
            val uid = activity?.intent?.getStringExtra("UID")

            // Se verifica el formulario antes de crear el proyecto.
            if (verificarFormulario()) {
                val equipo = arrayListOf<Trabajador>()

                // Se crea un objeto Proyecto con los datos ingresados por el usuario.
                val nuevoProyecto = Proyecto(
                    idDirector = uid!!,
                    nombre = etNombreProyecto.text.toString().trim().lowercase(),
                    tipo = spnTipoProyecto.selectedItem.toString(),
                    moneda = spnTipoMoneda.selectedItem.toString(),
                    presupuesto = etPresupuestoProyecto.text.toString().toDouble(),
                    descripcion = etMtlDescripcionProyecto.text.toString(),
                    equipo = equipo
                )

                // Se agrega el nuevo proyecto a la colección "Proyectos" en Firestore.
                db.collection("Proyectos")
                    .add(nuevoProyecto)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Proyecto creado.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "No se ha creado el proyecto.", Toast.LENGTH_SHORT).show()
                    }

                // Se navega de regreso a la pantalla principal de director de proyecto.
                view.let { Navigation.findNavController(it).navigate(R.id.director_Proyecto_Main) }
            }
        }
    }

    private fun verificarFormulario(): Boolean {
        // Se verifica que los campos del formulario no estén vacíos.
        return UtilesFormularios.verificarCampo(etNombreProyecto) &&
                UtilesFormularios.verificarCampo(etPresupuestoProyecto) &&
                UtilesFormularios.verificarCampo(etMtlDescripcionProyecto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}