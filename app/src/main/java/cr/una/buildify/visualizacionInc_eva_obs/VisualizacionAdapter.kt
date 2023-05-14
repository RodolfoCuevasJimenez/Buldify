package cr.una.buildify.visualizacionInc_eva_obs

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
           holder.etObservaciones.setText(visualizacion.observaciones)
           holder.etObservaciones.isEnabled = false
           holder.btnGuardar.isEnabled = false
       } else {
           holder.etObservaciones.isEnabled = true
           holder.btnGuardar.isEnabled = true
       }

       holder.btnGuardar.setOnClickListener {
           val observaciones = holder.etObservaciones.text.toString()

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
                           holder.etObservaciones.isEnabled = false
                           holder.btnGuardar.isEnabled = false
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
        val etObservaciones = itemView.findViewById<EditText>(R.id.etObservaciones)

        fun bind(visualizacion: Visualizacion) {
            itemView.findViewById<TextView>(R.id.tvEtapa).text = visualizacion.etapa
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = visualizacion.descripcion
           // itemView.findViewById<TextView>(R.id.tvFecha).text = visualizacion.fecha.toString()
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







