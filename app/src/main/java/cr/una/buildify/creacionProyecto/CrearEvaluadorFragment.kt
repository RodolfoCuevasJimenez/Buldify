package cr.una.buildify.creacionProyecto

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import cr.una.buildify.R
import cr.una.buildify.carga_archivos.btn_Documentos
import cr.una.buildify.carga_archivos.btn_Preconstruccion
import cr.una.buildify.databinding.FragmentCargarArchivosBinding
import cr.una.buildify.databinding.FragmentCrearEvaluadorBinding
import cr.una.buildify.ui.director_proyecto.DirectorProyectoMainViewModel

class CrearEvaluadorFragment : Fragment() {

    private lateinit var baseDatos: FirebaseFirestore
    private var _binding: FragmentCrearEvaluadorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(CrearEvaluadorViewModel::class.java)

        _binding = FragmentCrearEvaluadorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseDatos = FirebaseFirestore.getInstance()

        val btnRegistrarEv = binding.btnRegistrarEva
        btnRegistrarEv.setOnClickListener{
            if(view.findViewById<TextView>(R.id.etEmail).text.isNotEmpty() && view.findViewById<TextView>(R.id.etCotrasena).text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(view.findViewById<TextView>(R.id.etEmail).text.toString(),
                    view.findViewById<TextView>(R.id.etCotrasena).text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        postUsuario(it.result?.user?.email?:"")
                        Navigation.findNavController(view).navigate(R.id.director_Proyecto_Main)
                    }
                    else{
                        mostrarAlerta()
                    }
                }

            }
            else{
                mostrarAlerta()
            }
        }
    }

    private fun postUsuario(email: String){
        val idUsuario = binding.etEmail.text.toString() // Obtener el ID del usuario desde el TextView
        val tipo = "Evaluador de la Obra" // Obtener el tipo de usuario desde el AutoCompleteTextView

        val usuario = hashMapOf(
            "idUsuario" to idUsuario,
            "tipo" to tipo
        )

        baseDatos.collection("Usuarios")
            .document(idUsuario)
            .set(usuario)
            .addOnSuccessListener {
                val toast = Toast.makeText(requireContext(),"Usuario agregado correctamente",Toast.LENGTH_SHORT)
                toast.show()
            }
            .addOnFailureListener{
                val toast = Toast.makeText(requireContext(),"Error al agregar el usuario ",Toast.LENGTH_SHORT)
                toast.show()
            }
    }

    private fun mostrarAlerta(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en los datos ingresados")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}