package com.amrg.blechat.ui.screens.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amrg.blechat.R
import com.amrg.blechat.domain.Message
import com.amrg.blechat.ui.theme.Gray
import com.amrg.blechat.ui.theme.Gray400
import com.amrg.blechat.ui.theme.InterBold
import com.amrg.blechat.ui.theme.InterRegular
import com.amrg.blechat.ui.theme.LightRed
import com.amrg.blechat.ui.theme.LightYellow
import com.amrg.blechat.ui.theme.Yellow
import com.amrg.blechat.utils.getCurrentTime
import com.amrg.blechat.utils.killApp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
) {

    var newMessageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UserNameRow(
                device = viewModel.currentDevice,
                modifier = Modifier.padding(top = 60.dp, start = 20.dp, end = 20.dp)
            ) {
                //viewModel.stopServer()
                //navController.navigate(Screen.ScanScreen.route)
                killApp()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp, topEnd = 30.dp
                        )
                    )
                    .background(Color.White)
            ) {
                val messages = remember { viewModel.messages }

                LaunchedEffect(key1 = messages.size) {
                    if (messages.isNotEmpty()) scrollState.animateScrollToItem(messages.lastIndex)
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .padding(start = 15.dp, top = 25.dp, end = 15.dp, bottom = 85.dp)
                        .imePadding()
                        .imeNestedScroll()
                ) {
                    items(count = messages.size) { index ->
                        ChatRow(message = messages[index])
                    }
                }
            }
        }

        CustomTextField(
            text = newMessageText, onValueChange = { newMessageText = it },
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .align(BottomCenter)
        ) {
            if (newMessageText.isNotEmpty()) {
                viewModel.sendMessage(newMessageText)
                newMessageText = ""
            }
        }
    }

}

@Composable
fun ChatRow(
    message: Message
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message is Message.Local) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(
                    if (message is Message.Local) LightRed else LightYellow
                ),
            contentAlignment = Center
        ) {
            Text(
                text = message.text, style = TextStyle(
                    color = Color.Black,
                    fontFamily = InterRegular,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            text = getCurrentTime(),
            style = TextStyle(
                color = Gray,
                fontFamily = InterRegular,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 15.dp),
        )
    }

}

@Composable
fun CustomTextField(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(164.dp),
        border = BorderStroke(1.dp, Gray400)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 3.dp),
            value = text, onValueChange = { onValueChange(it) },
            placeholder = {
                Text(
                    text = "Type Message",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = InterRegular,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    onSend()
                },
            ),
            trailingIcon = {
                CommonIconButtonDrawable(R.drawable.ic_send) {
                    onSend()
                }
            }
        )
    }
}

@Composable
fun CommonIconButtonDrawable(
    @DrawableRes icon: Int,
    onSend: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Yellow),
        contentAlignment = Center
    ) {
        IconButton(onClick = onSend) {
            Icon(
                painter = painterResource(id = icon), contentDescription = "",
                tint = Color.Black,
                modifier = Modifier
                    .size(20.dp)
                    .offset(1.dp)
            )
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun UserNameRow(
    modifier: Modifier = Modifier,
    device: BluetoothDevice?,
    onClose: () -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.img_user),
                contentDescription = "",
                modifier = Modifier
                    .size(42.dp)
                    .align(CenterVertically),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = device?.name ?: "User101", style = TextStyle(
                        color = Color.White,
                        fontFamily = InterBold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Online", style = TextStyle(
                        color = Color.White,
                        fontFamily = InterRegular,
                        fontSize = 14.sp
                    )
                )
            }
        }
        IconButton(
            onClick = onClose, modifier = Modifier
                .size(24.dp)
                .align(CenterVertically)
        ) {
            Icon(Icons.Default.Close, contentDescription = "", tint = Color.White)
        }
    }

}