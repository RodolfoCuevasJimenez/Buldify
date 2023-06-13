package cr.una.buildify.ui.VisualizarArchivos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cr.una.buildify.R

//Adaptador para Obtener de BD LOS RENDERS
class RenderAdapter(private var renderDocumentos: List<Render_Detalle>) : RecyclerView.Adapter<RenderAdapter.ViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(imageUrl: String)
    }

    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(listener: OnImageClickListener) {
        onImageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_images,
            parent,
            false
        )
        return ViewHolder(view)
    }

    //Vincular datos a las vistas dentro de un ViewHolder para una posición específica en el RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val renderDocumento = renderDocumentos[position]
        holder.bind(renderDocumento)
    }

    override fun getItemCount(): Int {
        return renderDocumentos.size
    }

    //la función setData actualiza los datos de la lista renderDocumentos con una nueva lista proporcionada, y notifica al adaptador para que refresque la vista del RecyclerView con los nuevos datos.
    fun setData(data: List<Render_Detalle>) {
        renderDocumentos = data.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.nombre_render)
        private val imageView: ImageView = itemView.findViewById(R.id.iv_storage_renders)

        fun bind(renderDocumento: Render_Detalle) {
            // Asignar el nombre del documento al TextView
            textViewTitle.text = renderDocumento.nombre

            // Utilizar Glide para cargar la imagen desde la URL y mostrarla en el ImageView
            Glide.with(itemView)
                .load(renderDocumento.ruta)
                .centerCrop()
                .into(imageView)

            // Configurar el click listener para el item del RecyclerView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val renderDocumento = renderDocumentos[position]
                    val imageUrl = renderDocumento.ruta

                    // Llamar al método onImageClick del listener registrado, pasando la URL de la imagen
                    onImageClickListener?.onImageClick(imageUrl)
                }
            }
        }
    }
}