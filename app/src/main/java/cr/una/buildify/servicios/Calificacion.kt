package cr.una.buildify.servicios

data class Calificacion(
    var idUsuario: String="", // El ID del usuario que realiza la calificación
    var puntaje: Float =0.0f  // El puntaje asignado en la calificación
    )