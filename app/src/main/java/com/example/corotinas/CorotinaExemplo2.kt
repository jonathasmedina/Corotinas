package com.example.corotinas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class CorotinaExemplo2 : AppCompatActivity() {

    private lateinit var btnProcessar: Button
    private lateinit var txtResultado: TextView
    private lateinit var txtContador: TextView

    // Escopo de coroutines atrelado ao ciclo de vida da Activity
    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_corotina_exemplo2)

        btnProcessar = findViewById(R.id.btnProcessar)
        txtResultado = findViewById(R.id.txtResultadoDados)
        txtContador = findViewById(R.id.txtContador)

        btnProcessar.setOnClickListener {
            txtResultado.text = "Iniciando processamento..."
            txtContador.text = "Contador: 0"

            // 🔄 Inicia um contador que atualiza a cada 1s (UI permanece responsiva)
            iniciarContador()

            // 🚀 Inicia processamento complexo com múltiplos withContext
            activityScope.launch {

                // 1️⃣ Simula chamada de API em Dispatcher.IO
                val dadosApi = withContext(Dispatchers.IO) {
                    simularChamadaApi()
                }

                // 2️⃣ Simula cálculo pesado em Dispatcher.Default
                val dadosProcessados = withContext(Dispatchers.Default) {
                    simularCalculoPesado(dadosApi)
                }

                // 3️⃣ Atualiza a UI em Dispatcher.Main
                withContext(Dispatchers.Main) {
                    txtResultado.text = "Resultado final:\n$dadosProcessados"
                }
            }
        }
    }

    /**
     * Função que inicia um contador que roda em paralelo ao processamento.
     */
    private fun iniciarContador() {
        activityScope.launch { // rodando na thread principal
            var contador = 0
            while (isActive) { // roda enquanto a Activity estiver ativa
                delay(1000)
                contador++
                txtContador.text = "Contador: $contador"
            }
        }
    }

    /**
     * Simula chamada de API (Dispatcher.IO)
     */
    private suspend fun simularChamadaApi(): String {
        delay(3000) // 3 segundos simulando latência
        return "Dados recebidos da API"
    }

    /**
     * Simula cálculo pesado (Dispatcher.Default)
     */
    private suspend fun simularCalculoPesado(dados: String): String {
        delay(4000) // 4 segundos simulando cálculo
        return "$dados → Processados com sucesso!"
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel() // cancela todas as coroutines
    }
}