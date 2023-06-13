package cr.una.buildify.visualizacionInc_eva_obs

import com.google.firebase.Timestamp

/**
 * Clase de datos que representa una visualización.
 *
 * @param etapa Etapa de visualización.
 * @param descripcion Descripción de la visualización.
 * @param fecha Fecha de la visualización (puede ser nula).
 * @param observaciones Observaciones adicionales sobre la visualización.
 */
data class Visualizacion(
    var etapa: String = "",
    var descripcion: String = "",
    var fecha: Timestamp? = null,
    var observaciones: String = ""
    )

