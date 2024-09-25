import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.data.`object`.Rating
import com.example.rmasprojekat18723.data.RegistrationUIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ObjectViewModel() : ViewModel() {

    private val TAG = ObjectViewModel::class.simpleName
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _objectUIState = MutableStateFlow(ObjectUIState())
    val objectUIState: StateFlow<ObjectUIState> = _objectUIState

    var objstate = mutableStateOf<List<ObjectUIState>>(emptyList())


    // Initial state with required parameters
   /* var objectUIState = mutableStateOf(
        ObjectUIState(
            objectId = "", // Placeholder value, to be updated when the object is created
            userId = "",
            locationName = "",
            latitude = 0.0,
            longitude = 0.0,
            timestamp = 0L,
            details = null,
            points = 0.0
        )
    )
*/
    private val _objectState = MutableLiveData<ObjectUIState>()
    val objectState: LiveData<ObjectUIState> get() = _objectState



    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    // Funkcija koja obrađuje različite događaje
    fun handleEvent(event: ObjectUIEvent) {
        when (event) {
            is ObjectUIEvent.DetailsChanged -> {
                _objectUIState.value = _objectUIState.value.copy(details = event.details)
                printState()
            }

            is ObjectUIEvent.LatitudeChanged -> {
                _objectUIState.value = _objectUIState.value.copy(latitude = event.latitude)
                printState()
            }

            is ObjectUIEvent.LocationNameChanged -> {
                _objectUIState.value = _objectUIState.value.copy(locationName = event.locationName)
                printState()
            }

            is ObjectUIEvent.LongitudeChanged -> {
                _objectUIState.value = _objectUIState.value.copy(longitude = event.longitude)
                printState()
            }

            is ObjectUIEvent.PointsChanged -> {
                _objectUIState.value = _objectUIState.value.copy(points = event.points)
                printState()
            }
            is ObjectUIEvent.TimeStampChanged -> {
                _objectUIState.value = _objectUIState.value.copy(timestamp = event.timeStamp)
                printState()
            }
            is ObjectUIEvent.AddObjectClicked -> {
                addObject(
                    onSuccess = event.onSuccess,
                    currentLocation = event.currentLocation,
                    timestamp = event.timestamp

                )
                Log.d("TAG", "Ovo znaci da je proso poziv event-a")
            }
            is ObjectUIEvent.LoadAllObjects -> {
                loadAllObjects()
            }
            is ObjectUIEvent.RateObject -> {
                rateObject(event.objectId, event.rating, event.onSuccess)
            }
        }
    }

    private fun printState() {
        Log.d(TAG, "Current State: ${_objectState.value}")
    }

    private fun addObject(
        onSuccess: () -> Unit,
        currentLocation: com.google.android.gms.maps.model.LatLng?,
        timestamp: Long
    ) {
        // Proveravamo da li je trenutna lokacija validna
        if (currentLocation != null) {
            val firestore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            // Proveravamo da li je korisnik autentifikovan
            val userId = currentUser?.uid ?: return
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username") ?: "Nepoznati korisnik"

                    // Pripremamo podatke za dodavanje
                    val objectData = hashMapOf(
                        "locationname" to objectUIState.value.locationName,
                        "details" to objectUIState.value.details,
                        "latitude" to currentLocation.latitude,
                        "longitude" to currentLocation.longitude,
                        "timestamp" to timestamp, // Koristimo prosleđeni timestamp
                        "points" to 0f,
                        "userId" to userId,
                        "postedByUsername" to username
                    )

                    // Dodavanje objekta u Firestore
                    firestore.collection("objects").add(objectData)
                        .addOnSuccessListener { documentRef ->
                            val objectId = documentRef.id
                            _objectUIState.value = objectUIState.value.copy(objectId = objectId)
                            onSuccess() // Pozivamo onSuccess nakon uspešnog dodavanja
                            Log.d("TAG", "Objekat je uspešno dodat u bazu podataka")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ObjectViewModel", "Failed to add object: ${exception.message}")
                        }
                }
        } else {
            Log.e("ObjectViewModel", "Trenutna lokacija nije dostupna.")
        }
    }


    private fun updateUserPoints(userId: String, pointsToAdd: Int) {
        val firestore = FirebaseFirestore.getInstance()

        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            val currentPoints = documentSnapshot.getLong("points")?.toInt() ?: 0
            val newPoints = currentPoints + pointsToAdd

            userRef.update("points", newPoints)
                .addOnSuccessListener {
                    Log.d("ObjectViewModel", "User points updated: $newPoints")
                }
                .addOnFailureListener { exception ->
                    Log.e("ObjectViewModel", "Failed to update user points: ${exception.message}")
                }
        }
    }

    private fun rateObject(objectId: String, rating: Int, onSuccess: () -> Unit) {
        if (objectId.isEmpty()) {
            Log.e("ObjectViewModel", "objectId is empty")
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("ratings")
            .whereEqualTo("userId", userId)
            .whereEqualTo("objectId", objectId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val newRating = Rating(userId = userId, objectId = objectId, grade = rating)
                    firestore.collection("ratings").add(newRating)
                        .addOnSuccessListener {
                            updateAvgGrade(objectId) {
                                assignPointsForRating(objectId, rating)
                                onSuccess()
                            }
                        }
                } else {
                    val ratingDocument = documents.firstOrNull()
                    val previousRating = ratingDocument?.getLong("grade")?.toInt() ?: 0

                    ratingDocument?.reference?.update("grade", rating)
                        ?.addOnSuccessListener {
                            updateAvgGrade(objectId) {
                                adjustPointsForUpdatedRating(objectId, previousRating, rating)
                                onSuccess()
                            }
                        }
                }
            }
    }

    private fun adjustPointsForUpdatedRating(
        objectId: String,
        previousRating: Int,
        newRating: Int
    ) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("objects").document(objectId).get()
            .addOnSuccessListener { documentSnapshot ->
                val ownerId = documentSnapshot.getString("postedByUserId")
                if (ownerId != null) {
                    val previousPoints = calculatePointsFromRating(previousRating)
                    val newPoints = calculatePointsFromRating(newRating)
                    val pointsDifference = newPoints - previousPoints

                    updateUserPoints(ownerId, pointsDifference)
                }
            }
    }

    private fun calculatePointsFromRating(rating: Int): Int {
        return when (rating) {
            0 -> -4
            1 -> 3
            2 -> 2
            3 -> -1
            4 -> 0
            5 -> 1
            6 -> 2
            7 -> 3
            8 -> 4
            9 -> 5
            10 -> 6
            else -> 0
        }
    }


    private fun assignPointsForRating(objectId: String, rating: Int) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("objects").document(objectId).get()
            .addOnSuccessListener { documentSnapshot ->
                val ownerId = documentSnapshot.getString("postedByUserId")
                if (ownerId != null) {
                    val pointsToAdd = when (rating) {
                        0 -> -4
                        1 -> 3
                        2 -> 2
                        3 -> -1
                        4 -> 0
                        5 -> 1
                        6 -> 2
                        7 -> 3
                        8 -> 4
                        9 -> 5
                        10 -> 6
                        else -> 0
                    }
                    updateUserPoints(ownerId, pointsToAdd)
                }
            }
    }

    private fun updateAvgGrade(objectId: String, onSuccess: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("ratings")
            .whereEqualTo("objectId", objectId)
            .get()
            .addOnSuccessListener { result ->
                val totalRatings = result.size()
                val sumRatings = result.documents.sumOf { it.getLong("grade")?.toInt() ?: 0 }

                if (totalRatings > 0) {
                    val points = sumRatings.toDouble() / totalRatings
                    firestore.collection("objects").document(objectId)
                        .update("points", points)
                        .addOnSuccessListener {
                            Log.d("ObjectViewModel", "Average grade is: $points")
                            updateObjectInMapState(objectId, points)
                            onSuccess()
                            loadAllObjects()
                        }
                }
            }
    }


    fun loadAllObjects() {
        val firestore = FirebaseFirestore.getInstance()
        Log.d("ObjectViewModel", "Loading all objects...")

        firestore.collection("objects").get()
            .addOnSuccessListener { result ->
                val objects = result.documents.map { document ->
                    ObjectUIState(
                        objectId = document.id,
                        locationName = document.getString("locationname") ?: "",
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        timestamp = document.getLong("timestamp") ?: 0L,
                        details = document.getString("details") ?: "",
                        points = document.getDouble("points") ?: 0.0,
                        userRatings = mutableMapOf()
                        )
                }
                _objectUIState.value = objectUIState.value.copy(objects = objects)
                Log.d("ObjectViewModel", "Loaded objects: ${objects.size}")

            }
            .addOnFailureListener { exception ->
                Log.e("ObjectViewModel", "Error loading objects: ${exception.message}")
            }
    }

    private fun updateObjectInMapState(objectId: String, points: Double) {
        val updatedObjects = _objectUIState.value.objects.map { obj ->
            if (obj.objectId == objectId) {
                obj.copy(points = points)
            } else {
                obj
            }
        }
        _objectUIState.value = objectUIState.value.copy(objects = updatedObjects)
    }


}
