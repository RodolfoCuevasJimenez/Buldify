package cr.una.buildify.ui.general.cronograma.modelo

import java.util.Date

/**
 * Clase para manejar los datos de las tareas del cronograma
 * @author Nestor Pasos
 */
data class TareaCronograma(
    val Id: String,
    val titulo: String,
    val descripcion: String,
    val fechaIni: Long,
    val fechaFin: Long,
    val horaInicio: String,
    val horaFin: String,
    val estaCompleta: Boolean,
    val idProyecto: String
) {

}