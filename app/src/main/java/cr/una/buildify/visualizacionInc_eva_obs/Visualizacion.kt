package cr.una.buildify.visualizacionInc_eva_obs

import com.google.firebase.Timestamp


data class Visualizacion(
    var etapa: String = "",
    var descripcion: String = "",
    var fecha: Timestamp? = null,
    var observaciones: String = ""
    )

