package com.example.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Target(val label: String) {
    Cluster("Driver Cluster"), Passenger("Passenger Screen"),
    Hud("Windshield (HUD)"), BlindSpots("Blind Spots")
}

data class UiState(
    val target: Target = Target.Cluster,
    val adbConnected: Boolean = false,
    val live: Boolean = false,
    val layer: Int = 3,
    val gaugeOn: Boolean = false,
    val title: String = "Standby",
    val message: String? = null,
)

class ScreensViewModel(private val adb: AdbController = FakeAdb()) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init { viewModelScope.launch { _state.update { it.copy(adbConnected = adb.connect()) } } }

    fun select(t: Target) = _state.update { it.copy(target = t) }
    fun toggleLive() = _state.update { it.copy(live = !it.live, title = if (!it.live) it.target.label else "Standby") }
    fun layer(delta: Int) = _state.update { it.copy(layer = (it.layer + delta).coerceIn(1, 9)) }
    fun consumeMessage() = _state.update { it.copy(message = null) }

    fun openApp() = viewModelScope.launch {
        val t = _state.value.target
        _state.update { it.copy(message = "Projecting app on ${t.label}…") }
    }

    fun toggleGauge() = viewModelScope.launch {
        val on = !_state.value.gaugeOn
        _state.update { it.copy(gaugeOn = on, message = "Gauge ${if (on) "on" else "off"}") }
    }

    fun setWallpaper() = _state.update { it.copy(message = "Choose a wallpaper…") }
    fun projectionLayout() = _state.update { it.copy(message = "Projection layout…") }

    fun reset() = viewModelScope.launch {
        _state.update { it.copy(message = "Resetting ${it.target.label} display…") }
        _state.update { it.copy(live = false, title = "Standby", layer = 3) }
        _state.update { it.copy(gaugeOn = false, message = "${it.target.label} is back to normal") }
    }
}
