package com.corusoft.ticketmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyTickets : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tickets)

        //Obtener referencia al botón
        val addButton = findViewById<FloatingActionButton>(R.id.buttonAdd)
        val filterButton = findViewById<Button>(R.id.buttonFilter)

        //Añadir listener al botón
        addButton.setOnClickListener {
            //Mostrar mensaje de log
            Log.d("MyTicketsActivity", "Se pulsó el botón para agregar un ticket")
        }

        filterButton.setOnClickListener {
            Log.d("MyTicketsActivity", "Se pulsó el botón para filtrar los tickets")
        }

        //Botón para ir a la pantalla de filtro
        filterButton.setOnClickListener {
            val intent = Intent(this, MyTicketsFilter::class.java)
            startActivity(intent)
        }
    }
}