import android.net.Uri
import android.util.Log
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmasprojekat18723.data.LoginUIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val TAG = "ObjectViewModel"

    //proveriti sve ovo!!!
    fun getUserById(userId: String, onResult: (LoginUIState?) -> Unit) {
        val userDocRef = db.collection("users").document(userId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Ručno mapiranje polja iz Firestore-a u LoginUIState
                    val email = document.getString("email") ?: ""
                    val password = document.getString("password") ?: ""

                    // Kreiraj instancu LoginUIState sa relevantnim poljima
                    val user = LoginUIState(
                        email = email,
                        password = password
                    )
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getObjectById(objectId: String, onResult: (ObjectUIState?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("objects").document(objectId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    data?.let {
                        // Rukom mapiraj podatke iz Firestore-a u ObjectUIState
                        val objectUIState = ObjectUIState(
                            objectId = objectId, // Dodaj objectId
                            userId = (it["userId"] as? Long)?.toInt() ?: 0, // Pretvori userId iz Long-a u Int
                            locationName = it["locationName"] as? String ?: "",
                            latitude = it["latitude"] as? Double ?: 0.0,
                            longitude = it["longitude"] as? Double ?: 0.0,
                            timestamp = it["timestamp"] as? Long ?: 0L,
                            details = it["details"] as? String,
                            points = it["points"] as? Double ?: 0.0,

                        )
                        onResult(objectUIState)
                    } ?: run {
                        onResult(null) // Ako nema podataka
                    }
                } else {
                    onResult(null) // Ako dokument ne postoji
                }
            }
            .addOnFailureListener {
                onResult(null) // U slučaju greške
            }
    }

}




