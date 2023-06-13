package cr.una.buildify.servicios

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.carga_archivos.btn_Progreso
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentRegistrarServicioBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.utiles.UtilesFormularios

private lateinit var etTituloServicio: EditText
private lateinit var etDescripcion: EditText
private lateinit var etTipoServicio: EditText
private lateinit var etNombrePersona: EditText
private lateinit var etTelefono: EditText
private lateinit var etDireccion: EditText
private lateinit var etCorreoElectronico: EditText
private lateinit var btnGuardar: Button
private lateinit var db: FirebaseFirestore

class RegistrarServicioFragment : Fragment() {

    private var _binding: FragmentRegistrarServicioBinding? = null

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

        _binding = FragmentRegistrarServicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialización de los elementos de la interfaz de usuario
        etTituloServicio = binding.etTituloServicio
        etDescripcion = binding.etDescripciN
        etTipoServicio = binding.etTipoServicio
        etNombrePersona = binding.etNombrePersona
        etDireccion = binding.etDireccion
        etTelefono = binding.etTelefono
        etCorreoElectronico = binding.etCorreoElectronico
        btnGuardar = binding.btnGuardar
        // Obtener una instancia de Firebase Firestore
        db = FirebaseFirestore.getInstance()
        // Configurar el click listener para el botón "Guardar"
        btnGuardar.setOnClickListener {
            guardar()
        }
    }

    private fun guardar() {
        // Verifica si cada campo del formulario es válido usando el método verificarCampo de la clase UtilesFormularios
        val camposValidos = UtilesFormularios.verificarCampo(etTituloServicio) &&
                UtilesFormularios.verificarCampo(etDescripcion) &&
                UtilesFormularios.verificarCampo(etTipoServicio) &&
                UtilesFormularios.verificarCampo(etNombrePersona) &&
                UtilesFormularios.verificarCampo(etDireccion) &&
                UtilesFormularios.verificarCampo(etTelefono) &&
                UtilesFormularios.verificarCampo(etCorreoElectronico)

        if (!camposValidos) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener los valores de los campos de texto
        val titulo = etTituloServicio.text.toString()
        val descripcion = etDescripcion.text.toString()
        val tipo = etTipoServicio.text.toString()
        val nombre = etNombrePersona.text.toString()
        val direccion = etDireccion.text.toString()
        val telefono = etTelefono.text.toString()
        val correo = etCorreoElectronico.text.toString()

        // Validar el formato del correo electrónico utilizando una expresión regular
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        if (!correo.matches(emailPattern)) {
            Toast.makeText(requireContext(), "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }
        // Crear un objeto de tipo Servicio con los datos ingresados
        val nuevoServicio = Servicio(
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo,
            nombrePersona = nombre,
            direccion = direccion,
            telefono = telefono,
            correoElectronico = correo
        )
        // Obtener una referencia a la colección "Servicios" en Firebase Firestore
        val serviciosRef = db.collection("Servicios")
        // Agregar el nuevo servicio a la colección
        serviciosRef.add(nuevoServicio)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Documento agregado con ID: ${documentReference.id}")
                Toast.makeText(requireContext(), "Se registró su servicio exitosamente", Toast.LENGTH_SHORT).show()

            }
            //Muestra mensaje en caso de que falle agregar el servicio
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error al agregar el documento", e)
                Toast.makeText(requireContext(), "No se pudo registrar su servicio", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}