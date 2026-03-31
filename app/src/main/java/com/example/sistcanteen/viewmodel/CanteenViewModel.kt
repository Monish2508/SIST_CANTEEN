package com.example.sistcanteen.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sistcanteen.models.CartItem
import com.example.sistcanteen.models.FoodItem
import com.example.sistcanteen.models.Order
import com.example.sistcanteen.models.OrderStatus
import com.example.sistcanteen.models.User
import com.example.sistcanteen.models.UserRole

class CanteenViewModel : ViewModel() {
    // Current User State
    var currentUser = mutableStateOf<User?>(null)
        private set

    // Menu State
    var foodItems = mutableStateListOf<FoodItem>(
        FoodItem(name = "Veg Burger", price = 45.0, category = "Snacks", imageUrl = "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=500&q=80"),
        FoodItem(name = "Chicken Roll", price = 60.0, category = "Snacks", imageUrl = "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=500&q=80"),
        FoodItem(name = "Coffee", price = 20.0, category = "Beverages", imageUrl = "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?w=500&q=80"),
        FoodItem(name = "Lunch Thali", price = 80.0, category = "Meals", imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&q=80"),
        FoodItem(name = "Samosa", price = 15.0, category = "Snacks", imageUrl = "https://images.unsplash.com/photo-1601050690597-df056fb1d99a?w=500&q=80"),
        FoodItem(name = "Cold Coffee", price = 35.0, category = "Beverages", imageUrl = "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?w=500&q=80")
    )

    // Search Query
    var searchQuery = mutableStateOf("")

    // Cart State
    var cart = mutableStateListOf<CartItem>()
        private set

    // Orders State
    var allOrders = mutableStateListOf<Order>()
        private set

    fun login(username: String, role: UserRole) {
        currentUser.value = User(name = username, email = "$username@sist.edu", role = role)
    }

    fun logout() {
        currentUser.value = null
        cart.clear()
        searchQuery.value = ""
    }

    fun addToCart(foodItem: FoodItem) {
        val existing = cart.find { it.foodItem.id == foodItem.id }
        if (existing != null) {
            val index = cart.indexOf(existing)
            cart[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            cart.add(CartItem(foodItem, 1))
        }
    }

    fun removeFromCart(foodItem: FoodItem) {
        val existing = cart.find { it.foodItem.id == foodItem.id }
        if (existing != null) {
            if (existing.quantity > 1) {
                val index = cart.indexOf(existing)
                cart[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                cart.remove(existing)
            }
        }
    }

    fun placeOrder(pickupTime: String) {
        val user = currentUser.value ?: return
        if (cart.isEmpty()) return

        val newOrder = Order(
            userId = user.id,
            userName = user.name,
            items = cart.toList(),
            totalPrice = cart.sumOf { it.foodItem.price * it.quantity },
            pickupTime = pickupTime
        )
        allOrders.add(newOrder)
        cart.clear()
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val index = allOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            allOrders[index] = allOrders[index].copy(status = status)
        }
    }

    fun addFoodItem(item: FoodItem) {
        foodItems.add(item)
    }

    fun deleteFoodItem(id: String) {
        foodItems.removeIf { it.id == id }
    }

    fun getUserOrders(): List<Order> {
        val user = currentUser.value ?: return emptyList()
        return allOrders.filter { it.userId == user.id }
    }
}
