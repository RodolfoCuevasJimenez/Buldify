package cr.una.buildify.director_proyecto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cr.una.buildify.R

enum class ProviderType{
    BASIC,
    GOOGLE
}

class Director_Proyecto_Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_director_proyecto_home)
    }
}