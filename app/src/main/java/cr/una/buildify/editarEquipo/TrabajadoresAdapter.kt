package cr.una.buildify.editarEquipo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.creacionProyecto.Trabajador


@Suppress("UNCHECKED_CAST")
class TrabajadoresAdapter(private var trabajadores: List<Trabajador>, private val context: Context, private val UID: String) : RecyclerView.Adapter<TrabajadoresAdapter.TrabajadorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrabajadorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_trabajador, parent, false)
        return TrabajadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrabajadorViewHolder, position: Int) {
        holder.bind(trabajadores[position])
    }

    override fun getItemCount(): Int {
        return trabajadores.size
    }

    inner class TrabajadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(trabajador: Trabajador) {
            val correo = "Correo electrónico: \n" + trabajador.correo
            val rol = "Rol: " + trabajador.rol
            itemView.findViewById<TextView>(R.id.tvNombreTrabajador).text = trabajador.nombre
            itemView.findViewById<TextView>(R.id.tvRolTrabajador).text = rol
            itemView.findViewById<TextView>(R.id.tvCorreoTrabajador).text = correo
            itemView.findViewById<ImageButton>(R.id.btnBorrarTrabajador).setOnClickListener {
                mostrarDialog(adapterPosition)
            }
            itemView.findViewById<ImageButton>(R.id.btnEditarTrabajador).setOnClickListener{
                val intent = Intent(context, FormularioTrabajador::class.java).apply {
                    putExtra("Tipo", "Editar")
                    putExtra("uidProyecto", UID)
                    putExtra("posicionTrabajador", adapterPosition)
                    putExtra("nombreTrabajador", trabajador.nombre)
                    putExtra("rolTrabajador", trabajador.rol)
                    putExtra("correoTrabajador", trabajador.correo)
                }
                context.startActivity(intent)
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun mostrarDialog(posicion: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar Trabajador")
        builder.setMessage("¿Seguro que desea eliminar al trabajador?")
        builder.setPositiveButton("Aceptar") { dialog, which ->
            val db = FirebaseFirestore.getInstance()
            val coleccion = db.collection("Proyectos")
            val documento = coleccion.document(UID)

            val trabajador = trabajadores[posicion]
            val nombreTrabajador = trabajador.nombre

            documento.get()
                .addOnSuccessListener { documentSnapshot ->
                    val equipo = documentSnapshot.get("equipo") as? List<HashMap<String, String>>
                    val nuevosTrabajadores = mutableListOf<HashMap<String, String>>()

                    equipo?.forEach { fila ->
                        val nombre = fila["nombre"]
                        if (nombre != nombreTrabajador) {
                            nuevosTrabajadores.add(fila)
                        }
                    }

                    documento.update("equipo", nuevosTrabajadores)
                        .addOnSuccessListener {
                            val nuevaListaTrabajadores = mutableListOf<Trabajador>()
                            nuevosTrabajadores.forEachIndexed { index, fila ->
                                val nombre = fila["nombre"]!!
                                val rol = fila["rol"]!!
                                val correo = fila["correo"]!!
                                val trabajadorTemp = Trabajador(index, nombre, rol, correo)
                                nuevaListaTrabajadores.add(trabajadorTemp)
                            }

                            trabajadores = nuevaListaTrabajadores
                            notifyDataSetChanged()
                            Toast.makeText(context, "Se ha eliminado el trabajador.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "No se ha eliminado el trabajador.", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "No se ha eliminado el trabajador.", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

}