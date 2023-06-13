package cr.una.buildify.tabla_costo

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentTablaCostosBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class tablaCostosFragment : Fragment() {

    private var _binding: FragmentTablaCostosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentTablaCostosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*Declaraciones*/
        lateinit var db: FirebaseFirestore
        var datos =""
        var serviciosList: MutableList<tablaCosto> = mutableListOf()
        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD
        val tablaLayout = binding.tablaid

        /*Extraemos los datos que se encuentran en la coleccion "Tabla-Costo" con el GET()*/

        db.collection("Tabla_Costo")
            .get()
            .addOnSuccessListener {
                    resultado ->
                for (documento in resultado){       /*Utilizamos el For para recorrer los datos BD*/
                    datos = "${documento.id}:${documento.data}\n"   /*Los datos que extraemos lo asignamos a la variable datos*/
                    val tabla = documento.toObject(tablaCosto::class.java)   /*Los datos lo convertimos a objeto "tablaCosto"*/
                    if (tabla != null) {
                        serviciosList.add(tabla)   /*Agregamos los objetos a la lista*/


/*Iniciamos la creacion de la tabla dinamica, la cantidad de filas se crea automaticamente
* dependiendo de la cantidad de objetos de la coleccion*/


/*Creamos los textView,Row, y botones desde el fragment Utilizando Context como autogeneracion */
                        val fila = TableRow(context)
                        val nombreTextView = TextView(context)
                        val preciom2TextView = TextView(context)
                        val preciototalTextView = TextView(context)
                        val btndetail= Button(context)

/*Le asignamos los valores de la lista  a las variables que vamos a mostrar en la tabla*/
                        nombreTextView.text = tabla.prototipo
                        preciom2TextView.text = tabla.m2
                        preciototalTextView.text = tabla.total



/*Asignamos formato a los textView y el boton desde el fragment*/
                        nombreTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        preciom2TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        preciototalTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        btndetail.setText("->")
                        btndetail.setTypeface(null, Typeface.BOLD)


/*Agregamos una fila con cada informacion del objeto*/
                        fila.addView(nombreTextView)
                        fila.addView(preciom2TextView)
                        fila.addView(preciototalTextView)
                        fila.addView(btndetail)

                        tablaLayout.addView(fila)


/*Al presionar el btn detalle podemos extraer de la lista mas informacion del prototipo*/

                        btndetail.setOnClickListener {

                            val alertDialog = AlertDialog.Builder(requireContext())
                                .setTitle("Deseas ver detalles de ${tabla.prototipo}?")
                                .setPositiveButton("Aceptar") { dialog, which ->
                                    val intent = Intent(requireContext(), Detail_tablaCosto::class.java)
                                    intent.putExtra("prototipo", tabla.prototipo)
                                    intent.putExtra("m2", tabla.m2)
                                    intent.putExtra("estructura", tabla.estructura)
                                    intent.putExtra("electrico", tabla.sistema_electrico)
                                    intent.putExtra("techo", tabla.techos)
                                    intent.putExtra("obligaciones", tabla.obligaciones)
                                    intent.putExtra("metros", tabla.m2_prototipo)
                                    intent.putExtra("total", tabla.total)
                                    startActivity(intent)


                                }
                                .setNegativeButton("Cancelar") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .create()

                            alertDialog.show()
                        }
                    }
                }




            }
            .addOnFailureListener{exception ->
            }






    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}