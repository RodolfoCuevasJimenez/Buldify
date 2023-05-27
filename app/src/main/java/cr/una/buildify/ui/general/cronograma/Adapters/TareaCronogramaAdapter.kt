package cr.una.buildify.ui.general.cronograma.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.ui.general.cronograma.modelo.ListaTareasCronograma
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma
import java.text.SimpleDateFormat

class TareaCronogramaAdapter :
    RecyclerView.Adapter<TareaCronogramaAdapter.TareaCronogramaViewHolder>() {

    var listaTareas = listOf<TareaCronograma>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
    }

    class TareaCronogramaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.title_task)
        val description = view.findViewById<TextView>(R.id.description_task)
        val fechaTask = view.findViewById<TextView>(R.id.task_time)
        val completoTask = view.findViewById<TextView>(R.id.task_complete)
        val completoTaskIcon = view.findViewById<ImageView>(R.id.ic_complete)

        fun bind(tarea: TareaCronograma) {
            title.text = tarea.titulo
            description.text = tarea.descripcion
            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
            fechaTask.text =
                String.format("%s - %s", sdf.format(tarea.fechaInicio), sdf.format(tarea.fechaFin))
            if (tarea.estaCompleta == true) {
                completoTask.text = "Completa"
                completoTaskIcon.setImageResource(R.drawable.ic_finish)
            } else {
                completoTask.text = "En progreso"
                completoTaskIcon.setImageResource(R.drawable.ic_in_progress)
            }

        }
    }
}