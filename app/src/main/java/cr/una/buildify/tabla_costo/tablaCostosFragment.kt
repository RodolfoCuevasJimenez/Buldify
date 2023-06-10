package cr.una.buildify.tabla_costo

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
        lateinit var btn_cargar: Button
        lateinit var db: FirebaseFirestore
        lateinit var tablaAdapter: tablaCostoAdapter
        var datos =""
        var serviciosList: MutableList<tablaCosto> = mutableListOf()

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD
        val tablaLayout = binding.tablaid

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

                        val fila = TableRow(context)
                        val nombreTextView = TextView(context)
                        val preciom2TextView = TextView(context)
                        val preciototalTextView = TextView(context)
                        val estrucTextView = TextView(context)
                        //  val btndetail= Button(this@tablaCostoActivity)

                        nombreTextView.text = tabla.prototipo
                        preciom2TextView.text = tabla.m2

                        preciototalTextView.text = tabla.total
                        estrucTextView.text= tabla.fecha_actualizacion

                        nombreTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        preciom2TextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                        preciototalTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        estrucTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        //descrpcionTextView.text = tabla.descripcion
                        //btndetail.setText("->")
                        //btndetail.setTypeface(null, Typeface.BOLD)

                        // btndetail.setBackgroundResource(R.drawable.detail)
                        //btndetail.setBackgroundColor(ContextCompat.getColor(this, R.color.gris))
                        //btndetail.setTextColor(ContextCompat.getColor(this, R.color.white))

                        fila.addView(nombreTextView)
                        fila.addView(preciom2TextView)

                        fila.addView(preciototalTextView)
                        fila.addView(estrucTextView)

                        //fila.addView(btndetail)

                        tablaLayout.addView(fila)
                    }
                }
                // tablaAdapter = tablaCostoAdapter(serviciosList)
            }
            .addOnFailureListener{exception ->
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}