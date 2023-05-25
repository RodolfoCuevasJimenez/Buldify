package cr.una.buildify.utiles

import android.widget.EditText

    class UtilesFormularios {
        companion object {
            fun verificarCampo(campo: EditText): Boolean {
                if(campo.text.toString().isBlank()){
                    campo.error = "El campo \"" + campo.hint + "\" no puede estar vacío."
                    return false
                }
                campo.error = null
                return true
            }
        }
    }
