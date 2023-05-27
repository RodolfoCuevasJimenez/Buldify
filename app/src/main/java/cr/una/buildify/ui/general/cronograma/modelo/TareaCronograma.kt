package cr.una.buildify.ui.general.cronograma.modelo

import java.util.Date

data class TareaCronograma(val titulo:String, val descripcion:String, val fechaInicio: Date, val fechaFin: Date, val estaCompleta:Boolean) {

}