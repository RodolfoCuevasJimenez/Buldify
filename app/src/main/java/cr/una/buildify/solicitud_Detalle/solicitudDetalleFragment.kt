package cr.una.buildify.solicitud_Detalle

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentSolicitudDetalleBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel
import cr.una.buildify.ui.duenno_obra.DuennoObraMainViewModel

class solicitudDetalleFragment : Fragment() {

    private var _binding: FragmentSolicitudDetalleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(DuennoObraMainViewModel::class.java)

        _binding = FragmentSolicitudDetalleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateinit var btn_guardar: Button

        lateinit var db: FirebaseFirestore

        db = FirebaseFirestore.getInstance() //obtenemos instancia de BD



//extraemos los datos del formulario

        val idSolicitud = binding.etIdSolicitud
        var nombreSolicitud = binding.etNombreSolicitud
        var tipoSolicitud = binding.etTipoSolicitud
        val areaSolicitud = binding.etAreaSolicitud
        val detalleSolicitud = binding.etDetalleSolicitud
        btn_guardar = binding.btnAddSolicitud

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
                            context,
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
                            context,
                            "ERROR DATOS NO GUARDADOS ",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
        })

    }     //FIN LISTENER
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}