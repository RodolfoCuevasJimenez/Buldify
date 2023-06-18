package cr.una.buildify.servicios


/**
 * Representa un servicio con sus respectivas propiedades.
 * @param tipo El tipo de servicio.
 * @param titulo El título del servicio.
 * @param descripcion La descripción del servicio.
 * @param direccion La dirección del servicio.
 * @param nombrePersona El nombre de la persona responsable del servicio.
 * @param telefono El número de teléfono del servicio.
 * @param correoElectronico El correo electrónico del servicio.
 */
data class Servicio(
        var id: String = "",
        var idUsuario: String= "",
        var tipo: String = "",
        var titulo: String = "",
        var descripcion: String = "",
        var direccion: String = "",
        var nombrePersona: String = "",
        var telefono: String = "",
        var correoElectronico: String = "",
        var calificaciones: ArrayList<Calificacion> = ArrayList(),
        var calificacionGeneral: Float = 0.0f
)

