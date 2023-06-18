package cr.una.buildify.ui.duenno_obra

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDuennoObraMainBinding
import cr.una.buildify.iniciosesion.inicioSesion
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class Duenno_Obra_Main : Fragment() {

    private var _binding: FragmentDuennoObraMainBinding? = null

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

        _binding = FragmentDuennoObraMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crdServicios = binding.crdServiciosInd
        crdServicios.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.buscarServicioFragment3)
        }

        val crdSolicitud = binding.crdSolicitarDetalle
        crdSolicitud.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.solicitudDetalleFragment)
        }

        val crdTabla = binding.crdTablaCostos
        crdTabla.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.tablaCostosFragment2)
        }

        val crdIncidentes = binding.crdIncidentes
        crdIncidentes.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizacion_inc_eva_obs_Fragment2)
        }
        val crdVisualizarTramites = binding.crdVisualizarTramites
        crdVisualizarTramites.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizarPlanosFragment)
        }
        val crdVisualizarEvidencia = binding.crdVisualizarEvidencia
        crdVisualizarEvidencia.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.visualizarProgresoFragment)
        }
        val  crdVisualizarCronograma = binding.crdVisualizarCronograma
        crdVisualizarCronograma.setOnClickListener{
            val tipo = activity?.intent?.extras?.getString("Tipo")
            val uid = activity?.intent?.extras?.getString("UID")
            val bundle = Bundle()
            bundle.putString("UID", uid)
            bundle.putString("tipo", tipo)
            Navigation.findNavController(view).navigate(R.id.nav_visualizar_cronograma, bundle)
        }

        val crdVisualizarProyectos = binding.crdVisualizarProyectos
        crdVisualizarProyectos.setOnClickListener{
            val tipo = activity?.intent?.extras?.getString("Tipo")
            val uid = activity?.intent?.extras?.getString("UID")
            val bundle = Bundle()
            bundle.putString("Tipo", tipo)
            bundle.putString("UID", uid)
            Navigation.findNavController(view).navigate(R.id.visualizacionProyectosFragment)
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

}