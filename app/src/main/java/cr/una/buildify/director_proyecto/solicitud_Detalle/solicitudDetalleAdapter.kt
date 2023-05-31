package cr.una.buildify.director_proyecto.solicitud_Detalle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.director_proyecto.tabla_costo.tablaCosto

/*
    class solicitudDetalleAdapter(private val context: Context, private val dataList: List<solicitudDetalle>) : BaseAdapter() {



        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Inflar el layout de cada elemento del GridView
            val view = LayoutInflater.from(context).inflate(R.layout.activity_diector_visualizar_solicitud_detalle, parent, false)

            // Obtener referencia a los elementos de la vista
            val tv_name_solicitud = view.findViewById<TextView>(R.id.tv_name_solicitud)
            val tv_area_solicitud = view.findViewById<TextView>(R.id.tv_area_solicitud)
            val tv_tipo_solicitud = view.findViewById<TextView>(R.id.tv_tipo_solicitud)
            val tv_detalle_solicitud = view.findViewById<TextView>(R.id.tv_detalle_solicitud)

            // Obtener el objeto de la lista en la posición actual
            val objeto = dataList[position]

            // Asignar los valores del objeto a los elementos de la vista
            tv_name_solicitud.text = dataList[position].nombre
            tv_area_solicitud.text = dataList[position].area
            tv_tipo_solicitud.text = dataList[position].tipo
            tv_detalle_solicitud.text = dataList[position].detalle


            return view
        }

        override fun getItem(position: Int): Any {
            return dataList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return dataList.size
        }


}*/


class solicitudDetalleAdapter(private val dataList: MutableList<solicitudDetalle>) :
    RecyclerView.Adapter<solicitudDetalleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        // Aumentar tamaño de la letra

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_diector_visualizar_solicitud_detalle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = toStringSolicitud(position)
        holder.textViewTitle.text = item

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

     fun toStringSolicitud(position: Int): String {

        return "ID:   ${dataList[position].id}\n" +
                "Nombre:   ${dataList[position].nombre}\n" +
                "Tipo:   ${dataList[position].tipo}\n" +
                "Área:   ${dataList[position].area}\n" +
                "Detalle:   ${dataList[position].detalle}"
    }
}