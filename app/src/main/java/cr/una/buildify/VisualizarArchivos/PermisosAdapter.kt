package cr.una.buildify.ui.VisualizarArchivos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import cr.una.buildify.R
import java.io.File

//Adaptador para Obtener de BD LOS PERMISOS
class PermisosAdapter(private var pdfFiles: List<Permisos_Detalle>) : RecyclerView.Adapter<PermisosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_pdf,
            parent,
            false
        )
        return ViewHolder(view)
    }

    //Vincular datos a las vistas dentro de un ViewHolder para una posición específica en el RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val planosDocumento = pdfFiles[position]
        holder.bind(planosDocumento)
    }

    override fun getItemCount(): Int {
        return pdfFiles.size
    }

    //la función setData actualiza los datos de la lista pdfFiles con una nueva lista proporcionada, y notifica al adaptador para que refresque la vista del RecyclerView con los nuevos datos.
    fun setData(data: List<Permisos_Detalle>) {
        pdfFiles = data.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pdfImageView: ImageView = itemView.findViewById(R.id.iv_storage_planos)
        private val pdfNameTextView: TextView = itemView.findViewById(R.id.nombre_plano)

        fun bind(permisosDocumento: Permisos_Detalle) {
            // Asignar el nombre del documento al TextView
            pdfNameTextView.text = permisosDocumento.nombre

            // Obtener una referencia al Firebase Storage y al archivo PDF correspondiente
            val storageReference = FirebaseStorage.getInstance().reference
            val pdfRef = storageReference.child("Permisos Usuarios/${permisosDocumento.nombre}.pdf")

            // Crear un archivo local en la caché del dispositivo
            val localFile = File(itemView.context.cacheDir, permisosDocumento.nombre)

            // Descargar el archivo PDF desde Firebase Storage al archivo local
            pdfRef.getFile(localFile)
                .addOnSuccessListener {
                    // Si la descarga es exitosa, crear un objeto PdfRenderer para procesar el PDF
                    val pdfRenderer = PdfRenderer(
                        ParcelFileDescriptor.open(
                            localFile,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )

                    // Abrir la primera página del PDF
                    val currentPage = pdfRenderer.openPage(0)

                    // Crear un Bitmap y renderizar la página del PDF en el Bitmap
                    val bitmap = Bitmap.createBitmap(
                        currentPage.width,
                        currentPage.height,
                        Bitmap.Config.ARGB_8888
                    )
                    currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // Cerrar la página y el PdfRenderer
                    currentPage.close()
                    pdfRenderer.close()

                    // Mostrar el Bitmap en el ImageView
                    pdfImageView.setImageBitmap(bitmap)
                }
                .addOnFailureListener { exception ->
                    // En caso de error durante la descarga del archivo PDF, se puede agregar un manejo de errores aquí
                }

            // Configurar el click listener para el CardView
            itemView.setOnClickListener {
                val context = itemView.context
                val pdfPath = localFile.absolutePath

                // Abrir el archivo PDF utilizando una aplicación de terceros
                openPdfExternalApp(context, pdfPath)
            }
        }
    }

    private fun openPdfExternalApp(context: Context, pdfFilePath: String) {
        // Crear un objeto File a partir de la ruta del archivo PDF
        val file = File(pdfFilePath)

        // Obtener el URI del archivo utilizando FileProvider para permitir el acceso seguro al mismo
        val uri = FileProvider.getUriForFile(context, "cr.una.buildify.provider", file)

        // Crear un intent con la acción ACTION_VIEW para abrir el archivo PDF
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Crear un Intent Chooser para mostrar las aplicaciones disponibles para abrir el archivo PDF
        val chooser = Intent.createChooser(intent, "Abrir con")

        // Verificar si hay alguna aplicación disponible para abrir el archivo PDF
        if (chooser.resolveActivity(context.packageManager) != null) {
            // Iniciar la actividad del Intent Chooser
            context.startActivity(chooser)
        } else {
            Toast.makeText(context, "No se encontró ninguna aplicación para abrir el archivo PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
