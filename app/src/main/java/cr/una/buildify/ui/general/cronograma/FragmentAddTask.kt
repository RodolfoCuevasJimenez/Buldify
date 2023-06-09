package cr.una.buildify.ui.general.cronograma

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentAddTareaBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.ArrayList
import java.util.Calendar


class FragmentAddTask : Fragment() {

    private var _binding: FragmentAddTareaBinding? = null
    private var db = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var timeSelected: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentCronogramaBinding.inflate(inflater, container, false)
        val root: View = binding.root*/
        val view: View = inflater.inflate(R.layout.fragment_add_tarea, container, false)

        var nombreProyectos = mutableListOf<String>()

        val listaProyectos = db.collection("Proyectos").get().addOnSuccessListener {
            for (result in it){
                nombreProyectos.add(result["nombre"].toString())
            }
        }

        var arrayAdapter = ArrayAdapter(view.context,
            android.R.layout.simple_spinner_item, nombreProyectos)

        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val aux = arguments?.getString("fechaSeleccionada")
        val dateSelected = LocalDate.parse(aux!!)
        view.findViewById<TextView>(R.id.date_selected).setText(dateSelected.toString())
        val txtDateIni = view.findViewById<TextInputEditText>(R.id.txt_task_date_ini)
        txtDateIni.setOnClickListener {
            SetDatePicker(view, R.id.txt_task_date_ini)
        }



        txtDateIni.setOnClickListener {
            SetDatePicker(view, R.id.txt_task_date_fin)
        }

        return view
    }

    private fun SetDatePicker(view: View, editText: Int) {
        val inflater: LayoutInflater =
            activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater//. view.la LayoutInflater.from(view.context)
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
        val horaAux =
            if (hora > 12) "${hora - 12}" else if (hora == 0) "0$hora" else "$hora"
        val minutoAux =
            if (minuto < 10) "0${minuto}" else "${minuto}"

        return "$horaAux:$minutoAux $amPm"
    }
}