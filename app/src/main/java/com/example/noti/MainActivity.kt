package com.example.noti

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noti.ui.theme.NotiTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotiTheme {
                Scaffold() {
                    TimePick()
                }
            }
        }
    }

    //Create new notification
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimePick(){
        Box(contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
            val currentTime = Calendar.getInstance()
            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true
            )
            Column {
                TimeInput(
                    state = timePickerState
                )
            }
        }
    }
    //List of notification
    @Composable
    fun TimeList(notis: List<NotiInfo>){
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            itemsIndexed(notis){_, item ->

            }
        }
    }
    //Single notification
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimeOne(notiInfo: NotiInfo){
        val time = LocalTime.of(notiInfo.hour,
            notiInfo.minute)
        val format24hShort = time.format(DateTimeFormatter.ofPattern("HH:mm"))
            Card(
                elevation = CardDefaults
                    .elevatedCardElevation(4.dp)
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
                        Switch(
                            checked = notiInfo.isActive,
                            onCheckedChange = {

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

}

