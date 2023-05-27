package cr.una.buildify.ui.general.cronograma

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCronogramaBinding

class FragmentAddTask : Fragment() {

    private var _binding: FragmentCronogramaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val homeViewModel =
            ViewModelProvider(this).get(DirectorProyectoMainViewModel::class.java)

        _binding = FragmentCronogramaBinding.inflate(inflater, container, false)
        val root: View = binding.root*/
        val view: View = inflater.inflate(R.layout.fragment_add_tarea, container, false)
        return view
    }
}