package cr.una.buildify.ui.general.cronograma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCronogramaBinding
import cr.una.buildify.ui.general.cronograma.Adapters.TareaCronogramaAdapter
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma
import cr.una.buildify.ui.general.servicios.proyecto.ProyectoRepositorio
import cr.una.buildify.ui.general.servicios.tarea.TareaRepositorio
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class CronogramaFragment : Fragment() {

    private var _binding: FragmentCronogramaBinding? = null
    private val binding get() = _binding!!

    private lateinit var dateSelected: LocalDate
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var uid: String? = null
    private var tipo: String? = null

    private val tareaRepositorio = TareaRepositorio()
    private val proyectoRepositorio = ProyectoRepositorio()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_cronograma, container, false)

        dateSelected = LocalDate.now()
        uid = this.activity?.intent?.extras?.getString("UID")
        tipo = this.activity?.intent?.extras?.getString("Tipo")

        if(tipo?.lowercase() == "dueño de la obra"){
            view.findViewById<FloatingActionButton>(R.id.btn_view_add_task).visibility = View.GONE
        }


        val recyclerViewCrono = view.findViewById<RecyclerView>(R.id.recyclerCrono)
        recyclerViewCrono.setHasFixedSize(true)
        recyclerViewCrono.layoutManager = LinearLayoutManager(view.context)
        val cronoAdapater = TareaCronogramaAdapter()
        cronoAdapater.parentView = view
        cronoAdapater.uid = this.uid
        cronoAdapater.tipo = this.tipo

        recyclerViewCrono.adapter = cronoAdapater

        ObtenerListaTareas(cronoAdapater)

        InitCalendarOptions(view, cronoAdapater)
        SetEscuchaEventos(view)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Funcion para cargar la informacion para el Calendar View
     * @param [view] la vista del fragment
     * @param [cronoAdapater] el adaptador para el recycler view
     */
    fun InitCalendarOptions(view: View, cronoAdapater: TareaCronogramaAdapter) {
        val calendarView: CalendarView = view.findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            dateSelected = LocalDate.of(year, (month + 1), dayOfMonth)

            ObtenerListaTareas(cronoAdapater)
        }
    }

    /**
     * Metodo para asignar los eventos a los elementos de la vista
     * @param [view] La vista del fragment
     */
    fun SetEscuchaEventos(view: View) {
        val btnViewAddTask = view.findViewById<FloatingActionButton>(R.id.btn_view_add_task)
        btnViewAddTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("fechaSeleccionada", dateSelected.toString())
            bundle.putString("uid", uid)
            Navigation.findNavController(view).navigate(R.id.nav_add_task, bundle)
        }
    }

    /**
     * Funcion para obtener la lista de tareas por proyecto
     * @author Nestor Pasos
     * @param [cronoAdapter] Adapter del recycler view
     */
    fun ObtenerListaTareas(cronoAdapter: TareaCronogramaAdapter) {
        val listTareas = mutableListOf<TareaCronograma>()
        val listIds = mutableListOf<String>()

        proyectoRepositorio
            .getListaProyectos(uid.toString())
            .addOnSuccessListener {
                for (res in it) {
                    listIds.add(res["id"].toString())
                }

                tareaRepositorio
                    .listarTareas(listIds, dateSelected.format(dateFormatter))
                    .addOnSuccessListener { result ->
                        for (tarea in result) {
                            listTareas.add(
                                TareaCronograma(
                                    tarea["titulo"].toString(),
                                    tarea["descripcion"].toString(),
                                    tarea["fecha"].toString(),
                                    tarea["horaInicio"].toString(),
                                    tarea["horaFin"].toString(),
                                    tarea["estaCompleta"].toString().toBoolean(),
                                    tarea["idProyecto"].toString()
                                )
                            )
                        }

                        cronoAdapter.listaTareas = listTareas.toList()
                    }
            }
    }
}