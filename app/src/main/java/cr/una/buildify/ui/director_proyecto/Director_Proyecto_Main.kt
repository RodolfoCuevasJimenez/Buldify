package cr.una.buildify.ui.director_proyecto

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.GridLayout
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.director_proyecto_drawer
import cr.una.buildify.iniciosesion.inicioSesion

class Director_Proyecto_Main : Fragment() {

    private var _binding: FragmentDirectorProyectoMainBinding? = null

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

        _binding = FragmentDirectorProyectoMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setNavigationByButton(root)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crdResistrarDocs = binding.crdRegistroDocumentos
        crdResistrarDocs.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.cargar_Archivos_Fragment)
        }

        val crdIncidentes = binding.crdGestionIncidentes
        crdIncidentes.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizacion_inc_eva_obs_Fragment)
        }

        val crdServiciosInd = binding.crdServiciosInd
        crdServiciosInd.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.buscarServicioFragment)
        }

        val crdCrearProyecto = binding.crdCrearProyecto
        crdCrearProyecto.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.crearProyectoFragment)
        }

        val crdVisualizarProyecto = binding.crdVisualizarProy
        crdVisualizarProyecto.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizacionProyectosFragment)
        }

        val crdAvance = binding.crdRegistroAvance
        crdAvance.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.cargar_Progreso_Fragment)
        }

        val crdTablaCostos = binding.crdTablaCostos
        crdTablaCostos.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.tablaCostosFragment)
        }

        val crdDetalle = binding.crdDetallesObra
        crdDetalle.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizacionSolicitudDetalleFragment)
        }

        var cerrarSesion: Intent? = null
        cerrarSesion = Intent(activity,inicioSesion::class.java)

        val btnCerrarSesion = binding.btnCerrarSesion
        btnCerrarSesion.setOnClickListener{
            onDestroyView()
            onDetach()
            startActivity(cerrarSesion)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setNavigationByButton(view: View) {
        val gridLay = view.findViewById<GridLayout>(R.id.grid_menu_dir_proyecto)
        for (i in 1..gridLay.childCount) {
            if (gridLay.getChildAt(i) is CardView) {
                val cardView = gridLay.getChildAt(i) as CardView
                cardView.setOnClickListener {
                    when (cardView.id) {
                        R.id.btn_add_cronograma -> {
                            val tipo = activity?.intent?.extras?.getString("Tipo")
                            val uid = activity?.intent?.extras?.getString("UID")
                            var bundle = Bundle()
                            bundle.putString("UID", uid)
                            bundle.putString("tipo", tipo)
                            Navigation.findNavController(view).navigate(R.id.nav_crear_cronograma, bundle)
                        }
                    }

                }
            }
        }
    }
}