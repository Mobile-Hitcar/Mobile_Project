package com.example.mobile_project.firebaseDB

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Data Class สำหรับรถ — ต้องมีค่า default ทุกฟิลด์เพื่อให้ Firestore แมปได้
data class VehicleEntity(
    val id: String = "",
    val brand: String = "",
    val model: String = "",
    val segment: String = "",
    val seats: Int = 0,
    val door: Int = 0,
    val gear: String = "",
    val energy: String = "",
    val price: Int = 0,
    val imageUrl: String = "",
    val status: String = "Available"
)

// ViewModel สำหรับดึงข้อมูลรถจาก Firestore แบบ Real-time
class VehicleViewModel : ViewModel() {
    private val collection = Firebase.firestore.collection("vehicles")

    val allVehicles: Flow<List<VehicleEntity>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val vehicles = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(VehicleEntity::class.java)?.copy(id = doc.id)
                }
                trySend(vehicles)
            }
        }
        awaitClose { listener.remove() }
    }
}