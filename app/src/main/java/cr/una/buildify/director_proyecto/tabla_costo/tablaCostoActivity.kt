package cr.una.buildify.director_proyecto.tabla_costo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R

import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat


class tablaCostoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diector_tablacosto)

        lateinit var btn_cargar: Button
        lateinit var db: FirebaseFirestore
        lateinit var tablaAdapter: tablaCostoAdapter
        var datos =""
        var serviciosList: MutableList<tablaCosto> = mutableListOf()

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD
        val tablaLayout = findViewById<TableLayout>(R.id.tablaid)

//scroll
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val otherViewHeight = findViewById<View>(R.id.tablaid).height
        scrollView.layoutParams.height = screenHeight - otherViewHeight
//fin scroll

        db.collection("Tabla_Costo")
            .get()
            .addOnSuccessListener {
                    resultado ->
                for (documento in resultado){
                    datos = "${documento.id}:${documento.data}\n"
                    val tabla = documento.toObject(tablaCosto::class.java)
                    if (tabla != null) {
                        serviciosList.add(tabla)
//aqui creamos la tabla dinamica

                        val fila = TableRow(this@tablaCostoActivity)
                        val nombreTextView = TextView(this@tablaCostoActivity)
                        val preciom2TextView = TextView(this@tablaCostoActivity)
                        val preciototalTextView = TextView(this@tablaCostoActivity)
                        val btndetail= Button(this@tablaCostoActivity)

                        nombreTextView.text = tabla.prototipo
                        preciom2TextView.text = tabla.m2
                        preciototalTextView.text = tabla.total

                        nombreTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                        preciom2TextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                        preciototalTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                        //descrpcionTextView.text = tabla.descripcion
                        btndetail.setText("->")
                        btndetail.setTypeface(null, Typeface.BOLD)
                       // btndetail.setBackgroundResource(R.drawable.detail)
                        //btndetail.setBackgroundColor(ContextCompat.getColor(this, R.color.gris))
                        //btndetail.setTextColor(ContextCompat.getColor(this, R.color.white))

                        fila.addView(nombreTextView)
                        fila.addView(preciom2TextView)
                        fila.addView(preciototalTextView)
                        fila.addView(btndetail)

                        tablaLayout.addView(fila)


                    }
                }
               // tablaAdapter = tablaCostoAdapter(serviciosList)


            }

            .addOnFailureListener{exception ->


            }

    }





    }





