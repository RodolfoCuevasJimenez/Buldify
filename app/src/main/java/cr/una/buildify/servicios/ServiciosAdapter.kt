package cr.una.buildify.servicios


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R


class ServiciosAdapter(private val servicios: List<Servicio>, private val db: FirebaseFirestore, private val rol:String, private val idUsuario:String) : RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        // Infla el diseño de item_servicio en una nueva vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicioId = servicios[position].id
        holder.bind(servicios[position], db)
        // Verificar el rol y si el servicio ya fue calificado
        if (rol != "Trabajador Independiente" && !buscarCalificado(servicios[position])) {
            // Si el rol no es "Trabajador Independiente" y el servicio no ha sido calificado
            // Mostrar el botón de calificación y configurar su evento de clic
            holder.ibCalificar.visibility = View.VISIBLE
            holder.ibCalificar.setOnClickListener {
                holder.calificarServicio(holder.itemView.context, servicioId)
            }
        }
            else{
            // Si el rol es "Trabajador Independiente" o el servicio ya ha sido calificado
            // Ocultar el botón de calificación
                holder.ibCalificar.visibility = View.INVISIBLE
            }

    }

    override fun getItemCount(): Int {
        return servicios.size
    }
    private fun buscarCalificado(servicio: Servicio): Boolean {
        for(calificacion in servicio.calificaciones){
            // Verificar si la calificación corresponde al ID de usuario actual
            if(calificacion.idUsuario == idUsuario){
                return true
            }
        }
        return false
    }
    /**
     * Clase que representa un ViewHolder para un elemento de datos de Servicio en el RecyclerView.
     * @param itemView La vista raíz del elemento de datos.
     */
    inner class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ibCalificar: ImageButton = itemView.findViewById(R.id.ibCalificar)
        /**
         * Vincula los datos del servicio al ViewHolder.
         * @param servicio El objeto Servicio que se va a mostrar en el ViewHolder.
         */
        fun bind(servicio: Servicio, db: FirebaseFirestore) {
            // Vincular los datos del servicio con las vistas del ViewHolder
            itemView.findViewById<TextView>(R.id.tvTituloServicio).text = servicio.titulo
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = servicio.descripcion
            itemView.findViewById<TextView>(R.id.tvFecha).text = servicio.direccion
            itemView.findViewById<TextView>(R.id.tvEtapa).text = servicio.nombrePersona
            itemView.findViewById<TextView>(R.id.tvTelefono).text = servicio.telefono
            itemView.findViewById<TextView>(R.id.tvCorreo).text = servicio.correoElectronico
            itemView.findViewById<TextView>(R.id.tvCalificacionServicio).text= String.format("%.2f",servicio.calificacionGeneral)
            // Obtener la calificación del usuario asociada al servicio desde la base de datos
            db.collection("Usuarios").document(servicio.correoElectronico).get().addOnSuccessListener {
                val calificacion= it.get("Calificacion")
                if(calificacion!=null) {
                    // Mostrar la calificación general del usuario en la vista correspondiente
                    itemView.findViewById<TextView>(R.id.tvCalificacionGeneral).text = String.format("%.2f",calificacion)
                }else{
                    // Si no hay calificación, mostrar "0" en la vista correspondiente
                    itemView.findViewById<TextView>(R.id.tvCalificacionGeneral).text = "0"
                }

            }

        }

        fun calificarServicio(context: Context, servicioId: String) {
            // Crear un diálogo de alerta para la calificación del servicio
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Calificar servicio")
            builder.setMessage("Por favor, califique el servicio.")
            // Crear una barra de calificación (RatingBar)
            val ratingBar = RatingBar(context)
            ratingBar.numStars = 5
            ratingBar.stepSize = 1.0f
            // Configurar los colores de la barra de calificación
            val progressColor = ContextCompat.getColor(context, R.color.buttonColor)
            val backgroundColor = ContextCompat.getColor(context, R.color.teal_200)
            // Configurar los parámetros de diseño de la barra de calificación
            ratingBar.progressTintList = ColorStateList.valueOf(progressColor)
            ratingBar.progressBackgroundTintList = ColorStateList.valueOf(backgroundColor)
            // Configurar los parámetros de diseño de la barra de calificación
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            ratingBar.layoutParams = layoutParams
            // Crear un contenedor lineal (LinearLayout) para la barra de calificación
            val container = LinearLayout(context)
            container.orientation = LinearLayout.VERTICAL
            container.addView(ratingBar)

            builder.setView(container)
            // Configurar el botón "Aceptar" del diálogo
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                val puntaje = ratingBar.rating
                Toast.makeText(context, "Calificación: $puntaje", Toast.LENGTH_SHORT).show()
            // Obtener la referencia al servicio en la base de datos
                val servicioRef = db.collection("Servicios").document(servicioId)
                servicioRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val servicio = document.toObject(Servicio::class.java)

                            if (servicio != null) {
                                val calificacion = Calificacion(idUsuario, puntaje)
                                // Verificar si el servicio ya tiene calificaciones
                                if (servicio.calificaciones.isNullOrEmpty()) {
                                    servicio.calificaciones = arrayListOf(calificacion)
                                } else {
                                    servicio.calificaciones.add(calificacion)
                                }

                                // Calcular el promedio de las calificaciones
                                val promedio =
                                    servicio.calificaciones.map { it.puntaje }.average()

                                if (promedio != null) {
                                    // Actualizar el servicio con la nueva calificación general
                                    servicio.calificacionGeneral = promedio.toFloat()

                                    // Actualizar el servicio en la base de datos
                                    servicioRef.set(servicio)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Calificación registrada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error al actualizar el servicio",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.e(TAG, "Error al actualizar el servicio", e)
                                        }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al obtener el servicio", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error al obtener el servicio", e)
                    }
                dialog.dismiss()
            }
            // Configurar el botón "Cancelar" del diálogo
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }

    }
}


