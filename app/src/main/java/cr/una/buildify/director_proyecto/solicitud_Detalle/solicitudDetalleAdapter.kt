package cr.una.buildify.director_proyecto.solicitud_Detalle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.director_proyecto.tabla_costo.tablaCosto


class solicitudDetalleAdapter (private val solicitudDetalle: List<solicitudDetalle>) : RecyclerView.Adapter<solicitudDetalleAdapter.solicitudDetalleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): solicitudDetalleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_director_proyecto_solicitud_detalle, parent, false)
        return solicitudDetalleViewHolder(view)
    }

    override fun onBindViewHolder(holder: solicitudDetalleViewHolder, position: Int) {
        holder.bind(solicitudDetalle[position])
    }

    override fun getItemCount(): Int {
        return solicitudDetalle.size
    }

    class solicitudDetalleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(solicitud: solicitudDetalle) {

        }
    }
}