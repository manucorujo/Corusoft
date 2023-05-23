package com.corusoft.ticketmanager

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corusoft.ticketmanager.backend.BackendAPI
import com.corusoft.ticketmanager.backend.dtos.users.UserDTO
import com.corusoft.ticketmanager.backend.exceptions.BackendConnectionException
import com.corusoft.ticketmanager.backend.exceptions.BackendErrorException
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.HorizontalBarChartView
import kotlinx.coroutines.launch


class Landing : AppCompatActivity() {
    companion object {
        private val donutSet = listOf(
            20F, 80F, 100F
        )
        private val horizontalBarSet = listOf(
            "FOOD" to 85.47F, "SHOPPING" to 62.91F, "ENTERTAINMENT" to 25.93F
        )
        private const val animationDuration = 1500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        requestForDashboardData()

        val donutChartView: DonutChartView = findViewById(R.id.donutChart)
        val barChartView: HorizontalBarChartView = findViewById(R.id.barChartHorizontal)

        donutChartView.donutColors = intArrayOf(
            Color.parseColor("#7bde3a"),
            Color.parseColor("#5ea62e"),
            Color.parseColor("#3d6b1e")
        )

        donutChartView.animation.duration = animationDuration
        donutChartView.animate(donutSet)

        barChartView.animation.duration = animationDuration
        barChartView.animate(horizontalBarSet)
    }

    /**
     * Solicita al backend los datos necesarios para la pantalla de inicio del usuario
     */
    private fun requestForDashboardData() {
        // Realizar peticiones al backend
        val backend = BackendAPI()
        var loggedUser: UserDTO?
        lifecycleScope.launch {
            try {
                loggedUser = backend.loginFromToken()
                Toast.makeText(
                    applicationContext,
                    "Usuario ${loggedUser?.nickname} logeado con token",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (ex: BackendErrorException) {
                Toast.makeText(
                    applicationContext,
                    ex.getDetails(),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (ex: BackendConnectionException) {
                System.err.println(ex.message)
                Toast.makeText(
                    applicationContext,
                    ex.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        println("Datos históricos recibidos")
        Toast.makeText(applicationContext, "Datos históricos recibidos", Toast.LENGTH_SHORT).show()
    }

}
