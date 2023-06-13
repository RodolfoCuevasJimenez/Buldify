package cr.una.buildify.ui.general.servicios.tarea

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import cr.una.buildify.ui.general.cronograma.modelo.TareaCronograma

/**
 * Clase para manejar los operaciones sobre la tabla de tareas
 * @author Nestor Pasos
 */
class TareaRepositorio {
    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Funcion para agregar una tarea a la BD
     * @param [tarea] la tarea agregar
     * @return referencia del objeto agregado
     */
    fun agregarTarea(tarea: TareaCronograma): Task<DocumentReference> {
        return firebaseDB.collection("Tareas_Cronograma").add(tarea)
    }

    /**
     * Funcion para agregar listar las tareas de la BD
     * @param [idReferencia] lista con los id de los proyectos
     * @param [fecha] fecha que se quiere filtrar
     * @return referencia del objeto de la consulta a BD
     */
    fun listarTareas(idReferencia: List<String>, fecha: String): Task<QuerySnapshot> {
            return firebaseDB
            .collection("Tareas_Cronograma")
            .whereIn("idProyecto", idReferencia)
            .whereEqualTo("fecha", fecha)
            .get()
    }
}