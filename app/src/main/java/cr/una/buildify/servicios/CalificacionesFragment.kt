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
    private var _binding: FragmentCalificacionesBinding? = null
    private lateinit var rvCalificaciones: RecyclerView
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var db: FirebaseFirestore
    private var serviciosList: MutableList<Servicio> = mutableListOf()

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel =
            ViewModelProvider(this).get(TrabajadorIndependienteMainViewModel::class.java)

        _binding = FragmentCalificacionesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvIdNombreTrabajador: TextView
        tvIdNombreTrabajador = binding.tvIdNombreTrabajador
        val tvCalificacionTrabajador: TextView
        tvCalificacionTrabajador=binding.tvCalificacionTrabajador
        tvIdNombreTrabajador.text = activity?.intent!!.getStringExtra("Email")!!
        if(serviciosList.isEmpty()){
            tvCalificacionTrabajador.text="0"
            Toast.makeText(context, "El trabajador no tiene servicios asociados", Toast.LENGTH_SHORT).show()
        }else{
            tvCalificacionTrabajador.text = calcCalificacion().toString()
        }
        rvCalificaciones = binding.rvCalificaciones
        rvCalificaciones.layoutManager = LinearLayoutManager(activity)
        db = FirebaseFirestore.getInstance()
        filterServices()

    }
    private fun filterServices() {
        val idTrabajador= activity?.intent!!.getStringExtra("Email")!!
        val serviciosRef = db.collection("Servicios")
        serviciosRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Error al obtener los servicios", e)
                return@addSnapshotListener
            }

            serviciosList = mutableListOf<Servicio>()
            for (doc in snapshot?.documents ?: emptyList()) {
                val servicio = doc.toObject(Servicio::class.java)
                if (servicio != null && servicio.idUsuario == idTrabajador) {
                    serviciosList.add(servicio)
                }
            }
            val varIntent= activity?.intent!!.getStringExtra("Tipo")!!
            serviciosAdapter = ServiciosAdapter(serviciosList, db, varIntent)
            rvCalificaciones.adapter = serviciosAdapter
        }
    }
    private fun calcCalificacion(): Double {
        return serviciosList.map { it.calificacionGeneral }.average()
    }


}