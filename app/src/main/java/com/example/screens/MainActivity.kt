package com.example.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Bg = Color(0xFF0A0B0D); private val Panel = Color(0xFF121316); private val Card = Color(0xFF1A1B1F)
private val Gold = Color(0xFFE3B866); private val GoldBg = Color(0xFF2B2518); private val Mute = Color(0xFF8D8A82)
private val Ok = Color(0xFF5FD08A); private val Red = Color(0xFFFF6B5E); private val Org = Color(0xFFFF8A3D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(background = Bg)) { ScreensScreen() } }
    }
}

@Composable
fun ScreensScreen(vm: ScreensViewModel = viewModel()) {
    val s by vm.state.collectAsState()
    val snack = remember { SnackbarHostState() }
    LaunchedEffect(s.message) { s.message?.let { snack.showSnackbar(it); vm.consumeMessage() } }

    Scaffold(containerColor = Bg, snackbarHost = { SnackbarHost(snack) }) { pad ->
        Column(Modifier.padding(pad).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Screens", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Pill("Dual screen")
                Spacer(Modifier.weight(1f))
                Pill(if (s.adbConnected) "ADB CONNECTED" else "ADB OFFLINE", dot = if (s.adbConnected) Ok else Red)
            }
            Text("Driver cluster, passenger screen and HUD", color = Mute, fontSize = 13.sp, modifier = Modifier.padding(bottom = 14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.width(240.dp).background(Panel, RoundedCornerShape(22.dp)).padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Target.values().forEach { t ->
                        val sel = t == s.target
                        Row(Modifier.fillMaxWidth().background(if (sel) GoldBg else Color.Transparent, RoundedCornerShape(14.dp))
                            .clickable { vm.select(t) }.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(t.label, color = if (sel) Gold else Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Column(Modifier.weight(1f).background(Panel, RoundedCornerShape(22.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Show live", color = Mute, fontSize = 13.sp)
                        Switch(checked = s.live, onCheckedChange = { vm.toggleLive() }, modifier = Modifier.padding(start = 8.dp))
                        Spacer(Modifier.weight(1f))
                        IconButton({ vm.layer(-1) }) { Icon(Icons.Default.ChevronLeft, null, tint = Mute) }
                        Text("Layer ${s.layer}" + if (s.layer == 3) " · auto" else "", color = Mute, fontSize = 13.sp)
                        IconButton({ vm.layer(1) }) { Icon(Icons.Default.ChevronRight, null, tint = Mute) }
                    }
                    Box(Modifier.fillMaxWidth().height(110.dp).background(Bg, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text(s.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Action(Modifier.weight(1f), Icons.Default.OpenInNew, "Open app", "Launch an app here", Gold) { vm.openApp() }
                        Action(Modifier.weight(1f), Icons.Default.Refresh, "Reset", "Back to default", Red) { vm.reset() }
                        Action(Modifier.weight(1f), Icons.Default.Wallpaper, "Wallpaper", "Set a background", Gold) { vm.setWallpaper() }
                        Action(Modifier.weight(1f), Icons.Default.Speed, "Gauge", if (s.gaugeOn) "On" else "Off", Org) { vm.toggleGauge() }
                        Action(Modifier.weight(1f), Icons.Default.Tune, "Projection", "Layout settings", Org) { vm.projectionLayout() }
                    }
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String, dot: Color? = null) {
    Row(Modifier.background(GoldBg, CircleShape).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        if (dot != null) { Box(Modifier.size(8.dp).background(dot, CircleShape)); Spacer(Modifier.width(6.dp)) }
        Text(text, color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Action(m: Modifier, icon: ImageVector, title: String, sub: String, tint: Color, onClick: () -> Unit) {
    Column(m.background(Card, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = tint)
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(sub, color = Mute, fontSize = 11.sp)
    }
}
