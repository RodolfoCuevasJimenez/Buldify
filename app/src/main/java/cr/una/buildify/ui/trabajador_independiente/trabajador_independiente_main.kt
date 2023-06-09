package cr.una.buildify.ui.trabajador_independiente

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentEvaluadorObraMainBinding
import cr.una.buildify.databinding.FragmentTrabajadorIndependienteMainBinding
import cr.una.buildify.iniciosesion.inicioSesion
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class trabajador_independiente_main : Fragment() {

    private var _binding: FragmentTrabajadorIndependienteMainBinding? = null

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

        _binding = FragmentTrabajadorIndependienteMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crdRegistarServ = binding.crdRegistarServicio
        crdRegistarServ.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.registrarServicioFragment)
        }
        val crdCalificaciones = binding.cardCalificaciones
        crdCalificaciones.setOnClickListener(){
            Navigation.findNavController(view).navigate(R.id.calificacionesTrabajador)
        }

        var cerrarSesion: Intent? = null
        cerrarSesion = Intent(activity, inicioSesion::class.java)

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