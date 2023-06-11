package cr.una.buildify.tabla_costo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.solicitud_Detalle.solicitudDetalle

class tablaCostoAdapter(private val tablaCosto: MutableList<tablaCosto>) : RecyclerView.Adapter<tablaCostoAdapter.TablaCostoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablaCostoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_tabla_costos, parent, false)
        return TablaCostoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TablaCostoViewHolder, position: Int) {
        holder.bind(tablaCosto[position])
    }

    override fun getItemCount(): Int {
        return tablaCosto.size
    }

    class TablaCostoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(tablaCosto: tablaCosto) {
            itemView.findViewById<TextView>(R.id.columtv_prototipo).text = tablaCosto.prototipo
            itemView.findViewById<TextView>(R.id.columnatv_preciom2).text = tablaCosto.m2

            itemView.findViewById<TextView>(R.id.columnatv_precioTotal).text = tablaCosto.total
            itemView.findViewById<TextView>(R.id.columnatv_detalle).text = tablaCosto.fecha_actualizacion

        }
    }
}