package com.example.sistcanteen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun FoodItemCard(item: FoodItem, onAdd: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(item.category, color = Color.Gray, fontSize = 14.sp)
                Text("₹${item.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            Button(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }
    }
}

@Composable
fun CartDialog(
    viewModel: CanteenViewModel,
    pickupTime: String,
    onPickupTimeChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your Cart") },
        text = {
            Column {
                if (viewModel.cart.isEmpty()) {
                    Text("Your cart is empty")
                } else {
                    viewModel.cart.forEach { cartItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${cartItem.foodItem.name} x${cartItem.quantity}")
                            Row {
                                IconButton(onClick = { viewModel.removeFromCart(cartItem.foodItem) }) {
                                    Icon(Icons.Default.Remove, contentDescription = null)
                                }
                                IconButton(onClick = { viewModel.addToCart(cartItem.foodItem) }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Total: ₹${viewModel.cart.sumOf { it.foodItem.price * it.quantity }}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = pickupTime,
                        onValueChange = onPickupTimeChange,
                        label = { Text("Pickup Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (viewModel.cart.isNotEmpty()) {
                Button(onClick = {
                    viewModel.placeOrder(pickupTime)
                    onDismiss()
                }) {
                    Text("Place Order")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun OrderHistoryDialog(orders: List<Order>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order History") },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp)) {
                items(orders.reversed()) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Token: ${order.token}", fontWeight = FontWeight.Bold)
                                Text(order.status.name, fontSize = 12.sp, color = getStatusColor(order.status))
                            }
                            Text("₹${order.totalPrice}", fontSize = 14.sp)
                        }
                    }
                }
                if (orders.isEmpty()) {
                    item { Text("No orders yet") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING -> Color.Red
        OrderStatus.PREPARING -> Color(0xFFFFA500) // Orange
        OrderStatus.READY -> Color(0xFF4CAF50) // Green
        OrderStatus.COMPLETED -> Color.Gray
        OrderStatus.CANCELLED -> Color.Black
    }
}
