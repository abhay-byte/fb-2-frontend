package com.ivarna.finalbenchmark2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivarna.finalbenchmark2.domain.model.*

@Composable
fun InformationRow(
    itemValue: ItemValue,
    isLastItem: Boolean,
    modifier: Modifier = Modifier
) {
    val title = itemValue.getName()
    val value = itemValue.getValue()
    
    val contentColor = if (value.isEmpty()) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.isEmpty()) MaterialTheme.colorScheme.tertiary
                       else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor,
                    modifier = Modifier.padding(start = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        
        if (!isLastItem) {
            Divider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}