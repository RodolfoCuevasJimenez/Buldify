package cr.una.buildify.servicios


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

