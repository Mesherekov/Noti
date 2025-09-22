package com.example.noti

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.noti.ui.theme.NotiTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    lateinit var model: MainViewModel
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ), 0
            )
        }
        enableEdgeToEdge()
        model = ViewModelProvider(this)[MainViewModel::class]
        model.getAllData(this)
        if (model.itemsState.value.any {
            it.isActive
            }){
        }
        setContent {
            NotiTheme {
                try {
                    Intent(applicationContext, ForegroundService::class.java).also {
                        it.action = ForegroundService.Actions.START.toString()
                        startService(it)
                    }
                } catch (ex: Exception){
                    Log.i("ERROR", ex.toString())
                }
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                val isHide = remember {
                    mutableStateOf(true)
                }
                Scaffold(bottomBar = {
                    if (isHide.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth().fillMaxHeight(0.2f)
                                .padding(20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Card(
                                elevation = CardDefaults.elevatedCardElevation(3.dp),
                                shape = CircleShape
                            ) {
                                Button(
                                    modifier = Modifier.clip(CircleShape)
                                        .size(70.dp),
                                    onClick = {
                                        isHide.value = false
                                    }, shape = CircleShape, colors = ButtonDefaults.buttonColors(
                                        Color(
                                            0xFF3B5998
                                        )
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.plus),
                                        contentDescription = "add",
                                        modifier = Modifier.scale(1.3f)
                                    )
                                }
                            }
                        }
                    }
                }) {
                    Spacer(Modifier.height(20.dp))
                    TimeList(model.itemsState.collectAsState().value, this)
                    if (!isHide.value) TimePick(isHide, this)
                }
            }
        }
    }

    //Create new notification
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimePick(isHide: MutableState<Boolean>, context: Context){
        var message by remember{mutableStateOf("")}
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
            val currentTime = Calendar.getInstance()
            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true
            )
            Card(elevation = CardDefaults
                .elevatedCardElevation(5.dp),
                modifier = Modifier.padding(3.dp).fillMaxWidth(0.6f)) {
                Column {
                    Row(Modifier.padding(4.dp)) {
                        IconButton(onClick = {
                            isHide.value = true
                        }, modifier = Modifier.size(52.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "close",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            model.addNoti(context, NotiInfo(timePickerState.hour,
                                timePickerState.minute, true, message))
                            isHide.value = true
                        }, modifier = Modifier.size(52.dp)) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "done",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    TimeInput(
                        state = timePickerState
                    )
                    OutlinedTextField(onValueChange = {
                        message = it
                    },
                        value = message, label = {Text("Введите напоминание")},
                        modifier = Modifier.padding(3.dp))

                }
            }
        }
    }


    //List of notification
    @Composable
    fun TimeList(notis: List<NotiInfo>,
                 context: Context){
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp).windowInsetsPadding(WindowInsets.navigationBars)) {
            itemsIndexed(notis){index, item ->
                TimeOne(item, index+1, context)
            }
        }
    }
    //Single notification
    @SuppressLint("SuspiciousIndentation")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimeOne(notiInfo: NotiInfo,
                id: Int, context: Context){
        val time = LocalTime.of(notiInfo.hour,
            notiInfo.minute)
        var colorCard by remember {
            mutableStateOf(if (notiInfo.isActive) Color(0xFFB3B5FF) else Color(0xFFCFCCCC))
        }
        val format24hShort = time.format(DateTimeFormatter.ofPattern("HH:mm"))
            Card(
                elevation = CardDefaults
                    .elevatedCardElevation(4.dp),
                modifier = Modifier.padding(5.dp)
                    .swipeToDismiss {
                    model.deleteNoti(id, context)
                },

                colors = CardDefaults.cardColors(
                    containerColor = colorCard
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {Column {
                    Text(text = format24hShort,
                        fontSize = 25.sp)
                    Text(text = notiInfo.message,
                        fontSize = 14.sp)
                }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                        contentAlignment = Alignment.CenterEnd) {
                        val isActive = remember {
                            mutableStateOf(notiInfo.isActive)
                        }
                        Switch(
                            checked = isActive.value,
                            onCheckedChange = {
                                colorCard = if (!it) Color(0xFFCFCCCC) else Color(0xFFB3B5FF)
                                isActive.value = it
                                model.updateNoti(context,
                                    notiInfo.copy(isActive = isActive.value), id)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.DarkGray,
                                checkedTrackColor = Color.Blue,
                                uncheckedTrackColor = Color.LightGray,
                                checkedBorderColor = Color.Blue,
                                uncheckedBorderColor = Color.DarkGray,
                                checkedIconColor = Color.White,
                                uncheckedIconColor = Color.White
                            )
                        )
                    }
                }
            }
    }
    @SuppressLint("MultipleAwaitPointerEventScopes", "ReturnFromAwaitPointerEventScope")
    fun Modifier.swipeToDismiss(
        onDismissed: () -> Unit
    ): Modifier = composed {
        val offsetX = remember { Animatable(0f) }
        pointerInput(Unit) {
            // Used to calculate fling decay.
            val decay = splineBasedDecay<Float>(this)
            // Use suspend functions for touch events and the Animatable.
            coroutineScope {
                while (true) {
                    // Detect a touch down event.
                    val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                    val velocityTracker = androidx.compose.ui.input.pointer.util.VelocityTracker()
                    // Stop any ongoing animation.
                    offsetX.stop()
                    awaitPointerEventScope {
                        horizontalDrag(pointerId) { change ->
                            // Update the animation value with touch events.
                            launch {
                                offsetX.snapTo(
                                    offsetX.value + change.position.x
                                )
                            }
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position
                            )
                        }
                    }
                    // No longer receiving touch events. Prepare the animation.
                    val velocity = velocityTracker.calculateVelocity().x
                    val targetOffsetX = decay.calculateTargetValue(
                        offsetX.value,
                        velocity
                    )
                    // The animation stops when it reaches the bounds.
                    offsetX.updateBounds(
                        lowerBound = -size.width.toFloat(),
                        upperBound = size.width.toFloat()
                    )
                    launch {
                        if (targetOffsetX.absoluteValue <= size.width) {
                            // Not enough velocity; Slide back.
                            offsetX.animateTo(
                                targetValue = 0f,
                                initialVelocity = velocity
                            )
                        } else {
                            offsetX.animateDecay(velocity, decay)
                            onDismissed()
                        }
                    }
                }
            }
        }
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
    }

}

