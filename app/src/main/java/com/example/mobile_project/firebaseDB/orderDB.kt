package com.example.mobile_project.firebaseDB

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// ✅ Data Class สำหรับ collection "orders" ใน Firestore
data class Order(
    val orderId: String = "",
    val userEmail: String = "",
    val vehicleId: String = "",
    val vehicleBrand: String = "",
    val vehicleModel: String = "",
    val vehicleImageUrl: String = "",
    val vehiclePrice: Int = 0,
    val status: String = "Awaiting Payment",
    val timestamp: Long = System.currentTimeMillis()
)

// ✅ ViewModel สำหรับจัดการ Orders
class OrderViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val collection = db.collection("orders")

    // ดึงประวัติคำสั่งซื้อของ user คนนี้ แบบ Real-time
    fun getOrdersByUser(userEmail: String): Flow<List<Order>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userEmail", userEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                    }
                    // เรียงจากใหม่ -> เก่า
                    trySend(orders.sortedByDescending { it.timestamp })
                }
            }
        awaitClose { listener.remove() }
    }

    // ลบคำสั่งซื้อออกจาก Firestore
    fun deleteOrder(orderId: String, onResult: (Boolean) -> Unit) {
        collection.document(orderId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // อัปเดตสถานะคำสั่งซื้อ (เช่น เปลี่ยนเป็น "Payment Successful")
    fun updateOrderStatus(orderId: String, newStatus: String, onResult: (Boolean) -> Unit) {
        collection.document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // บันทึกคำสั่งซื้อใหม่
    fun placeOrder(
        userEmail: String,
        vehicle: VehicleEntity,
        onResult: (Boolean) -> Unit
    ) {
        val newOrder = Order(
            userEmail = userEmail,
            vehicleId = vehicle.id,
            vehicleBrand = vehicle.brand,
            vehicleModel = vehicle.model,
            vehicleImageUrl = vehicle.imageUrl,
            vehiclePrice = vehicle.price,
            status = "Awaiting Payment"
        )
        collection.add(newOrder)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}