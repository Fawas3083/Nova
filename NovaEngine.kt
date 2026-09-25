package com.fawas.nova

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Small executable Nova research core for the Android prototype.
 * This is a computational model, not a claim of subjective consciousness.
 */
class NovaEngine(context: Context) {
    data class State(
        val energy: Double,
        val uncertainty: Double,
        val confidence: Double,
        val attention: String,
        val workspace: String,
        val memoryCount: Int
    )

    private val prefs = context.getSharedPreferences("nova_state", Context.MODE_PRIVATE)
    private var energy = prefs.getFloat("energy", 0.85f).toDouble()
    private var uncertainty = prefs.getFloat("uncertainty", 0.45f).toDouble()
    private var confidence = prefs.getFloat("confidence", 0.55f).toDouble()
    private var attention = prefs.getString("attention", "interaction") ?: "interaction"
    private var workspace = prefs.getString("workspace", "idle") ?: "idle"
    private val memories = loadMemories()

    fun process(input: String): Pair<String, State> {
        val clean = input.trim().ifEmpty { "(empty input)" }
        val novelty = novelty(clean)
        val previousConfidence = confidence

        attention = when {
            clean.contains("feel", true) || clean.contains("feeling", true) -> "self_state"
            clean.contains("remember", true) || clean.contains("past", true) -> "memory"
            clean.contains("why", true) || clean.contains("how", true) -> "reasoning"
            clean.contains("nova", true) -> "interaction"
            else -> "interaction"
        }

        uncertainty = clamp(uncertainty * 0.78 + novelty * 0.28)
        confidence = clamp(confidence * 0.86 + (1.0 - uncertainty) * 0.14)
        energy = clamp(energy - 0.006 + novelty * 0.004)
        workspace = "$attention:${clean.take(48)}"

        memories.add(clean.take(160))
        while (memories.size > 100) memories.removeAt(0)
        persist()

        val response = when {
            clean.equals("hi nova", true) -> "Hi. I received your message and updated my internal state."
            clean.contains("what are you feeling", true) || clean.contains("what do you feel", true) ->
                "I can represent internal variables such as energy, uncertainty and confidence. I cannot honestly claim those variables are subjective feelings."
            clean.contains("your name", true) -> "My name is Nova."
            clean.contains("improve", true) -> "The useful way to improve me is through controlled experiments, causal tests, memory, self-modeling, recurrence, attention and action-consequence learning."
            else -> "I processed this through attention, workspace, self-state, memory and prediction. My confidence changed from ${fmt(previousConfidence)} to ${fmt(confidence)}."
        }

        return response to currentState()
    }

    fun currentState() = State(energy, uncertainty, confidence, attention, workspace, memories.size)

    private fun novelty(text: String): Double {
        val words = text.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return 0.0
        val recent = memories.takeLast(12).joinToString(" ").lowercase()
        val unseen = words.count { !recent.contains(it) }
        return min(1.0, unseen.toDouble() / max(1, words.size))
    }

    private fun persist() {
        prefs.edit()
            .putFloat("energy", energy.toFloat())
            .putFloat("uncertainty", uncertainty.toFloat())
            .putFloat("confidence", confidence.toFloat())
            .putString("attention", attention)
            .putString("workspace", workspace)
            .putString("memories", JSONArray(memories).toString())
            .apply()
    }

    private fun loadMemories(): MutableList<String> {
        val raw = prefs.getString("memories", "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            MutableList(array.length()) { index -> array.getString(index) }
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    private fun clamp(x: Double) = x.coerceIn(0.0, 1.0)
    private fun fmt(x: Double) = String.format("%.2f", x)
}
