package cr.una.buildify.solicitud_Detalle


import android.graphics.Rect
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R


/*La clase adapter sera encargada de lo relacionado con la visualización del recycler view que mostrar la información de solicitud detalle*/

class solicitudDetalleAdapter(private val dataList: MutableList<solicitudDetalle>) :
    RecyclerView.Adapter<solicitudDetalleAdapter.ViewHolder>() {

/*La clase a continuación será la encargada de extraer la variable que va a recibir el objeto BD
* También le damos formato*/

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        init {
            textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Cambiar 18f al tamaño deseado en sp
            textViewTitle.setTextColor(ContextCompat.getColor(view.context, R.color.black)) //Cambiar R.color.colorPrimary al color deseado
        }
    }

    /*La función a continuación será la encargada Diseñar una base para el objeto a mostar en los item del recycler view*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_visualizacion_solicitud_detalle, parent, false)
        val itemBorder = ContextCompat.getDrawable(parent.context, R.drawable.item_border)
        view.background = itemBorder
        return ViewHolder(view)
    }

    /*La función a continuación será la encargada Asignarle el objeto a la variable inicializada en la parte superior*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = toStringSolicitud(position)
        holder.textViewTitle.text = item

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    /*La función a continuación le da estilo a los atributos del objeto*/
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
