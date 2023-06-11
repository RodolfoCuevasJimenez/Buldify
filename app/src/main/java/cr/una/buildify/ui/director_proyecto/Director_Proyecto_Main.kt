package cr.una.buildify.ui.director_proyecto

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding

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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}