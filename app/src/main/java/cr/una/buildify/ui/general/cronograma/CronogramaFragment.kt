package cr.una.buildify.ui.general.cronograma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCronogramaBinding
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.ui.general.cronograma.Adapters.TareaCronogramaAdapter
import cr.una.buildify.ui.general.cronograma.modelo.ListaTareasCronograma
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma
import java.util.Date

class CronogramaFragment : Fragment() {

    private var _binding: FragmentCronogramaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentCronogramaBinding.inflate(inflater, container, false)
        val root: View = binding.root*/
        val view:View = inflater.inflate(R.layout.fragment_cronograma,container,false)

        val recyclerViewCrono = view.findViewById<RecyclerView>(R.id.recyclerCrono)
        recyclerViewCrono.setHasFixedSize(true)
        recyclerViewCrono.layoutManager = LinearLayoutManager(view.context)
        val cronoAdapater = TareaCronogramaAdapter()
        recyclerViewCrono.adapter = cronoAdapater

        val listaPrueba = listOf<TareaCronograma>(TareaCronograma("tarea 1", "esta es una tarea de prueba",
            Date(),Date(),false),
            TareaCronograma("tarea 2", "esta es una tarea de prueba",
                Date(),Date(),false),
            TareaCronograma("tarea 3", "esta es una tarea de prueba",
                Date(),Date(),false)
        )

        cronoAdapater.listaTareas = listOf(
            TareaCronograma("test 1","tarea de prueba 1", Date(), Date(), true),
            TareaCronograma("test 2","tarea de prueba 1",Date(), Date(), false),
            TareaCronograma("test 3","tarea de prueba 1",Date(), Date(), true),
            TareaCronograma("test 4","tarea de prueba 1",Date(), Date(), false)
        )

        InitCalendarOptions(view,cronoAdapater)

//        Navigation.findNavController(view).navigate(R.id.nav_view)

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
    fun InitCalendarOptions(view:View, cronoAdapater:TareaCronogramaAdapter){
        val calendarView : CalendarView = view.findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cronoAdapater.listaTareas = listOf(TareaCronograma("test 1","tarea de prueba 2", Date(), Date(), true),
            TareaCronograma("test 2","tarea de prueba 2",Date(), Date(), false),
            TareaCronograma("test 3","tarea de prueba 2",Date(), Date(), true),
            TareaCronograma("test 4","tarea de prueba 2",Date(), Date(), false)
            )
            Navigation.findNavController(view).navigate(R.id.nav_add_task)
        }
    }
}