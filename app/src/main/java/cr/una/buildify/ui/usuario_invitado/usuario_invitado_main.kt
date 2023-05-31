package cr.una.buildify.ui.usuario_invitado

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentTrabajadorIndependienteMainBinding
import cr.una.buildify.databinding.FragmentUsuarioInvitadoMainBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class usuario_invitado_main : Fragment() {

    private var _binding: FragmentUsuarioInvitadoMainBinding? = null

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

        _binding = FragmentUsuarioInvitadoMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}