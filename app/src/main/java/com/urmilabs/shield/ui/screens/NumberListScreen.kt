package com.urmilabs.shield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.urmilabs.shield.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberListScreen(
    type: String,
    navController: NavController,
    viewModel: NumberListViewModel = hiltViewModel()
) {
    val numbers by viewModel.numbers.collectAsState()
    var newNumber by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val isWhitelist = type.equals("WHITELIST", ignoreCase = true)
    
    val titleRes = if (isWhitelist) "Safe List" else "Block List"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleRes, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Input Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newNumber,
                    onValueChange = { newNumber = it },
                    label = { Text(stringResource(R.string.enter_number)) },
                    placeholder = { Text("+1 (555) 000-0000") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = context.getString(R.string.desc_enter_number, type) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilledIconButton(
                    onClick = {
                        if (newNumber.isNotBlank()) {
                            viewModel.addNumber(newNumber)
                            newNumber = ""
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .padding(top = 8.dp)
                        .semantics { contentDescription = context.getString(R.string.desc_add_number_to_list, type) },
                    shape = MaterialTheme.shapes.medium,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // List Area
            if (numbers.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "List is empty", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(numbers) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = context.getString(R.string.desc_phone_number_in_list, item.number, type) },
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = item.number,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.removeNumber(item.number) },
                                    modifier = Modifier.semantics { contentDescription = context.getString(R.string.desc_remove_number_from_list, item.number, type) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.remove),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
