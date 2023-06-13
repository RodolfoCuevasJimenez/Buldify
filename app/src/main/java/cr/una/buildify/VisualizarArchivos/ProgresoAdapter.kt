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

//Adaptador para Obtener de BD LAS EVIDENCIAS
class ProgresoAdapter(private var progresoDocumentos: List<Progreso_Detalle>) : RecyclerView.Adapter<ProgresoAdapter.ViewHolder>() {

    interface OnImageClickListener {
        fun onImageClick(imageUrl: String, tipo: String)
    }

    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(listener: OnImageClickListener) {
        onImageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_images_videos,
            parent,
            false
        )
        return ViewHolder(view)
    }

    //Vincular datos a las vistas dentro de un ViewHolder para una posición específica en el RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val progresoDocumento = progresoDocumentos[position]
        holder.bind(progresoDocumento)
    }

    override fun getItemCount(): Int {
        return progresoDocumentos.size
    }

    //la función setData actualiza los datos de la lista progresoDocumentos con una nueva lista proporcionada, y notifica al adaptador para que refresque la vista del RecyclerView con los nuevos datos.
    fun setData(data: List<Progreso_Detalle>) {
        progresoDocumentos = data.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.nombre_progreso)
        private val imageView: ImageView = itemView.findViewById(R.id.iv_storage_progreso)

        fun bind(progresoDocumento: Progreso_Detalle) {
            // Asignar el nombre del documento al TextView
            textViewTitle.text = progresoDocumento.nombre

            // Utilizar Glide para cargar la imagen desde la URL y mostrarla en el ImageView
            Glide.with(itemView.context)
                .load(progresoDocumento.ruta) // Utiliza la URL del video como carátula
                .into(imageView)

            // Configurar el click listener para el item del RecyclerView
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val renderDocumento = progresoDocumentos[position]
                    val imageUrl = renderDocumento.ruta

                    // Obtener el nombre del video a partir del nombre del documento
                    val video = progresoDocumento.nombre.split('.').map { it -> it.trim() }
                    Log.i("aaa", video.get(1).toString())

                    // Llamar al método onImageClick del listener registrado, pasando la URL de la imagen y el nombre del video
                    onImageClickListener?.onImageClick(imageUrl, video.get(1).toString())
                }
            }
        }
    }
}