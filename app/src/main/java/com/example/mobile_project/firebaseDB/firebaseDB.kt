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


// ✅ 1. เพิ่ม Object นี้ไว้ด้านบนสุด เพื่อจดจำอีเมลของคนที่ใช้งานอยู่ตอนนี้
object UserSession {
    var currentUserEmail: String = ""
}

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
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(false)
                } else {
                    // ✅ 2. เมื่อล็อกอินสำเร็จ ให้จำอีเมลนี้เก็บไว้ใน Session
                    UserSession.currentUserEmail = email
                    onResult(true)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun fetchUserData(email: String, onResult: (User?) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // ดึงเอกสารแรกที่เจอมาแปลงเป็น Data Class User
                    val user = documents.documents[0].toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    // 2. ฟังก์ชันสำหรับบันทึกข้อมูลที่แก้ไขกลับลงไปใน Firestore
    fun updateUserData(
        oldEmail: String, // รับอีเมลเดิมมาเพื่อใช้ค้นหา
        newEmail: String, // รับอีเมลใหม่ที่ผู้ใช้พิมพ์แก้
        newFirstName: String,
        newLastName: String,
        newPhone: String,
        onResult: (Boolean) -> Unit
    ) {
        val db = Firebase.firestore
        db.collection("users").whereEqualTo("email", oldEmail).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.documents[0].id
                    db.collection("users").document(docId)
                        .update(
                            mapOf(
                                "email" to newEmail, // ✅ อัปเดตอีเมลใหม่ลง Firestore
                                "firstName" to newFirstName,
                                "lastName" to newLastName,
                                "phone" to newPhone
                            )
                        )
                        .addOnSuccessListener { onResult(true) }
                        .addOnFailureListener { onResult(false) }
                } else {
                    onResult(false) // หาอีเมลเดิมไม่เจอ
                }
            }
            .addOnFailureListener { onResult(false) }
    }
}