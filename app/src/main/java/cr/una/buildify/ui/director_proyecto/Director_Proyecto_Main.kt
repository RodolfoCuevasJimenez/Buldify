package cr.una.buildify.ui.director_proyecto

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentDirectorProyectoMainBinding
import cr.una.buildify.databinding.FragmentHomeBinding
import cr.una.buildify.ui.home.HomeViewModel

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}