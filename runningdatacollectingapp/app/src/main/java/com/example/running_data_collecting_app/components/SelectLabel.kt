package com.example.running_data_collecting_app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.running_data_collecting_app.data.Label
import com.example.running_data_collecting_app.utils.LabelEvent

@ExperimentalMaterial3Api
@Composable
fun SelectLabel(
    title: String,
    labels: List<Label>,
    selectedLabel: Label?,
    onEvent: (LabelEvent) -> Unit,
    hasButtons: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember {
        mutableStateOf(false)
    }

    val handleLabelSelected: (Label) -> Unit = { label ->
        onEvent(LabelEvent.SelectLabel(label))
    }

    val handleAddClick: () -> Unit = {
        isDialogOpen = true
    }

    val handleDeleteClick: () -> Unit = {
        if (selectedLabel != null) {
            onEvent(LabelEvent.DeleteLabel(selectedLabel))
        }
    }

    val handleSaveClick: (String) -> Unit = { text ->
        onEvent(LabelEvent.AddLabel(Label(name = text)))
    }



    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, fontSize = 18.sp, modifier = Modifier.padding(4.dp)
            )
            if (hasButtons) {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(onClick = handleAddClick) {
                        Text(text = "Add")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(enabled = selectedLabel != null, onClick = handleDeleteClick) {
                        Text(text = "Delete")
                    }
                }
            }
        }
        LazyColumn(
            modifier = modifier
                .selectableGroup()
        ) {
            items(labels) { label ->
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (label.name == selectedLabel?.name),
                            onClick = { handleLabelSelected(label) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (label.name == selectedLabel?.name),
                        onClick = null
                    )
                    Text(
                        text = label.name,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

    }


    if (isDialogOpen) {
        TextFieldDialog(
            onDismissRequest = { isDialogOpen = false }, onSave = handleSaveClick
        )
    }
}