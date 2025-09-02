package com.example.corotinas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnProcessar: Button
    private lateinit var txtResultado: TextView

    // Criamos um escopo de coroutines atrelado ao ciclo de vida da Activity
    // Assim, se a Activity for destruída, todas as coroutines são canceladas
    // Dispatchers.Main = Thread principal
    // SupervisorJob = Caso uma coroutine falhe, as outras continuam executando

    private val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnProcessar = findViewById(R.id.btnProcessar)
        txtResultado = findViewById(R.id.txtResultado)

        btnProcessar.setOnClickListener {
            txtResultado.text = "Iniciando processamento..."

            // Iniciamos a coroutine no Dispatcher.Main (thread da UI)
            activityScope.launch {

                // 1️⃣ Simula chamada de API em Dispatcher.IO
                val dadosApi = withContext(Dispatchers.IO) {
                    simularChamadaApi()
                }

                // 2️⃣ Simula processamento pesado em Dispatcher.Default
                val dadosProcessados = withContext(Dispatchers.Default) {
                    simularCalculoPesado(dadosApi)
                }

                // 3️⃣ Atualiza a UI novamente em Dispatcher.Main
                withContext(Dispatchers.Main) {
                    txtResultado.text = "Resultado final:\n$dadosProcessados"
                }
            }
        }
    }

    /**
     * suspend = indica que a função é uma função suspensa, ou seja, pode ser pausada e retomada.
     * Função suspensa que simula uma chamada de API.
     * Executa em Dispatchers.IO (ideal para operações de entrada/saída, como rede ou banco de dados).
     */
    private suspend fun simularChamadaApi(): String {
        delay(2000) // espera 2 segundos para simular latência de rede
        return "Dados recebidos da API"
    }

    /**
     * Função suspensa que simula um cálculo pesado.
     * Executa em Dispatchers.Default (ideal para cálculos intensivos de CPU).
     */
    private suspend fun simularCalculoPesado(dados: String): String {
        delay(3000) // espera 3 segundos simulando cálculo demorado
        return "$dados → Processados com sucesso!"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelamos as coroutines ao destruir a Activity
        activityScope.cancel()
    }
}

/*
// Tipos de Dispatchers em android:

 1. Main Dispatcher
Resumo: O Main Dispatcher é responsável por executar tarefas na thread principal,
garantindo que as atualizações de UI sejam feitas de forma segura e eficiente.

 2. IO Dispatcher
Resumo: O IO Dispatcher é otimizado para operações de entrada/saída,
como leitura e escrita em arquivos ou comunicação de rede, permitindo que essas tarefas sejam
realizadas sem bloquear a thread principal.

3. Default Dispatcher
Resumo: O Default Dispatcher é utilizado para tarefas que não se enquadram nas categorias de Main ou IO,
sendo uma opção genérica para execução de corrotinas em segundo plano.

4. Unconfined Dispatcher
Resumo: O Unconfined Dispatcher é um tipo especial de dispatcher que não está confinado a nenhuma
thread específica. Ele pode ser executado em qualquer thread disponível,
o que o torna útil para tarefas que não dependem de um contexto de thread específico.
 */