package cr.una.buildify.utiles

import android.widget.EditText

class UtilesFormularios {
    companion object {
        // Función para verificar si un campo de texto está vacío
        fun verificarCampo(campo: EditText): Boolean {
            if(campo.text.toString().isBlank()){
                // Mostrar un mensaje de error en el campo si está vacío
                campo.error = "El campo \"" + campo.hint + "\" no puede estar vacío."
                return false
            }
            // Limpiar el mensaje de error del campo si no está vacío
            campo.error = null
            return true
        }
    }
}
