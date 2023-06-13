package cr.una.buildify.ui.general.servicios.proyecto

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * Clase para manejar los operaciones sobre la tabla de proyectos
 * @author Nestor Pasos
 */
class ProyectoRepositorio {
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Funcion para obtener la lista de proyectos de acuerdo a un ID
     * @param idReferencia el Id del director encargado del proyecto
     * @return El resultado de la consulta
     */
    fun getListaProyectos(idReferencia: String): Task<QuerySnapshot> {
        return firebaseDB
            .collection("Proyectos")
            .whereEqualTo("idDirector", idReferencia)
            .get()
    }
}