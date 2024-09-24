import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rmas18577.data.`object`.ObjectUIEvent
import com.example.rmas18577.data.`object`.ObjectUIState
import com.example.rmas18577.screens.uploadImageToFirebaseStorage
import com.example.rmasprojekat18723.navigation.Navigator
import com.example.rmasprojekat18723.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ObjectViewModel() : ViewModel() {

    private val TAG = ObjectViewModel::class.simpleName
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _userObjects = MutableLiveData<List<ObjectUIState>>()


    val userObjects: LiveData<List<ObjectUIState>> = _userObjects



    // Initial state with required parameters
    var objectUIState = mutableStateOf(
        ObjectUIState(
            objectId = "", // Placeholder value, to be updated when the object is created
            userId = 0,
            locationName = "",
            latitude = 0.0,
            longitude = 0.0,
            timestamp = 0L,
            details = null,
            points = 0.0
        )
    )

    private val _objectState = MutableLiveData<ObjectUIState>()
    val objectState: LiveData<ObjectUIState> get() = _objectState

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    // Funkcija koja obrađuje različite događaje
    fun handleEvent(event: ObjectUIEvent) {
        when (event) {
            is ObjectUIEvent.DetailsChanged -> {
                _objectState.value = _objectState.value?.copy(details = event.details)
                printState()
            }
            is ObjectUIEvent.LatitudeChanged -> {
                _objectState.value = _objectState.value?.copy(latitude = event.latitude)
                printState()
            }
            is ObjectUIEvent.LocationNameChanged -> {
                _objectState.value = _objectState.value?.copy(locationName = event.locationName)
                printState()
            }
            is ObjectUIEvent.LongitudeChanged -> {
                _objectState.value = _objectState.value?.copy(longitude = event.longitude)
                printState()
            }
            is ObjectUIEvent.PointsChanged -> {
                _objectState.value = _objectState.value?.copy(points = event.points)
                printState()
            }
            is ObjectUIEvent.TimeStampChanged -> {
                _objectState.value = _objectState.value?.copy(timestamp = event.timeStamp)
                printState()
            }
            is ObjectUIEvent.AddObjectClicked -> {
                // Proveri da li su svi potrebni podaci prisutni u _objectState
                val currentState = _objectState.value

                if (currentState != null) {

                    // Pozovi funkciju za dodavanje objekta
                    addObject(
                        locationName = currentState.locationName,
                        latitude = currentState.latitude,
                        longitude = currentState.longitude,
                        timestamp = currentState.timestamp,
                        details = currentState.details ?: "", // Ako je null, koristi praznu vrednost
                        points = currentState.points,
                        onResult = { success, message ->
                            if (success) {
                                Log.d(TAG, "Object successfully added")
                              //  toastNotifier.showToast("Objekat uspešno dodat!")
                            } else {
                                Log.e(TAG, "Failed to add object: $message")
                              //  toastNotifier.showToast("Greška: $message")
                            }
                        }
                    )
                } else {
                    Log.e(TAG, "Object state is null, cannot add object")
                }
            }

        }
    }

    private fun printState() {
        Log.d(TAG, "Current State: ${_objectState.value}")
    }

    fun addObject(
        locationName: String,
        latitude: Double,
        longitude: Double,
        timestamp: Long,
        details: String,
        points: Double?,
        onResult: (Boolean, String?) -> Unit
    ) {
        Log.d(TAG, "Pokušavam da dodam objekat: locationName=$locationName, latitude=$latitude, longitude=$longitude")

        val userId = auth.currentUser?.uid
        val username = auth.currentUser?.displayName
        if (userId != null && username != null) {
            if (locationName.isEmpty()) {
                onResult(false, "Naziv lokacije je obavezan")
                return
            }

            val mapObject = hashMapOf(
                "locationName" to locationName,
                "latitude" to latitude,
                "longitude" to longitude,
                "timestamp" to timestamp,
                "details" to details,
                "author" to username,
                "ownerId" to userId,
                "points" to points
            )

            db.collection("objects").add(mapObject)
                .addOnSuccessListener { documentReference ->
                    val objectId = documentReference.id
                    Log.d(TAG, "Objekat dodat sa ID: $objectId")
                    onResult(true, null) // Obavesti o uspehu
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Dodavanje objekta nije uspelo", exception)
                    onResult(false, "Dodavanje objekta nije uspelo: ${exception.message}")
                }
        } else {
            onResult(false, "Korisnik nije autentifikovan")
        }
    }




    fun getAllUserObjects() {
        val userId = auth.currentUser?.uid
        Log.d(TAG, "Current User ID: $userId")
        userId?.let {
            db.collection("objects")
                .whereEqualTo("ownerId", it) // Uveri se da koristiš ispravno ime polja
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "Documents retrieved: ${documents.size()}")
                    val objectsList = documents.map { document ->
                        Log.d(TAG, "Document ID: ${document.id}, Data: ${document.data}")
                        document.toObject(ObjectUIState::class.java).apply {
                            objectId = document.id
                        }
                    }
                    _userObjects.value = objectsList
                    Log.d(TAG, "User objects list: $objectsList") // Loguj listu objekata
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting user objects: ", exception)
                }
        } ?: Log.w(TAG, "User ID is null")
    }




}
