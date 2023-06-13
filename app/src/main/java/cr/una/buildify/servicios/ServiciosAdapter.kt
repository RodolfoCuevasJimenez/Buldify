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
class ServiciosAdapter(private val servicios: List<Servicio>, private val db: FirebaseFirestore, private val rol:String) : RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicioId = servicios[position].id
        holder.bind(servicios[position], db)
        if(rol!="Trabajador Independiente")
           holder.ibCalificar.setOnClickListener {
               holder.calificarServicio(holder.itemView.context, servicioId)
        }
        else{
            holder.ibCalificar.visibility=View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return servicios.size
    }

    inner class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ibCalificar: ImageButton = itemView.findViewById(R.id.ibCalificar)

        fun bind(servicio: Servicio, db: FirebaseFirestore) {
            itemView.findViewById<TextView>(R.id.tvTituloServicio).text = servicio.titulo
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = servicio.descripcion
            itemView.findViewById<TextView>(R.id.tvFecha).text = servicio.direccion
            itemView.findViewById<TextView>(R.id.tvEtapa).text = servicio.nombrePersona
            itemView.findViewById<TextView>(R.id.tvTelefono).text = servicio.telefono
            itemView.findViewById<TextView>(R.id.tvCorreo).text = servicio.correoElectronico
        }

        fun calificarServicio(context: Context, servicioId: String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Calificar servicio")
            builder.setMessage("Por favor, califique el servicio.")

            val ratingBar = RatingBar(context)
            ratingBar.numStars = 5
            ratingBar.stepSize = 1.0f

            val progressColor = ContextCompat.getColor(context, R.color.buttonColor)
            val backgroundColor = ContextCompat.getColor(context, R.color.teal_200)

            ratingBar.progressTintList = ColorStateList.valueOf(progressColor)
            ratingBar.progressBackgroundTintList = ColorStateList.valueOf(backgroundColor)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            ratingBar.layoutParams = layoutParams

            val container = LinearLayout(context)
            container.orientation = LinearLayout.VERTICAL
            container.addView(ratingBar)

            builder.setView(container)
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                val puntaje = ratingBar.rating
                Toast.makeText(context, "Calificación: $puntaje", Toast.LENGTH_SHORT).show()

                val servicioRef = db.collection("Servicios").document(servicioId)
                servicioRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val servicio = document.toObject(Servicio::class.java)

                            if (servicio != null) {
                                val calificacion = Calificacion(puntaje)

                                // Verificar si el servicio ya tiene calificaciones
                                if (servicio.calificaciones.isNullOrEmpty()) {
                                    servicio.calificaciones = arrayListOf(calificacion)
                                } else {
                                    servicio.calificaciones?.add(calificacion)
                                }

                                // Calcular el promedio de las calificaciones
                                val promedio =
                                    servicio.calificaciones?.map { it.puntaje }?.average()

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
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}


