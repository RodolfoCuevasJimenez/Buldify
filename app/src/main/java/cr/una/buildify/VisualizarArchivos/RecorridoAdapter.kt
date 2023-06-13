package cr.una.buildify.ui.VisualizarArchivos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cr.una.buildify.R

//Adaptador para Obtener de BD LOS RECORRIDOS
class RecorridoAdapter(private var recorridoDocumentos: List<Recorrido_Detalle>) : RecyclerView.Adapter<RecorridoAdapter.ViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(videoUrl: String)
    }

    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(listener: OnImageClickListener) {
        onImageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_videos,
            parent,
            false
        )
        return ViewHolder(view)
    }

    //Vincular datos a las vistas dentro de un ViewHolder para una posición específica en el RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recorridoDocumento = recorridoDocumentos[position]
        holder.bind(recorridoDocumento)
    }

    override fun getItemCount(): Int {
        return recorridoDocumentos.size
    }

    //la función setData actualiza los datos de la lista recorridoDocumentos con una nueva lista proporcionada, y notifica al adaptador para que refresque la vista del RecyclerView con los nuevos datos.
    fun setData(data: List<Recorrido_Detalle>) {
        recorridoDocumentos = data.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.nombre_recorrido)
        private val imageViewThumbnail: ImageView = itemView.findViewById(R.id.vv)

        fun bind(recorridoDocumento: Recorrido_Detalle) {
            // Asignar el nombre del documento al TextView
            textViewTitle.text = recorridoDocumento.nombre

            // Utilizar Glide para cargar la imagen desde la URL y mostrarla en el ImageView
            Glide.with(itemView.context)
                .load(recorridoDocumento.ruta) // Utiliza la URL del video como carátula
                .into(imageViewThumbnail)

            // Configurar el click listener para el item del RecyclerView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val recorridoDocumento = recorridoDocumentos[position]
                    val videoUrl = recorridoDocumento.ruta

                    // Llamar al método onImageClick del listener registrado, pasando la URL del video
                    onImageClickListener?.onImageClick(videoUrl)
                }
            }
        }
    }
}