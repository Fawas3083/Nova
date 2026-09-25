package com.fawas.nova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val engine = NovaEngine(this)
        setContent { NovaApp(engine) }
    }
}

data class Message(val from: String, val text: String)

@androidx.compose.runtime.Composable
fun NovaApp(engine: NovaEngine) {
    var input by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(engine.currentState()) }
    val messages = remember { mutableListOf<Message>() }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Nova", style = MaterialTheme.typography.headlineMedium)
                Text("Experimental cognitive architecture — not a claim of consciousness.", style = MaterialTheme.typography.bodySmall)

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Internal state", style = MaterialTheme.typography.titleMedium)
                        Text("Attention: ${state.attention}")
                        Text("Energy: ${"%.2f".format(state.energy)}   Uncertainty: ${"%.2f".format(state.uncertainty)}   Confidence: ${"%.2f".format(state.confidence)}")
                        Text("Memory events: ${state.memoryCount}")
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(messages.toList()) { message ->
                        Text("${message.from}: ${message.text}")
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Talk to Nova") },
                        maxLines = 3
                    )
                    Button(onClick = {
                        if (input.isNotBlank()) {
                            val userText = input.trim()
                            messages.add(Message("You", userText))
                            val (reply, newState) = engine.process(userText)
                            messages.add(Message("Nova", reply))
                            state = newState
                            input = ""
                        }
                    }) { Text("Send") }
                }
            }
        }
    }
}
