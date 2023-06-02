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


        db.collection("Solicitud_Detalle")
            .get()
            .addOnSuccessListener {
                    resultado ->
                for (documento in resultado){
                    datos = "${documento.id}:${documento.data}\n "
                    val tabla = documento.toObject(solicitudDetalle::class.java)
                    if (tabla != null) {
                        serviciosList.add(tabla)

//RECYCLER VIEW
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        val adapter = solicitudDetalleAdapter(serviciosList)
                        recyclerView.adapter = adapter

                        recyclerView.layoutManager = LinearLayoutManager(this)

                        val itemSpacing = resources.getDimensionPixelSize(R.dimen.item_spacing)

// Crea una instancia de RecyclerViewItemDecoration y AGREGAMOS RecyclerView
                        val itemDecoration = RecyclerViewItemDecoration(itemSpacing)
                        recyclerView.addItemDecoration(itemDecoration)



                    }

                }


            }

            .addOnFailureListener{exception ->


            }

    }



}