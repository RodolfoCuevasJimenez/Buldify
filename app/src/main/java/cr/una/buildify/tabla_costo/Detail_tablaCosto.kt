package cr.una.buildify.tabla_costo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cr.una.buildify.R

/*Clase para la asigancion de valores al formulario DETALLE*/

class Detail_tablaCosto : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tabla_costo)


/*Asiganamos el valor de la llave del fragment a una variable*/
        var recibPrototipo = intent.getStringExtra("prototipo")!!
/*Llamamos el atributo el cual mostrar[a el texto*/
       var etprototipo: TextView = findViewById(R.id.texDetailPrototipo)

        var recibm2 = intent.getStringExtra("m2")!!
        var etm2: TextView = findViewById(R.id.texDetailm2)


        var recibEstructura = intent.getStringExtra("estructura")!!
        var etEstructura: TextView = findViewById(R.id.texDetailEstructura)

        var recibElectricidad = intent.getStringExtra("electrico")!!
        var etElectricidad: TextView = findViewById(R.id.texDetailElectricidad)

        var recibTecho = intent.getStringExtra("techo")!!
        var etTecho: TextView = findViewById(R.id.texDetailTecho)

        var recibObligaciones = intent.getStringExtra("obligaciones")!!
        var etObligaciones: TextView = findViewById(R.id.texDetailObligaciones)

        var recibMetros = intent.getStringExtra("metros")!!
        var etMetros: TextView = findViewById(R.id.texDetailMetros)

        var recibTotal = intent.getStringExtra("total")!!
        var etTotal: TextView = findViewById(R.id.texDetailTotal)


/*Realizamos la asignacion del texto a los TextView del formulario*/
        etprototipo.text= recibPrototipo
        etm2.text= recibm2
       etEstructura.text= recibEstructura
       etElectricidad.text= recibElectricidad
        etTecho.text= recibTecho
        etObligaciones.text= recibObligaciones
        etMetros.text= recibMetros
        etTotal.text= recibTotal


        }
}