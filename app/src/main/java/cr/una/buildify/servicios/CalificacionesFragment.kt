package cr.una.buildify.servicios

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.databinding.FragmentCalificacionesBinding
import cr.una.buildify.ui.trabajador_independiente.TrabajadorIndependienteMainViewModel


class CalificacionesFragment : Fragment() {
    // Declaración de variables y propiedades
    private var _binding: FragmentCalificacionesBinding? = null
    private lateinit var rvCalificaciones: RecyclerView
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var db: FirebaseFirestore
    private var serviciosList: MutableList<Servicio> = mutableListOf()
    // Propiedad para acceder al objeto de enlace (_binding) de forma segura
    private val binding get() = _binding!!
    // Método llamado cuando se crea la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(TrabajadorIndependienteMainViewModel::class.java)

        _binding = FragmentCalificacionesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    // Método llamado cuando la vista del fragmento ha sido creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicialización de vistas y variables
        val tvIdNombreTrabajador: TextView = binding.tvIdNombreTrabajador
        // Obtener el valor del correo electrónico pasado como extra desde la actividad
        tvIdNombreTrabajador.text = activity?.intent!!.getStringExtra("Email")!!
        rvCalificaciones = binding.rvCalificaciones
        rvCalificaciones.layoutManager = LinearLayoutManager(activity)
        //Obtiene la instancia de Firebase
        db = FirebaseFirestore.getInstance()
        // Filtrar los servicios asociados al trabajador
        filterServices()

    }
    // Método privado para filtrar los servicios asociados al trabajador
    private fun filterServices() {
        // Obtener el ID del trabajador desde el intent de la actividad
        val idTrabajador= activity?.intent!!.getStringExtra("Email")!!
        val serviciosRef = db.collection("Servicios")
        serviciosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            serviciosList = mutableListOf()
            for (doc in snapshot?.documents ?: emptyList()) {
                val servicio = doc.toObject(Servicio::class.java)
                // Agregar el servicio a la lista si el ID del usuario coincide con el ID del trabajador
                if (servicio != null && servicio.idUsuario == idTrabajador) {
                    serviciosList.add(servicio)
                }
            }

            if (serviciosList.isEmpty()) {
                binding.tvCalificacionTrabajador.text = "0"
                Toast.makeText(context, "El trabajador no tiene servicios asociados", Toast.LENGTH_SHORT).show()
            } else {
                // Calcular y mostrar la calificación promedio del trabajador
                db.collection("Usuarios").document(idTrabajador).update("Calificacion",calcCalificacion())

                binding.tvCalificacionTrabajador.text = String.format("%.2f",calcCalificacion())
            }
            // Obtener el valor de "Tipo" desde el intent de la actividad o desde los argumentos del fragmento
            var varIntent= activity?.intent?.getStringExtra("Tipo")
            if(varIntent == null)
                varIntent = arguments?.getString("Tipo") ?: ""
            // Crear un adaptador de servicios y configurarlo en el RecyclerView
            serviciosAdapter = ServiciosAdapter(serviciosList, db, varIntent,idTrabajador)
            rvCalificaciones.adapter = serviciosAdapter
        }
    }
    // Método privado para calcular la calificación promedio de los servicios
    private fun calcCalificacion(): Double {
        var tamannoLista = 0
        var total = 0.0
        // Recorrer la lista de servicios y sumar las calificaciones
        serviciosList.forEach{
            if(it.calificaciones.isNotEmpty()) {
                tamannoLista++
                total += it.calificacionGeneral
            }
        }
        return total/tamannoLista
    }


}