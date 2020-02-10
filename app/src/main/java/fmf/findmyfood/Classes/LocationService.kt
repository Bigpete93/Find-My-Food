import com.google.android.gms.location.FusedLocationProviderClient

class GetLocation {
    var lattitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var fusedLocationClient: FusedLocationProviderClient

    fun checkPermission() {
        //MainActivity.checkSelfPermission(this, Access)
    }


}