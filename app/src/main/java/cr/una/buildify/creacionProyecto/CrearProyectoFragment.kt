package cr.una.buildify.creacionProyecto

import SpinAdapterDirectores
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import cr.una.buildify.DataClasses.Usuario
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCrearProyectoBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.utiles.UtilesFormularios

class CrearProyectoFragment : Fragment() {

    // Variables de vista
    private var _binding: FragmentCrearProyectoBinding? = null
    private lateinit var spnTipoProyecto: Spinner
    private lateinit var spnTipoMoneda: Spinner
    private lateinit var etNombreProyecto: EditText
    private lateinit var etPresupuestoProyecto: EditText
    private lateinit var etMtlDescripcionProyecto: EditText
    private lateinit var tvSpnClientes: TextView
    private lateinit var btnCrearProyecto: Button

    // Referencia a la base de datos Firestore
    private var db = Firebase.firestore

    // Binding para acceder a las vistas
    private val binding get() = _binding!!

    // Variables auxiliares
    private lateinit var dialog: Dialog
    private var nuevoCliente: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se obtiene una instancia del ViewModel necesario para el fragmento.
        val homeViewModel =
            ViewModelProvider(this)[DirectorProyectoMainViewModel::class.java]

        // Se infla y se vincula el layout del fragmento mediante el binding.
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
        tvSpnClientes = binding.tvSpnClientes

        // Se configura el adaptador para el Spinner de tipo de proyecto.
        val adapterTipo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoProyecto))
        spnTipoProyecto.adapter = adapterTipo

        // Se configura el adaptador para el Spinner de tipo de moneda.
        val adapterMoneda = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.tipoMoneda))
        spnTipoMoneda.adapter = adapterMoneda

        // Se configura el OnClickListener para el texto de selección de clientes.
        tvSpnClientes.setOnClickListener{
            configSpinnerConBusqueda()
        }

        // Se configura el OnClickListener para el botón de crear proyecto.
        btnCrearProyecto.setOnClickListener {
            crearProyecto()
        }
    }

    private fun crearProyecto() {
        // Se obtiene el UID del director de proyecto desde la actividad anterior.
        val uid = activity?.intent?.getStringExtra("Email")

        // Se verifica el formulario antes de crear el proyecto.
        if (verificarFormulario()) {
            val equipo = arrayListOf<Trabajador>()

            // Se crea un objeto Proyecto con los datos ingresados por el usuario.
            val nuevoProyecto = Proyecto(
                idDirector = uid!!,
                idCliente = nuevoCliente?.idUsuario?:"",
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
                    // Se actualiza el campo "id" con el ID generado automáticamente por Firestore.
                    it.update("id", it.id).addOnSuccessListener {
                        Toast.makeText(context, "Proyecto creado.", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "No se ha creado el proyecto.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            // Se navega de regreso a la pantalla principal de director de proyecto.
            view.let {
                Navigation.findNavController(it!!).navigate(R.id.director_Proyecto_Main)
            }
        }
    }

    private fun verificarFormulario(): Boolean {
        // Se verifica que los campos del formulario no estén vacíos.
        return UtilesFormularios.verificarCampo(etNombreProyecto) and
                UtilesFormularios.verificarCampo(etPresupuestoProyecto) and
                UtilesFormularios.verificarCampo(etMtlDescripcionProyecto) and
                tvSpnClientes.text.toString().isNotBlank()
    }

    private fun configSpinnerConBusqueda() {
        val listaUsuarios: MutableList<Usuario> = mutableListOf()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_searchable_spinner)
        dialog.window?.setLayout(650, 800)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val seleccioneStr ="Seleccione un Dueño de la Obra"
        val titulo = dialog.findViewById<TextView>(R.id.tvTituloCambioDirector)
        titulo.text = seleccioneStr
        dialog.show()
        val etBuscarDirector = dialog.findViewById<EditText>(R.id.etBuscarDirector)
        val lvClientes = dialog.findViewById<ListView>(R.id.lvDirectores)

        // Se obtienen los usuarios con el tipo "Dueño de la Obra" desde la colección "Usuarios" en Firestore.
        db.collection("Usuarios")
            .whereEqualTo("tipo", "Dueño de la Obra")
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
                val adapter = SpinAdapterDirectores(requireContext(), android.R.layout.simple_list_item_1, listaUsuarios.toTypedArray())
                lvClientes.adapter = adapter

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
                        // Se filtra la lista de usuarios según el texto ingresado en el EditText de búsqueda.
                        adapter.filter.filter(s)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Este método se llama después de que el texto en el EditText ha cambiado.
                    }
                })

                lvClientes.setOnItemClickListener { _, _, i, _ ->
                    // Se muestra el nombre del cliente seleccionado en el TextView y se guarda en la variable nuevoCliente.
                    tvSpnClientes.text = adapter.getItem(i).nombre
                    nuevoCliente = adapter.getItem(i)
                    dialog.dismiss()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Se anula la referencia al binding para evitar memory leaks.
        _binding = null
    }
}
