package com.example.rmas18577.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.rmas18577.data.NavigationItem
import com.example.rmas18577.ui.theme.Pink80
import com.example.rmas18577.ui.theme.Purple80
import com.google.android.gms.maps.MapView


@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Purple80
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun Heading(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        style = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Magenta
        ),
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextFieldComponent(
    labelValue: String, painterResource: ImageVector,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorStatus: Boolean = false
) {

    val textValue = remember {
        mutableStateOf("")
    }
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Gray,
            cursorColor = Pink80,
            containerColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextChanged(it)
        },
        isError = !errorStatus
    )
}

@Composable
fun PasswordTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    onTextSelected: (String) -> Unit,
    errorStatus: String?,
    errorMessage: String,
    isPasswordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }

    TextField(
        value = password,
        onValueChange = {
            password = it
            onTextSelected(it)
        },
        label = { Text(labelValue) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = errorStatus != null,
        trailingIcon = {
            IconButton(onClick = { onVisibilityToggle() }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
    if (errorStatus != null) {
        Text(text = errorMessage, color = Color.Magenta, style = TextStyle(fontSize = 12.sp))
    }
}





@Composable
fun DividerText() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Pink80
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "ILI", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Pink80
        )
    }
}


@Composable
fun ClickableTextLogin(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit) {
    val initialText = if (tryingToLogin) "Već imaš nalog? " else "Nemaš nalog? "

    val loginText = if (tryingToLogin) "Prijavi se" else "Registruj se"

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Black)) {  // Ovaj deo teksta je crn
            append(initialText)
        }
        withStyle(style = SpanStyle(color = Purple80, textDecoration = TextDecoration.Underline)) {  // Ovaj deo teksta je jače boje
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 24.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        androidx.compose.foundation.text.ClickableText(
            text = annotatedString,
            onClick = { offset ->
                val annotations = annotatedString.getStringAnnotations(offset, offset)
                annotations.firstOrNull()?.let { annotation ->
                    if (annotation.item == loginText) {
                        onTextSelected(annotation.item)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.Center)  // Centriranje teksta
        )
    }
}



@Composable
fun ButtonComponent(value: String, onButtonClicked: () -> Unit, isEnabled: Boolean = false) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        enabled = isEnabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Purple80, Pink80)),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

        }

    }
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit) {
    val initialText =
        if (tryingToLogin) "Already have an account? " else "Don’t have an account yet? "
    val loginText = if (tryingToLogin) "Login" else "Register"

    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = Purple80)) {
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString,
        onClick = { offset ->

            annotatedString.getStringAnnotations(offset, offset)
                .firstOrNull()?.also { span ->
                    Log.d("ClickableTextComponent", "{${span.item}}")

                    if (span.item == loginText) {
                        onTextSelected(span.item)
                    }
                }

        },
    )
}





@Composable
fun NavigationDrawerHeader(value: String?) {
    Box(
        modifier = Modifier
            .background(
                Brush.horizontalGradient(
                    listOf(Purple80, Pink80)
                )
            )
            .fillMaxWidth()
            .height(180.dp)
            .padding(32.dp)
    ) {

        NavigationDrawerText(
            title = value?:"Teksrt za navdrawer", 28.sp , Color.Cyan
        )

    }
}

@Composable
fun NavigationDrawerBody(navigationDrawerItems: List<NavigationItem>,
                         onNavigationItemClicked:(NavigationItem) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {

        items(navigationDrawerItems) {
            NavigationItemRow(item = it,onNavigationItemClicked)
        }

    }
}

@Composable
fun NavigationItemRow(item: NavigationItem,
                      onNavigationItemClicked:(NavigationItem) -> Unit) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigationItemClicked.invoke(item)
            }.padding(all = 16.dp)
    ) {

        Icon(
            imageVector = item.icon,
            contentDescription = item.description,
        )

        Spacer(modifier = Modifier.width(18.dp))

        NavigationDrawerText(title = item.title, 18.sp, Color.Magenta)


    }
}

@Composable
fun NavigationDrawerText(title: String, textUnit: TextUnit,color: Color) {

    val shadowOffset = Offset(4f, 6f)

    Text(
        text = title, style = TextStyle(
            color = Color.Black,
            fontSize = textUnit,
            fontStyle = FontStyle.Normal,
            shadow = Shadow(
                color = Color.Magenta,
                offset = shadowOffset, 2f
            )
        )
    )
}




@Composable
fun LoginOrRegisterButtons(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple80 // Boja pozadine za dugme
            )
        ) {
            Text(
                text = "Prijavi se",
                style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple80 // Boja pozadine za dugme
            )
        ) {
            Text(
                text = "Registruj se",
                style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun UnderLinedTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ), color = Color.Gray,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    toolbarTitle: String, logoutButtonClicked: () -> Unit,
    navigationIconClicked: () -> Unit
) {

    TopAppBar(
        title = {
            Text(
                text = toolbarTitle, color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navigationIconClicked.invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Meni",
                    tint = Color.White
                )
            }

        },
        actions = {
            IconButton(onClick = {
                logoutButtonClicked.invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = "Log out",
                )
            }
        }
    )
}










