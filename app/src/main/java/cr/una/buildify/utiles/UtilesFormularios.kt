package cr.una.buildify.utiles

import android.widget.EditText

class UtilesFormularios {
    companion object {
        // Función para verificar si un campo de texto está vacío
        fun verificarCampo(campo: EditText): Boolean {
            if(campo.text.toString().trim().isBlank() || campo.text.toString().trim().isEmpty()){
                // Mostrar un mensaje de error en el campo si está vacío
                campo.error = "El campo \"" + campo.hint + "\" no puede estar vacío."
                return false
            }
            // Limpiar el mensaje de error del campo si no está vacío
            campo.error = null
            return true
        }

        fun verificarEmail(campo: EditText): Boolean {
            val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
            if((campo.text.toString().trim().isNotBlank() && campo.text.toString().trim().isNotEmpty()) && !campo.text.toString().matches(emailPattern)){
                // Mostrar un mensaje de error en el campo si tiene un formato incorrecto
                campo.error = "Ingrese un formato de email válido."
                return false
            }
            // Limpiar el mensaje de error del campo si no está vacío
            campo.error = null
            return true
        }
    }
}
