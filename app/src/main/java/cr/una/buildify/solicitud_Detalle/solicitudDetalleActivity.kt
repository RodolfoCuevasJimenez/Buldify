package cr.una.buildify.solicitud_Detalle

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import cr.una.buildify.R
import android.view.View


class solicitudDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_proyecto_solicitud_detalle)

        lateinit var btn_guardar: Button

        lateinit var db: FirebaseFirestore

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD



//extraemos los datos del formulario

        val idSolicitud = findViewById<EditText>(R.id.et_id_solicitud)
        var nombreSolicitud = findViewById<EditText>(R.id.et_nombre_solicitud)
        var tipoSolicitud = findViewById<EditText>(R.id.et_tipo_solicitud)
        val areaSolicitud = findViewById<EditText>(R.id.et_area_solicitud)
        val detalleSolicitud = findViewById<EditText>(R.id.et_detalle_solicitud)
        btn_guardar = findViewById<Button>(R.id.btn_add_solicitud)

        //fin extraer datos deñ formulario
        btn_guardar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val id = idSolicitud.text.toString()
                val nombre = nombreSolicitud.text.toString()
                val tipo = tipoSolicitud.text.toString()
                val area = areaSolicitud.text.toString()
                val detail = detalleSolicitud.text.toString()

                val datosAGuardar = hashMapOf(
                    "id" to id,
                    "nombre" to nombre,
                    "tipo" to tipo,
                    "area" to area,
                    "detalle" to detail
                )


                val solicitud = solicitudDetalle(
                    id, nombre, tipo,
                    area, detail
                )

               // nombreSolicitud.setText(solicitud.area_solicitud)

                db.collection("Solicitud_Detalle")
                    .document(id.toString())
                    .set(datosAGuardar)
                    .addOnSuccessListener {

                            Toast.makeText(
                                this@solicitudDetalleActivity,
                                "DATOS GUARDADOS CORRECTAMENTE",
                                Toast.LENGTH_SHORT
                            ).show()


                            //iTextViewPrecio.text= serviciosList.get(2).total
//limpia los datos
                        idSolicitud.text.clear()
                        nombreSolicitud.text.clear()
                        tipoSolicitud.text.clear()
                        areaSolicitud.text.clear()
                        detalleSolicitud.text.clear()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this@solicitudDetalleActivity,
                            "ERROR DATOS NO GUARDADOS ",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
        })

    }     //FIN LISTENER


}










