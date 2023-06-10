package cr.una.buildify.carga_archivos

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import cr.una.buildify.R
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel


lateinit var btn_Preconstruccion: CardView
lateinit var btn_Documentos: CardView
lateinit var btn_Progreso: CardView

class Cargar_Archivos_Fragment : Fragment() {


    private var _binding: FragmentCargarArchivosBinding? = null

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

        _binding = FragmentCargarArchivosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_Preconstruccion = binding.btnPreconstruccion
        btn_Preconstruccion.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.cargar_Planos_Fragment)
        }

        btn_Documentos = binding.btnDocumentos
        btn_Documentos.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.cargar_Documentos_Fragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}