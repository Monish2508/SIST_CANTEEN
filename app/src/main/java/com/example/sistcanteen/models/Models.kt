package com.example.sistcanteen.models

import java.util.UUID

enum class UserRole {
    STUDENT, STAFF, ADMIN
}

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val role: UserRole
)

data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val description: String = ""
)

enum class OrderStatus {
    PENDING, PREPARING, READY, COMPLETED, CANCELLED
}

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val pickupTime: String,
    val token: String = (100..999).random().toString()
)

data class CartItem(
    val foodItem: FoodItem,
    val quantity: Int
)
