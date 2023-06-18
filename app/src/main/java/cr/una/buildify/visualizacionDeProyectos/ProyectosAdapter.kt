package cr.una.buildify.visualizacionDeProyectos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Proyecto
import cr.una.buildify.editarEquipo.CambiarDirectorActivity
import cr.una.buildify.editarEquipo.EditarEquipoDeTrabajo
import kotlin.math.roundToInt

class ProyectosAdapter(
    private val proyectos: List<Proyecto>,
    private val tipoUsuario: String,
    private val db: FirebaseFirestore
) : RecyclerView.Adapter<ProyectosAdapter.ProyectoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto, parent, false)
        return ProyectoViewHolder(view, parent.context, tipoUsuario, db)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        // Vincular los datos del proyecto a la vista correspondiente
        holder.bind(proyectos[position])
    }

    override fun getItemCount(): Int {
        // Devolver el número total de proyectos en la lista
        return proyectos.size
    }

    class ProyectoViewHolder(
        itemView: View,
        private val context: Context,
        private val tipoUsuario: String,
        private val db: FirebaseFirestore
    ) : RecyclerView.ViewHolder(itemView) {
        private interface NombreUsuarioCallback {
            fun onNombreUsuarioObtenido(nombre: String)
        }

        fun bind(proyecto: Proyecto) {
            // Asignar los datos del proyecto a los elementos de la vista
            val presupuesto =
                "Presupuesto: " + proyecto.moneda + String.format("%.2f", ((proyecto.presupuesto * 100.0).roundToInt() / 100.0))
            val tipo = "Tipo: " + proyecto.tipo
            val clienteTextView: TextView = itemView.findViewById(R.id.tvCliente)
            itemView.findViewById<TextView>(R.id.tvNombreTrabajador).text = proyecto.nombre
            itemView.findViewById<TextView>(R.id.tvTipo).text = tipo
            itemView.findViewById<TextView>(R.id.tvPresupuesto).text = presupuesto
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = proyecto.descripcion

            if (tipoUsuario == "Director de Proyecto") {
                // Configurar el listener del botón para iniciar una actividad al hacer clic
                getNombreUsuario(proyecto.idCliente, object : NombreUsuarioCallback {
                    override fun onNombreUsuarioObtenido(nombre: String) {
                        val cliente = "Dueño de la obra: $nombre"
                        clienteTextView.text = cliente
                    }
                })
                itemView.findViewById<ImageButton>(R.id.ibtnDirector).visibility = View.GONE
                itemView.findViewById<ImageButton>(R.id.ibtnEquipo).visibility = View.VISIBLE
                itemView.findViewById<ImageButton>(R.id.ibtnEquipo).setOnClickListener {
                    context.startActivity(Intent(context, EditarEquipoDeTrabajo::class.java).apply {
                        putExtra("UIDPROYECTO", proyecto.id)
                    })
                }
            } else {
                getNombreUsuario(proyecto.idDirector, object : NombreUsuarioCallback {
                    override fun onNombreUsuarioObtenido(nombre: String) {
                        val cliente = "Director del Proyecto: $nombre"
                        clienteTextView.text = cliente
                    }
                })
                itemView.findViewById<ImageButton>(R.id.ibtnEquipo).visibility = View.GONE
                itemView.findViewById<ImageButton>(R.id.ibtnDirector).visibility = View.VISIBLE
                itemView.findViewById<ImageButton>(R.id.ibtnDirector).setOnClickListener {
                    context.startActivity(Intent(context, CambiarDirectorActivity::class.java).apply {
                        putExtra("UIDProyecto", proyecto.id)
                    })
                }
            }
        }

        private fun getNombreUsuario(id: String, callback: NombreUsuarioCallback) {
            db.collection("Usuarios").document(id).get()
                .addOnSuccessListener { documentSnapshot ->
                    val nombre = documentSnapshot.getString("nombre") ?: ""
                    callback.onNombreUsuarioObtenido(nombre)
                }
                .addOnFailureListener {
                    callback.onNombreUsuarioObtenido("Error al obtener el nombre")
                }
        }
    }
}
