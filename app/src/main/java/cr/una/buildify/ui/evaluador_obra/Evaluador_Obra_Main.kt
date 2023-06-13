package cr.una.buildify.ui.evaluador_obra

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDuennoObraMainBinding
import cr.una.buildify.databinding.FragmentEvaluadorObraMainBinding
import cr.una.buildify.director_proyecto_drawer
import cr.una.buildify.iniciosesion.inicioSesion
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class Evaluador_Obra_Main : Fragment() {

    private var _binding: FragmentEvaluadorObraMainBinding? = null

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

        _binding = FragmentEvaluadorObraMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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