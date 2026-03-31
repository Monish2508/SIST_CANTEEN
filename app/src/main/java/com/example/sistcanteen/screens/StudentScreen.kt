package com.example.sistcanteen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistcanteen.models.FoodItem
import com.example.sistcanteen.viewmodel.CanteenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: CanteenViewModel, onLogout: () -> Unit) {
    var showCart by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var pickupTime by remember { mutableStateOf("12:30 PM") }
    var searchQuery by viewModel.searchQuery

    val filteredItems = viewModel.foodItems.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SIST Canteen") },
                actions = {
                    IconButton(onClick = { showHistory = true }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { showCart = true }) {
                        BadgedBox(
                            badge = {
                                if (viewModel.cart.isNotEmpty()) {
                                    Badge { Text(viewModel.cart.sumOf { it.quantity }.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search food or category...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            Text(
                "Recommended for You",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            val recommendations = remember { viewModel.foodItems.shuffled().take(2) }
            Row(modifier = Modifier.padding(8.dp)) {
                recommendations.forEach { item ->
                    RecommendationChip(item = item, onAdd = { viewModel.addToCart(item) })
                }
            }

            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredItems) { item ->
                    FoodItemCard(item = item, onAdd = { viewModel.addToCart(item) })
                }
            }
        }

        if (showCart) {
            CartDialog(
                viewModel = viewModel,
                pickupTime = pickupTime,
                onPickupTimeChange = { pickupTime = it },
                onDismiss = { showCart = false }
            )
        }

        if (showHistory) {
            OrderHistoryDialog(orders = viewModel.getUserOrders()) {
                showHistory = false
            }
        }
    }
}

@Composable
fun RecommendationChip(item: FoodItem, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.name, fontSize = 12.sp)
            IconButton(onClick = onAdd, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
