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



}




