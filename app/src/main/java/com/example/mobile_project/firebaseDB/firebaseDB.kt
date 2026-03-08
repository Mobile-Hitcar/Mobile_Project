package com.example.mobile_project.firebaseDB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// 1. สร้าง Data Class ตาม Diagram ของคุณ
data class User(
    val id: String = "",
    val email: String = "",
    val password: String = "", // ดูคำแนะนำด้านล่างเกี่ยวกับ Password
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "active",
    val role: String = "user"
)

// 2. สร้าง DataSource สำหรับจัดการ Firestore
class FirestoreUserDataSource {
    // ชื่อ Collection ที่จะถูกสร้างอัตโนมัติคือ "users"
    private val collection = Firebase.firestore.collection("users")

    suspend fun insert(user: User) {
        // ใช้ email หรือสร้าง ID ใหม่เป็น Document ID ก็ได้
        // ในที่นี้ถ้าไม่ได้กำหนด ID มา จะใช้ .add() เพื่อ random ID ให้อัตโนมัติ
        collection.add(user).await()
    }

    // (สามารถเพิ่มฟังก์ชัน update, delete, getAll แบบเดียวกับ Order ได้ในอนาคต)
}

// 3. สร้าง Repository
class UserRepository(
    private val dataSource: FirestoreUserDataSource = FirestoreUserDataSource()
) {
    suspend fun insert(user: User) {
        dataSource.insert(user)
    }
}

// 4. สร้าง ViewModel ไว้เชื่อมกับหน้า UI
class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    // ฟังก์ชันนี้จะถูกเรียกจากปุ่ม Register
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        viewModelScope.launch {
            val newUser = User(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phone = phone
            )
            repository.insert(newUser)
        }
    }

    //ฟังก์ชันใหม่สำหรับตรวจสอบ Login
    fun loginUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("users")
            // ค้นหาเอกสารที่ email และ password ตรงกับที่พิมพ์มา
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(false) // ไม่พบข้อมูล (รหัสผ่านผิด หรือไม่มีอีเมลนี้)
                } else {
                    onResult(true) // เจอข้อมูล ล็อกอินสำเร็จ!
                }
            }
            .addOnFailureListener {
                onResult(false) // เผื่อกรณีเน็ตหลุดหรือมี Error
            }
    }
}