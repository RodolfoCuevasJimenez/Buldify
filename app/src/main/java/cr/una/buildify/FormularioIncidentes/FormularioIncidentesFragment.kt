package cr.una.buildify.FormularioIncidentes

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
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentFormularioIncidentesBinding
import cr.una.buildify.servicios.*
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.utiles.UtilesFormularios

private lateinit var etFecha: EditText
private lateinit var etDescripcion: EditText
private lateinit var etEtapa: EditText
private lateinit var etObservaciones: EditText
private lateinit var btnRegistrar: Button
private lateinit var baseDatos: FirebaseFirestore
private lateinit var tipo : String

class FormularioIncidentesFragment : Fragment() {

    private var _binding: FragmentFormularioIncidentesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentFormularioIncidentesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialización de los elementos de la interfaz de usuario
        etFecha = binding.etFechaIncidente
        etDescripcion = binding.etDescripcion
        etEtapa = binding.etEtapaObra
        etObservaciones = binding.etObservaciones
        btnRegistrar = binding.btnRegistrarIn

        // Obtener una instancia de Firebase Firestore
        baseDatos = FirebaseFirestore.getInstance()
        // Configurar el click listener para el botón "Regostrar"
        btnRegistrar.setOnClickListener {
            registrarIncidente()
        }
    }

    private fun registrarIncidente() {
        // Verifica si cada campo del formulario es válido usando el método verificarCampo de la clase UtilesFormularios
        val camposValidos = UtilesFormularios.verificarCampo(etFecha) &&
                UtilesFormularios.verificarCampo(etDescripcion) &&
                UtilesFormularios.verificarCampo(etEtapa) &&
                UtilesFormularios.verificarCampo(etObservaciones)

        if (!camposValidos) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un objeto con los datos del Incidente ingresados

        if(binding.chbxIncidente.isChecked() && !binding.chbxMejora.isChecked()){
            tipo = "Incidente"
        }
        else if (!binding.chbxIncidente.isChecked() && binding.chbxMejora.isChecked()){
            tipo = "Mejora"
        }
        else if (binding.chbxIncidente.isChecked() && binding.chbxMejora.isChecked()){
            Toast.makeText(requireContext(), "Solamente se debe de seleccionar un tipo de reporte", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            Toast.makeText(requireContext(), "Debe seleccionar al menos un tipo de Reporte", Toast.LENGTH_SHORT).show()
            return
        }


        val objetoFormulario = hashMapOf(
            "descripcion" to binding.etDescripcion.text.toString(),
            "etapa" to binding.etEtapaObra.text.toString(),
            "fecha" to binding.etFechaIncidente.text.toString(),
            "observaciones" to binding.etObservaciones.text.toString(),
            "tipo" to tipo
        )

        // Obtener una referencia a la colección "Visualizacion" en Firebase Firestore
        val IncidentesRef = baseDatos.collection("Visualizacion")
        // Agregar el nuevo incidente a la colección
        IncidentesRef.add(objetoFormulario)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Incidente agregado con ID: ${documentReference.id}")
                Toast.makeText(requireContext(), "Se registró su Incidente exitosamente", Toast.LENGTH_SHORT).show()
            }
            //Muestra mensaje en caso de que falle agregar el incidente
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error al agregar el incidente", e)
                Toast.makeText(requireContext(), "No se pudo registrar el incidente", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onDetach()
    }

}