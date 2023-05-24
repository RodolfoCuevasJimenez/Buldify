package cr.una.buildify.visualizacionDeProyectos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Proyecto
import cr.una.buildify.editarEquipo.EditarEquipoDeTrabajo
import kotlin.math.roundToInt


class ProyectosAdapter(private val proyectos: List<Proyecto>) : RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        holder.bind(proyectos[position])
    }

    override fun getItemCount(): Int {
        return proyectos.size
    }

    class ProyectoViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(proyecto: Proyecto) {
            val presupuesto = "Presupuesto:" + proyecto.moneda + ((proyecto.presupuesto * 100.0).roundToInt() / 100.0).toString()
            val tipo = "Tipo: " + proyecto.tipo
            val cliente = "Cliente: "
            itemView.findViewById<TextView>(R.id.tvNombreTrabajador).text = proyecto.nombre
            itemView.findViewById<TextView>(R.id.tvCorreoTrabajdor).text = tipo
            itemView.findViewById<TextView>(R.id.tvCorreoTrabajador).text = presupuesto
            itemView.findViewById<TextView>(R.id.tvCliente).text = cliente
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = proyecto.descripcion
            itemView.findViewById<ImageButton>(R.id.ibtnEquipo).setOnClickListener(View.OnClickListener {
                context.startActivity(Intent(context, EditarEquipoDeTrabajo::class.java).apply {
                    putExtra("UIDPROYECTO", proyecto.id)
                })
            })
        }
    }
}