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

        if(tipo?.lowercase() != "dueño de la obra") {

            holder.itemView.setOnClickListener {
                var gson = Gson()
                val bundle = Bundle()
                bundle.putString("tarea", gson.toJson(tarea))
                bundle.putString("uid", uid)
                bundle.putString("tipo", tipo)

                Navigation.findNavController(this.parentView!!).navigate(R.id.nav_add_task, bundle)
            }
        }
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
            fechaTask.text =
                String.format("%s - %s", tarea.horaInicio, tarea.horaFin)
            if (tarea.estaCompleta) {
                completoTask.text = "Completa"
                completoTaskIcon.setImageResource(R.drawable.ic_finish)
            } else {
                completoTask.text = "En progreso"
                completoTaskIcon.setImageResource(R.drawable.ic_in_progress)
            }

        }
    }
}