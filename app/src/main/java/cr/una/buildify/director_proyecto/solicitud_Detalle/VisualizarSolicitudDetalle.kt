package cr.una.buildify.director_proyecto.solicitud_Detalle

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R


class VisualizarSolicitudDetalle : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diector_visualizar_solicitud_detalle)


        lateinit var db: FirebaseFirestore



        var datos =""
        var serviciosList: MutableList<solicitudDetalle> = mutableListOf()

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD
      /*val gridView = findViewById<GridView>(R.id.gridView)
        val tv_detalle_solicitud = findViewById<TextView>(R.id.tv_detalle_solicitud)*/

        db.collection("Solicitud_Detalle")
            .get()
            .addOnSuccessListener {
                    resultado ->
                for (documento in resultado){
                    datos = "${documento.id}:${documento.data}\n "
                    val tabla = documento.toObject(solicitudDetalle::class.java)
                    if (tabla != null) {
                        serviciosList.add(tabla)
//aqui creamos la tabla dinamica


                       // tv_detalle_solicitud.text =tabla.detalle

                     /*   val adapter = solicitudDetalleAdapter(this, serviciosList)
                        gridView.adapter = adapter*/
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        val adapter = solicitudDetalleAdapter(serviciosList)
                        recyclerView.adapter = adapter


                    }





                }


            }

            .addOnFailureListener{exception ->
               // tv_detalle_solicitud.text ="no hay datos a extraer"

            }

    }



}