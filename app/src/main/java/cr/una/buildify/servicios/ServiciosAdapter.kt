package cr.una.buildify.servicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R



class ServiciosAdapter(private val servicios: List<Servicio>) : RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        holder.bind(servicios[position])
    }

    override fun getItemCount(): Int {
        return servicios.size
    }

    class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(servicio: Servicio) {
            itemView.findViewById<TextView>(R.id.tvTituloServicio).text = servicio.titulo
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = servicio.descripcion
            itemView.findViewById<TextView>(R.id.tvFecha).text = servicio.direccion
            itemView.findViewById<TextView>(R.id.tvEtapa).text = servicio.nombrePersona
            itemView.findViewById<TextView>(R.id.tvTelefono).text = servicio.telefono
            itemView.findViewById<TextView>(R.id.tvCorreo).text = servicio.correoElectronico
        }
    }
}
