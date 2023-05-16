package cr.una.buildify.visualizacionInc_eva_obs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp



class VisualizacionAdapter(private val visualizacionList: MutableList<Visualizacion>) :
    RecyclerView.Adapter<VisualizacionAdapter.VisualizacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisualizacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inc_eva_obs, parent, false)
        return VisualizacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisualizacionViewHolder, position: Int) {
        val visualizacion = visualizacionList[position]

        holder.bind(visualizacion)
    }

    override fun getItemCount(): Int {
        return visualizacionList.size
    }

    class VisualizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(visualizacion: Visualizacion) {
            itemView.findViewById<TextView>(R.id.tvEtapa).text = visualizacion.etapa
            itemView.findViewById<TextView>(R.id.tvDescripcionV).text = visualizacion.descripcion
            itemView.findViewById<TextView>(R.id.tvObservaciones).text=visualizacion.observaciones
            val timestamp: Timestamp? = visualizacion.fecha
            if (timestamp != null) {
                val date: Date = timestamp.toDate()
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateString = formatter.format(date)
                itemView.findViewById<TextView>(R.id.tvFecha).text = dateString
            }


        }
    }
}









