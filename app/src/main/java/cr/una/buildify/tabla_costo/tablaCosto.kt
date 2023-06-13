package cr.una.buildify.tabla_costo

/*Creamos la clase con los mismos atributos de la BASE DE DATOS  --el nombre tiene que ser exactamente igual
para que sea reconocido por FireBase a la hora de extraer o guardar la informacion---*/

class tablaCosto (var prototipo: String = "",
                  var obligaciones: String = "",
                  var estructura: String = "",
                  var techos: String = "",
                  var sistema_electrico: String = "",
                  var descripcion: String = "",
                  var m2: String = "",
                  var total: String = "",
                  var fecha_actualizacion: String = "",
                  var  m2_prototipo: String = "",
                  var id: String = "") {

}