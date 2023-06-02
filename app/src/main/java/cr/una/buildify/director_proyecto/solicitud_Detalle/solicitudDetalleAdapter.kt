package cr.una.buildify.director_proyecto.solicitud_Detalle

import android.content.Context
import android.graphics.Rect
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import cr.una.buildify.director_proyecto.tabla_costo.tablaCosto


class solicitudDetalleAdapter(private val dataList: MutableList<solicitudDetalle>) :
    RecyclerView.Adapter<solicitudDetalleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        init {
           textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Cambiar 18f al tamaño deseado en sp
           textViewTitle.setTextColor(ContextCompat.getColor(view.context, R.color.black)) //mbiar R.color.colorPrimary al color deseado
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_diector_visualizar_solicitud_detalle, parent, false)
        val itemBorder = ContextCompat.getDrawable(parent.context, R.drawable.item_border)
        view.background = itemBorder
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = toStringSolicitud(position)
        holder.textViewTitle.text = item

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun toStringSolicitud(position: Int): Spanned {
        val id = "<b>ID:</b>    ${dataList[position].id}"
        val nombre = "<b>Nombre:</b>   ${dataList[position].nombre}"
        val tipo = "<b>Tipo:</b>    ${dataList[position].tipo}"
        val area = "<b>Área:</b>   ${dataList[position].area}"
        val detalle = "<b>Detalle:</b>    ${dataList[position].detalle}"

        val formattedText = "$id<br>$nombre<br>$tipo<br>$area<br>$detalle"
        return Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
    }
}


class RecyclerViewItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // Aplica el espacio solo a los elementos excepto el último
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = space
        }
    }
}
