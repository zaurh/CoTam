package com.zaurh.cotam.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    textTf: String,
    onTextChange: (String) -> Unit,
    onDone: () -> Unit
) {

    Column() {
        TextField(
            placeholder = { Text(text = "Search...") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            keyboardActions = KeyboardActions(onDone = {
                onDone()
            }),
            value = textTf,
            onValueChange = onTextChange,
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary, fontSize = 16.sp
            ),
            modifier = modifier
                .clip(CircleShape)
                .fillMaxWidth()
        )
    }

}