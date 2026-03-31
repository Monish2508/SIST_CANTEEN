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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistcanteen.models.FoodItem
import com.example.sistcanteen.models.Order
import com.example.sistcanteen.models.OrderStatus
import com.example.sistcanteen.viewmodel.CanteenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: CanteenViewModel, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SIST Canteen - Admin") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(onClick = { showAddItemDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Orders", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Menu", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Stats", modifier = Modifier.padding(16.dp))
                }
            }

            when (selectedTab) {
                0 -> OrderManagementList(viewModel)
                1 -> MenuManagementList(viewModel)
                2 -> AdminStatsDashboard(viewModel)
            }
        }

        if (showAddItemDialog) {
            AlertDialog(
                onDismissRequest = { showAddItemDialog = false },
                title = { Text("Add New Food Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = itemPrice,
                            onValueChange = { itemPrice = it },
                            label = { Text("Price") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = itemCategory,
                            onValueChange = { itemCategory = it },
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val price = itemPrice.toDoubleOrNull() ?: 0.0
                        viewModel.addFoodItem(FoodItem(name = itemName, price = price, category = itemCategory, imageUrl = ""))
                        showAddItemDialog = false
                        itemName = ""
                        itemPrice = ""
                        itemCategory = ""
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddItemDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun OrderManagementList(viewModel: CanteenViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Incoming Orders",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.allOrders.reversed()) { order ->
                AdminOrderCard(order = order, onUpdateStatus = { status ->
                    viewModel.updateOrderStatus(order.id, status)
                })
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onUpdateStatus: (OrderStatus) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${order.token}", fontWeight = FontWeight.Bold)
                Text(order.status.name, color = getStatusColor(order.status))
            }
            Text("Customer: ${order.userName}")
            Text("Pickup: ${order.pickupTime}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            order.items.forEach { item ->
                Text("• ${item.foodItem.name} x${item.quantity}")
            }
            Text("Total: ₹${order.totalPrice}", fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (order.status == OrderStatus.PENDING) {
                    Button(onClick = { onUpdateStatus(OrderStatus.PREPARING) }) {
                        Text("Prepare")
                    }
                } else if (order.status == OrderStatus.PREPARING) {
                    Button(onClick = { onUpdateStatus(OrderStatus.READY) }) {
                        Text("Ready")
                    }
                } else if (order.status == OrderStatus.READY) {
                    Button(onClick = { onUpdateStatus(OrderStatus.COMPLETED) }) {
                        Text("Complete")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuManagementList(viewModel: CanteenViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Manage Menu Items",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.foodItems) { item ->
                AdminFoodItemCard(item = item, onDelete = { viewModel.deleteFoodItem(item.id) })
            }
        }
    }
}

@Composable
fun AdminStatsDashboard(viewModel: CanteenViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Statistics", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Revenue: ₹${viewModel.allOrders.sumOf { it.totalPrice }}")
                Text("Total Orders: ${viewModel.allOrders.size}")
                Text("Completed: ${viewModel.allOrders.count { it.status == OrderStatus.COMPLETED }}")
                Text("Pending: ${viewModel.allOrders.count { it.status == OrderStatus.PENDING }}")
            }
        }
    }
}

@Composable
fun AdminFoodItemCard(item: FoodItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("₹${item.price} | ${item.category}", color = Color.Gray)
            }
            Row {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}
