package cr.una.buildify.ui.duenno_obra

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.databinding.FragmentDuennoObraMainBinding
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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}