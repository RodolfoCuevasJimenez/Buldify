package cr.una.buildify.ui.general.cronograma

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Proyecto
import cr.una.buildify.databinding.FragmentAddTareaBinding
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma
import cr.una.buildify.ui.general.servicios.proyecto.ProyectoRepositorio
import cr.una.buildify.ui.general.servicios.tarea.TareaRepositorio
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


class FragmentAddTask : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentAddTareaBinding? = null
    private val binding get() = _binding!!

    private var uid: String? = null
    private val listProyectos = mutableListOf<Proyecto>()
    private var idProyecto: String? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val simpleFormat = SimpleDateFormat("dd-MM-yyyy")
    private lateinit var dateSelected: LocalDate

    private val proyectoDB: ProyectoRepositorio = ProyectoRepositorio()
    private val tareaDB: TareaRepositorio = TareaRepositorio()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTareaBinding.inflate(inflater, container, false)
        val view: View = binding.root

        //val view: View = inflater.inflate(R.layout.fragment_add_tarea, container, false)
        val dropdownProyectos = view.findViewById<Spinner>(R.id.dropdown_proyecto)

        try {

            SetValoresIniciales(view)

            proyectoDB
                .getListaProyectos(this.uid!!)
                .addOnSuccessListener {
                    for (res in it) {
                        val proyecto = Proyecto(
                            res["id"].toString(),
                            "",
                            res["nombre"].toString(),
                            "",
                            "",
                            0.0,
                            "",
                            "",
                            null
                        )

                        listProyectos.add(proyecto)
                    }

                    SetDropdownProyectos(view, dropdownProyectos, listProyectos)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context, "Error al cargar la lista de proyectos.", Toast.LENGTH_SHORT
                    ).show()
                }

            view.findViewById<TextInputEditText>(R.id.txt_task_date_ini)
                .setOnClickListener {
                    SetDatePicker(view, R.id.txt_task_date_ini)
                }
            view.findViewById<TextInputEditText>(R.id.txt_task_date_fin)
                .setOnClickListener {
                    SetDatePicker(view, R.id.txt_task_date_fin)
                }
            view.findViewById<TextInputEditText>(R.id.txt_task_dateSelected_ini)
                .setOnClickListener {
                    OpenGetCalendarView(view, R.id.txt_task_dateSelected_ini)
                }

            view.findViewById<TextInputEditText>(R.id.txt_task_dateSelected_fin)
                .setOnClickListener {
                    OpenGetCalendarView(view, R.id.txt_task_dateSelected_fin)
                }

            view.findViewById<Button>(R.id.btn_add_task).setOnClickListener {

                val titulo =
                    view.findViewById<TextInputLayout>(R.id.txt_lay_ds).editText?.text?.toString()
                        ?: ""
                val desc =
                    view.findViewById<TextInputLayout>(R.id.txt_lay_td).editText?.text?.toString()
                        ?: ""
                val fechaIni = _binding?.txtLayDateSelectedIni?.editText?.text?.toString()
                val fechaFin = _binding?.txtLayDateSelectedFin?.editText?.text?.toString()

                val horaInicio =
                    view.findViewById<TextInputLayout>(R.id.txt_lay_di).editText?.text?.toString()
                        ?: ""
                val horaFin =
                    view.findViewById<TextInputLayout>(R.id.txt_lay_df).editText?.text?.toString()
                        ?: ""

                val tarea = TareaCronograma(
                    "",
                    titulo,
                    desc,
                    simpleFormat.parse(fechaIni.toString()).time,
                    simpleFormat.parse(fechaFin.toString()).time,
                    horaInicio,
                    horaFin,
                    false,
                    this.idProyecto ?: ""
                )
                tareaDB
                    .agregarTarea(tarea)
                    .addOnSuccessListener {
                        it.update("id", it.id).addOnSuccessListener {
                            Toast.makeText(context, "Tarea agregada", Toast.LENGTH_SHORT)
                                .show()

                            Navigation.findNavController(view).popBackStack()
                        }
                    }
            }

        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT)
                .show()
        }

        return view
    }

    /**
     * Funcion para iniciar el date picker
     * @author Nestor Pasos
     * @param [view] Vista
     * @param [editText] Id del edit text
     */
    private fun SetDatePicker(view: View, editText: Int) {
        val inflater: LayoutInflater =
            activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_window_datepicker, null)

        val time_picker = popupView.findViewById<TimePicker>(R.id.time_picker)

        val popupWindow: PopupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.btn_select_time).setOnClickListener {
            view.findViewById<TextInputEditText>(editText)
                .setText(GetTimePickerHoraFormato(time_picker.hour, time_picker.minute))
            popupWindow.dismiss()
        }
    }

    /**
     * Funcion para obtener la hora seleccionada en el timePicker con formato HH:mm AMPM
     * @param [hora] hora seleccionada
     * @param [minuto] minuto seleccionado
     * @return [String] string con formato formato HH:mm AMPM
     */
    private fun GetTimePickerHoraFormato(hora: Int, minuto: Int): String {
        var amPm = if (hora > 11) "PM" else "AM"
        val horaAux = if (hora > 12) "${hora - 12}" else if (hora == 0) "0$hora" else "$hora"
        val minutoAux = if (minuto < 10) "0${minuto}" else "${minuto}"

        return "$horaAux:$minutoAux $amPm"
    }

    /**
     * Funcion para iniciar valores locales
     * @author Nestor Pasos
     * @param [view] Vista
     */
    private fun SetValoresIniciales(view: View) {
        uid = arguments?.getString("uid")
        val tarea = arguments?.getString("tarea")
        val accion = arguments?.getString("accion")
        if (!tarea.isNullOrEmpty()) {

        }

        if (!tarea.isNullOrEmpty()) {
            var gson = Gson()
            SetValoresTarea(gson.fromJson(tarea, TareaCronograma::class.java), view)
        } else {
            val aux = arguments?.getString("fechaSeleccionada")

            val dateSelected = LocalDate.parse(aux!!)
            _binding?.txtLayDateSelectedIni?.editText?.setText(dateSelected.format(dateFormatter))
        }
    }

    /**
     * Funcion para iniciar el dropdown de la lista de los proyectos
     * @author Nestor Pasos
     * @param [view] Vista
     * @param [dropdown] Spinner donde se agregaran los proyectos
     * @param listProyectos la lista de los objectos Proyecto
     */
    private fun SetDropdownProyectos(
        view: View,
        dropdown: Spinner,
        listProyectos: MutableList<Proyecto>
    ) {
        var arrayAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            listProyectos.map { it.nombre }
        )
        dropdown.adapter = arrayAdapter

        dropdown.onItemSelectedListener = this@FragmentAddTask
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.idProyecto = this.listProyectos[position].id
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    /**
     * Funcion para colocar los valores del task a editar
     * @author Nestor Pasos
     * @param [tarea] tarea que tiene la informacion
     * @param [view] vista del fragment
     */
    private fun SetValoresTarea(tarea: TareaCronograma, view: View) {
        view.findViewById<TextInputLayout>(R.id.txt_lay_ds).editText?.setText(tarea.titulo)
        view.findViewById<TextInputLayout>(R.id.txt_lay_td).editText?.setText(tarea.descripcion)
        _binding?.txtLayDateSelectedIni?.editText?.setText(simpleFormat.format(Date(tarea.fechaIni)))
        view.findViewById<TextInputLayout>(R.id.txt_lay_di).editText?.setText(tarea.horaInicio)
        view.findViewById<TextInputLayout>(R.id.txt_lay_df).editText?.setText(tarea.horaFin)
    }

    fun OpenGetCalendarView(view: View, editText: Int) {
        val inflater: LayoutInflater =
            activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_window_calendar, null)

        val popupWindow: PopupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val calendarView = popupView.findViewById<CalendarView>(R.id.calendarView_popup)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            dateSelected = LocalDate.of(year, (month + 1), dayOfMonth)
        }

        popupView.findViewById<Button>(R.id.btn_add_date_calendar).setOnClickListener {
            val dateS = dateSelected.format(dateFormatter)

            view.findViewById<TextInputEditText>(editText)
                .setText(dateS)

            popupWindow.dismiss()
        }
    }
}