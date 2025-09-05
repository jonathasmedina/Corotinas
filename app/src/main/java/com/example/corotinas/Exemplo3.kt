package com.example.corotinas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Exemplo3 : AppCompatActivity() {

    private lateinit var btnCarregarM: Button
    private lateinit var progressBarM: ProgressBar
    private lateinit var txtResultadoM: TextView
    private lateinit var btnToast: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exemplo3)

        btnCarregarM = findViewById(R.id.btnCarregarDados)
        progressBarM = findViewById(R.id.progressBarDados)
        txtResultadoM = findViewById(R.id.txtResultadoDados)
        btnToast = findViewById(R.id.btnToast)


        btnCarregarM.setOnClickListener {
            carregarDados()
        }
        btnToast.setOnClickListener {
            Toast.makeText(this, "Mensagem exibida enquanto  a função está suspensa.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun carregarDados() {
        // Mostrar o loading
        progressBarM.visibility = View.VISIBLE
        txtResultadoM.text = "Carregando..."

        // Lançar a coroutine
        lifecycleScope.launch {
            // Simula tarefa de rede ou banco (demora 3 segundos)
            val resultado = withContext(Dispatchers.IO) {
                delay(5000) // suspensão sem travar a tela

                "Dados carregados."
            }

            // Atualizar a interface (na Main Thread)
            progressBarM.visibility = View.GONE
            txtResultadoM.text = resultado
        }
    }
}