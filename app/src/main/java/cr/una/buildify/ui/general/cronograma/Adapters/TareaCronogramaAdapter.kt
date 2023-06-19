package cr.una.buildify.ui.general.cronograma.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import cr.una.buildify.R
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

/**
 * Clase para recycler view de cronograma
 * @author Nestor Pasos
 */
class TareaCronogramaAdapter :
    RecyclerView.Adapter<TareaCronogramaAdapter.TareaCronogramaViewHolder>() {

    var listaTareas = listOf<TareaCronograma>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var tareaSeleccionad: TareaCronograma? = null

    var parentView: View? = null
    var uid: String? = null
    var tipo: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaCronogramaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tarea_list_item, parent, false)
        return TareaCronogramaViewHolder(view)
    }

    override fun getItemCount() = listaTareas.size

    override fun onBindViewHolder(holder: TareaCronogramaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        holder.bind(tarea)
        this.tareaSeleccionad = tarea

        if (tipo?.lowercase() != "dueño de la obra") {

            holder.itemView.setOnClickListener {
                var gson = Gson()
                val bundle = Bundle()
                bundle.putString("tarea", gson.toJson(tarea))
                bundle.putString("uid", uid)
                bundle.putString("tipo", tipo)
                bundle.putString("accion", "update")

                Navigation.findNavController(this.parentView!!).navigate(R.id.nav_updt_task, bundle)
            }
        }
    }

    class TareaCronogramaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title_task)
        val description = view.findViewById<TextView>(R.id.description_task)
        val fechaTask = view.findViewById<TextView>(R.id.task_time)
        val completoTask = view.findViewById<TextView>(R.id.task_complete)
        val completoTaskIcon = view.findViewById<ImageView>(R.id.ic_complete)
        val taskDate = view.findViewById<TextView>(R.id.task_date)

        val simpleFormat = SimpleDateFormat("dd-MM-yyyy")

        fun bind(tarea: TareaCronograma) {
            title.text = tarea.titulo
            description.text = tarea.descripcion
            fechaTask.text =
                String.format("%s - %s", tarea.horaInicio, tarea.horaFin)
            val iniDate = Date(tarea.fechaIni)
            val finDate = Date(tarea.fechaFin)

            taskDate.text =
                String.format("%s - %s", simpleFormat.format(iniDate), simpleFormat.format(finDate))

            //Hora inicio
            val arrayHoraIni =
                if (tarea.horaInicio.trim().length == 7) tarea.horaInicio.substring(0, 4)
                    .split(":") else tarea.horaInicio.substring(0, 5).split(":")

            var horaI = arrayHoraIni[0].trim().toInt()
            val minI = arrayHoraIni[1].trim().toInt()
            val ampmIni = if (tarea.horaInicio.trim().length == 7) tarea.horaInicio.trim()
                .substring(5, 7) else tarea.horaInicio.trim().substring(6, 8)

            if (ampmIni == "PM") {
                horaI += 12
            }

            //Hora fin
            val arrayHoraFin = if (tarea.horaFin.trim().length == 7) tarea.horaFin.substring(0, 4)
                .split(":") else tarea.horaFin.substring(0, 5).split(":")

            var horaF = arrayHoraFin[0].trim().toInt()
            val minF = arrayHoraFin[1].trim().toInt()
            val ampmFin = if (tarea.horaFin.trim().length == 7) tarea.horaFin.trim()
                .substring(5, 7) else tarea.horaFin.trim().substring(6, 8)

            if (ampmFin == "PM") {
                horaF += 12
            }

            if (tarea.estaCompleta) {
                completoTask.text = "Completa"
                completoTaskIcon.setImageResource(R.drawable.ic_finish)
            } else {
                val format = SimpleDateFormat("yyyy-MM-dd")
                if ((LocalDate.now() > LocalDate.parse(format.format(finDate)))
                    || (LocalDate.now() == LocalDate.parse(format.format(finDate))
                            && LocalTime.now().hour > horaF
                            )
                ) {
                    completoTask.text = "Atrasado"
                    completoTaskIcon.setImageResource(R.drawable.ic_reloj)
                } else if ((LocalDate.now() < LocalDate.parse(format.format(iniDate)))
                    || (LocalDate.now() == LocalDate.parse(format.format(iniDate))
                            && LocalTime.now().hour < horaI && LocalTime.now().minute < minI
                            )
                ) {
                    completoTask.text = "Nuevo"
                    completoTaskIcon.setImageResource(R.drawable.ic_notification_new)
                } else {
                    completoTask.text = "En progreso"
                    completoTaskIcon.setImageResource(R.drawable.ic_in_progress)
                }
            }
        }
    }
}