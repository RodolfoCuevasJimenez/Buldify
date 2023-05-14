package cr.una.buildify.visualizacionInc_eva_obs

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cr.una.buildify.R
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


class VisualizacionAdapter(private val visualizacionList: MutableList<Visualizacion>) :
    RecyclerView.Adapter<VisualizacionAdapter.VisualizacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisualizacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inc_eva_obs, parent, false)
        return VisualizacionViewHolder(view)
    }

   override fun onBindViewHolder(holder: VisualizacionViewHolder, position: Int) {
       val visualizacion = visualizacionList[position]

       holder.bind(visualizacion)

       if (visualizacion.observaciones.isNotEmpty()) {
           holder.mltObservaciones.setText(visualizacion.observaciones)
           holder.mltObservaciones.isEnabled = false
           holder.btnGuardar.isEnabled = false
           holder.btnGuardar.visibility = View.INVISIBLE
       } else {
           holder.mltObservaciones.isEnabled = true
           holder.btnGuardar.isEnabled = true
           holder.btnGuardar.visibility = View.VISIBLE
       }

       holder.btnGuardar.setOnClickListener {
           val observaciones = holder.mltObservaciones.text.toString()

           visualizacion.observaciones = observaciones

           val db = FirebaseFirestore.getInstance()
           val visualizacionRef = db.collection("Visualizacion")
               .whereEqualTo("etapa", visualizacion.etapa)
               .whereEqualTo("descripcion", visualizacion.descripcion)
               .limit(1)

           visualizacionRef.get().addOnSuccessListener { snapshot ->
               if (!snapshot.isEmpty) {
                   val document = snapshot.documents[0]
                   document.reference.update("observaciones", observaciones)
                       .addOnSuccessListener {
                           Log.d(TAG, "Observaciones actualizadas exitosamente en la base de datos")
                           holder.mltObservaciones.isEnabled = false
                           holder.btnGuardar.isEnabled = false
                           holder.btnGuardar.visibility = View.INVISIBLE
                           Toast.makeText(holder.itemView.context, "Observaciones guardadas correctamente", Toast.LENGTH_SHORT).show()
                       }
                       .addOnFailureListener {
                           Log.e(TAG, "Error al actualizar las observaciones en la base de datos", it)
                       }
               } else {
                   Log.e(TAG, "No se encontró la visualización correspondiente en la base de datos")
               }
           }.addOnFailureListener {
               Log.e(TAG, "Error al obtener la visualización correspondiente de la base de datos", it)
           }
       }
   }
    override fun getItemCount(): Int {
        return visualizacionList.size
    }

    class VisualizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnGuardar = itemView.findViewById<Button>(R.id.btnGuardar)
        val mltObservaciones = itemView.findViewById<EditText>(R.id.mltObservaciones)

        fun bind(visualizacion: Visualizacion) {
            itemView.findViewById<TextView>(R.id.tvEtapa).text = visualizacion.etapa
            itemView.findViewById<TextView>(R.id.tvDescripcionV).text = visualizacion.descripcion
            val timestamp: Timestamp? = visualizacion.fecha
            if (timestamp != null) {
                val date: Date = timestamp.toDate()
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateString = formatter.format(date)
                itemView.findViewById<TextView>(R.id.tvFecha).text = dateString
            }


        }
    }
}







