package cr.una.buildify.creacionProyecto

data class Proyecto(
    var id: String = "",
    var idDirector: String = "",
    var nombre: String = "",
    var tipo: String = "",
    var moneda: String = "",
    var presupuesto: Double = 0.0,
    var idCliente: String = "",
    var descripcion: String = "",
    var equipo: ArrayList<Trabajador>? = ArrayList()
)