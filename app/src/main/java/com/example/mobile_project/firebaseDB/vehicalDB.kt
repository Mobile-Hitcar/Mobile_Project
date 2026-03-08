package com.example.mobile_project.firebaseDB

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.Context
import android.widget.Toast

// ✅ 1. Data Class สำหรับรถ (สำคัญมาก: ต้องมีค่าเริ่มต้น = "" หรือ = 0 ทุกตัว เพื่อให้ Firestore ดึงข้อมูลได้)
// ✅ 1. Data Class อ้างอิงตามที่คุณเคยออกแบบไว้ เพิ่มฟิลด์ให้ครบ
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
    val imageUrl: String = "", // ใช้ imageUrl แทน img เพื่อให้โค้ดหน้า Home ดึงรูปได้เลย
    val status: String = "Available"
)

// ✅ 2. ViewModel สำหรับดึงข้อมูลจาก Firestore แบบ Real-time
class VehicleViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val collection = db.collection("vehicles")

    // ใช้ callbackFlow เพื่อแปลง Snapshot ของ Firestore ให้เป็น Flow
    val allVehicles: Flow<List<VehicleEntity>> = callbackFlow {
        // addSnapshotListener จะทำงานทันทีที่มีการเปลี่ยนแปลงข้อมูลใน Database
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val vehicles = snapshot.documents.mapNotNull { doc ->
                    // แมปข้อมูล JSON จาก Firestore เข้า Data Class และเก็บ Document ID ไว้ด้วย
                    doc.toObject(VehicleEntity::class.java)?.copy(id = doc.id)
                }
                trySend(vehicles) // ส่งข้อมูลก้อนใหม่กลับไปที่หน้า UI
            }
        }

        // ยกเลิกการดึงข้อมูลเมื่อปิดหน้าจอนั้นๆ เพื่อประหยัดเน็ตและแบตเตอรี่
        awaitClose { listener.remove() }
    }

    // ✅ แก้ไขฟังก์ชันนี้ให้รับ Context และเพิ่มการแจ้งเตือน
    fun addMockData(context: Context) {
        val mockVehicles = listOf(
            // คันที่ 1: Sedan
            VehicleEntity(
                brand = "Honda", model = "Civic", segment = "Sedan", seats = 5, door = 4,
                price = 890000, gear = "Auto", energy = "เบนซิน", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 2: Sedan
            VehicleEntity(
                brand = "Toyota", model = "Camry", segment = "Sedan", seats = 5, door = 4,
                price = 1500000, gear = "Auto", energy = "ไฮบริด", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1621007947382-d3117ef65842?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 3: Hatchback
            VehicleEntity(
                brand = "Mazda", model = "3", segment = "Hatchback", seats = 5, door = 5,
                price = 980000, gear = "Auto", energy = "เบนซิน", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1616454790242-2b620cb2ce20?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 4: Pickup (กระบะตอนเดียว)
            VehicleEntity(
                brand = "Isuzu", model = "D-Max", segment = "Pickup", seats = 2, door = 2,
                price = 650000, gear = "Manual", energy = "ดีเซล", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1559416523-140ddc3d238c?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 5: SUV / PPV
            VehicleEntity(
                brand = "Toyota", model = "Fortuner", segment = "SUV", seats = 7, door = 5,
                price = 1450000, gear = "Auto", energy = "ดีเซล", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 6: SUV
            VehicleEntity(
                brand = "Honda", model = "CR-V", segment = "SUV", seats = 7, door = 5,
                price = 1580000, gear = "Auto", energy = "เบนซิน", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1563720225384-9c0f129715cb?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 7: Eco Car
            VehicleEntity(
                brand = "Nissan", model = "Almera", segment = "Eco Car", seats = 5, door = 4,
                price = 540000, gear = "Auto", energy = "เบนซิน", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 8: Pickup (กระบะ 4 ประตู)
            VehicleEntity(
                brand = "Ford", model = "Ranger", segment = "Pickup", seats = 5, door = 4,
                price = 920000, gear = "Auto", energy = "ดีเซล", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1553440569-bcc63803a83d?auto=format&fit=crop&q=80&w=800"
            ),
            // คันที่ 9: EV Car (รถไฟฟ้า)
            VehicleEntity(
                brand = "BYD", model = "Atto 3", segment = "EV", seats = 5, door = 5,
                price = 1099000, gear = "Auto", energy = "ไฟฟ้า", status = "Available",
                imageUrl = "https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&q=80&w=800"
            )
        )

        // วนลูปบันทึกรถทั้ง 9 คันลง Firestore
        mockVehicles.forEach { vehicle ->
            collection.add(vehicle)
                .addOnSuccessListener {
                    Toast.makeText(context, "เพิ่ม ${vehicle.brand} สำเร็จ!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}