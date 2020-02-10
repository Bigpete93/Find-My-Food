package fmf.findmyfood

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import fmf.findmyfood.Classes.LocationItem
import fmf.findmyfood.Fragments.Information
import fmf.findmyfood.Fragments.MapsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.android.synthetic.main.fragment_maps.*
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random


/*****************************
 *  Main activity mainly deals with the overall functions of the program
 *  Many tastks here should be moved when the app is more completed, but the overall program is
 *     the filters atop, in this task, the fragment below which switches between an array of buttons
 *     and a map feature.
 *
 *
 *
 * Functions:
 *      Main Activitiy
 *          OnCreate- Startup details and format of buttons
 *          IsNetwork Available - Bool for finding network
 *          Async Query - Handles the API problems, and calls updates asynchronously
 *          URL Builder - creates the URL for ASYN
 *          OnBackPressed - Handles the back button a little more elequantly for Android.
 *          SetListener - Sts the onclick event listeners
 *
 *
 *
 *
 ******************************/


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    Information.OnFragmentInteractionListener,
    MapsFragment.OnFragmentInteractionListener {

    /*******        Variables for layout locations          *******/
    lateinit var editMiles: Button
    lateinit var editPrice: Button
    lateinit var editFrame: FrameLayout
    lateinit var infoFragment: Information
    lateinit var mapFragment: MapsFragment

    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    lateinit var mLastLocation: Location
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    companion object {
        private const val MY_PERMISSION_CODE = 1000
    }

    /********** Variables for simple information ***********/
    var random: Int = 0

    var lat: Double = 0.0
    var lng: Double = 0.0


    /****************  Variables complex listing. *************/
    lateinit var locationList: String
    var items: LocationItem = LocationItem(
        ArrayList(), ""
    )

    /****************  Button on/off ***********************/
    var mileRange: Int = 5
    var priceRange: BooleanArray = booleanArrayOf(true, true, true, true)


    /**********************************************************
     *
     * On create is probably the largest function in the program
     *    When the program is created, the first thing it does is
     *    rearrange the layout of the app
     *      Following this it loads the fragment manager
     *
     * ************************************/
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get device dimensions
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)


        //Permissions

        //Width and height of the application
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        infoFragment = Information.newInstance(items, random, height, width)
        mapFragment = MapsFragment.newInstance(width, height, items, random, latitude, longitude)


        supportFragmentManager
            .beginTransaction()
            .add(R.id.Bottom, infoFragment)
            .commitNow()
        /******* GPS LOCATION **********/

        BuildLocation()


        /***********   FRAME   ************/
        editFrame = findViewById(R.id.FilterFrame)
        editFrame.layoutParams.width = width
        editFrame.layoutParams.height = (height * 0.17).toInt()

        editFrame = findViewById(R.id.Bottom)
        editFrame.layoutParams.width = width
        editFrame.layoutParams.height = (height * 0.83).toInt()

        editFrame.setPadding(0, 0, 0, 0)



        Mile1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
        Mile5.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
        Mile10.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
        Mile25.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))

        Price1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
        Price2.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
        Price3.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
        Price4.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))


        /***********    Miles    ************/
        /*  MILE1  ! */
        editMiles = findViewById(R.id.Mile1)
        val param1 = editMiles.layoutParams as FrameLayout.LayoutParams
        param1.setMargins(0, 0, 0, 0)
        param1.height = (height * 0.08).toInt()
        param1.width = width / 4
        Mile1.setOnClickListener {
            mileRange = 1
            Mile1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
            Mile5.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile10.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile25.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))

            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            BuildLocation()
        }


        /*  MILE5 ! */
        editMiles = findViewById(R.id.Mile5)
        val param2 = editMiles.layoutParams as FrameLayout.LayoutParams
        param2.setMargins(width / 4, 0, 0, 0)
        param2.height = (height * 0.08).toInt()
        param2.width = width / 4
        Mile5.setOnClickListener {
            mileRange = 5
            Mile1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile5.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
            Mile10.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile25.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            BuildLocation()

        }

        /*  MILE10 ! */
        editMiles = findViewById(R.id.Mile10)
        val param3 = editMiles.layoutParams as FrameLayout.LayoutParams
        param3.setMargins(width / 2, 0, 0, 0)
        param3.height = (height * 0.08).toInt()
        param3.width = width / 4
        Mile10.setOnClickListener {
            mileRange = 10
            Mile1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile5.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile10.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
            Mile25.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            BuildLocation()
        }

        /*  MILE25 ! */
        editMiles = findViewById(R.id.Mile25)
        val param4 = editMiles.layoutParams as FrameLayout.LayoutParams
        param4.setMargins(width / 4 * 3, 0, 0, 0)
        param4.height = (height * 0.08).toInt()
        param4.width = width / 4
        Mile25.setOnClickListener {
            mileRange = 25
            Mile1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile5.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile10.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
            Mile25.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            BuildLocation()
        }

        /***********    Prices    ************/

        /*  Price1  ! */
        editPrice = findViewById(R.id.Price1)
        val param5 = editPrice.layoutParams as FrameLayout.LayoutParams
        param5.setMargins(0, (height * 0.08).toInt(), 0, 0)
        param5.height = (height * 0.08).toInt()
        param5.width = width / 4
        Price1.setOnClickListener {
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            if (priceRange[0]) {
                priceRange[0] = false
                Price1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
                BuildLocation()
            } else {
                priceRange[0] = true
                Price1.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
                BuildLocation()
            }
        }

        /*  Price2 ! */
        editPrice = findViewById(R.id.Price2)
        val param6 = editPrice.layoutParams as FrameLayout.LayoutParams
        param6.setMargins(width / 4, (height * 0.08).toInt(), 0, 0)
        param6.height = (height * 0.08).toInt()
        param6.width = width / 4
        Price2.setOnClickListener {
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            if (priceRange[1]) {
                priceRange[1] = false
                Price2.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
                BuildLocation()
            } else if (!priceRange[1]) {
                priceRange[1] = true
                Price2.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
                BuildLocation()
            }
        }

        /*  Price3 ! */
        editPrice = findViewById(R.id.Price3)
        val param7 = editPrice.layoutParams as FrameLayout.LayoutParams
        param7.setMargins(width / 2, (height * 0.08).toInt(), 0, 0)
        param7.height = (height * 0.08).toInt()
        param7.width = width / 4
        Price3.setOnClickListener {
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            if (priceRange[2]) {
                priceRange[2] = false
                Price3.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
                BuildLocation()
            } else if (!priceRange[2]) {
                priceRange[2] = true
                Price3.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
                BuildLocation()
            }
        }

        /*  Price4 ! */
        editPrice = findViewById(R.id.Price4)
        val param8 = editPrice.layoutParams as FrameLayout.LayoutParams
        param8.setMargins(width / 4 * 3, (height * 0.08).toInt(), 0, 0)
        param8.height = (height * 0.08).toInt()
        param8.width = width / 4
        Price4.setOnClickListener {
            if (mapFragment != null && mapFragment.isVisible) {

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
            if (priceRange[3]) {
                priceRange[3] = false
                Price4.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOff))
                BuildLocation()
            } else if (!priceRange[3]) {
                priceRange[3] = true
                Price4.setBackgroundColor(ContextCompat.getColor(this, R.color.ButtonOn))
                BuildLocation()
            }
        }


        /********************* FRAGMENT **********************/

    }

    override fun onFragmentInteraction(uri: Uri) {
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    internal inner class AsyncQuery : AsyncTask<Void, Void, String>() {

        var hasInternet = false
        lateinit var TextInfo: TextView

        val builder = AlertDialog.Builder(this@MainActivity)

        override fun onPreExecute() {
            super.onPreExecute()
            builder.setTitle("Unable to Connect to Internet")
            builder.setMessage("Please try reconnecting to the Internet and trying again")
            //builder.create().show()
            builder.create()

        }

        override fun doInBackground(vararg params: Void?): String {
            if (isNetworkAvailable()) {
                hasInternet = true
                val client = OkHttpClient()
                val url = UrlBuilder()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                return response.body?.string().toString()
            } else {
                return ""
            }
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result != "") {
                locationList = result

                val gson = GsonBuilder().create()
                items = gson.fromJson(locationList, LocationItem::class.java)


                if (items.results.size > 0)
                    random = Random.nextInt(0, items.results.size - 1)
                else {
                    random = 0
                }
                if (items.results.size > 0) {
                    infoFragment.update(items, random)
                    mapFragment.update(items, random)
                } else {
                    infoFragment.emptyUpdate()
                    mapFragment.emptyUpdate()
                }
            } else {
                builder.show()


            }
            /************************* BUTTONS ************************/
            setListeners()
        }
    }

    /**************
     *
     * This creates the URL components and combines it as a string
     *
     *
     * */
    fun UrlBuilder(): String {
        var location: LocationManager? = null

        var URL: String = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        //
        var locationURL: String = "location=" + latitude.toString() + "," + longitude.toString()
        var radius: String
        var MTOMILE: Int = 1609
        var maxPrice: String = ""
        var minPrice: String = ""
        for (i in 0..3) {
            if (priceRange[i]) {
                minPrice = "&minprice=" + (i + 1).toString()
                break
            }
        }
        for (i in 3 downTo 0) {
            if (priceRange[i]) {
                maxPrice = "&maxprice=" + (i + 1).toString()
                break
            }
        }
        radius = "&radius=" + (MTOMILE * mileRange)
        var typeURL: String = "&type=restaurant"
        val keyURL: String = "&key=" + getString(R.string.PlacesKey)
        URL += locationURL + minPrice + maxPrice + radius + typeURL + keyURL
        return URL


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val test = mapFragment
        if (test != null && test.isVisible) {

            supportFragmentManager
                .beginTransaction()
                //.addToBackStack(null)
                .replace(R.id.Bottom, infoFragment)
                //.setTransition()
                .commitNow()
            infoFragment.update(items, random)
            setListeners()
        } else {
        }
    }

    fun setListeners() {
        infoFragment.infoButt.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.Bottom, mapFragment)
                .commitNow()

            mapFragment.backButt.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()

            }
        }

        infoFragment.directionsButt.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.Bottom, mapFragment)
                .commitNow()

            mapFragment.backButt.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.Bottom, infoFragment)
                    .commitNow()
                infoFragment.update(items, random)
                setListeners()
            }
        }

        infoFragment.likeButt.setOnClickListener { }
        infoFragment.dislikeButt.setOnClickListener {
            BuildLocation()
        }
        infoFragment.newChoiceButt.setOnClickListener {
            BuildLocation()
        }
    }


    /*************** Permissions ****************************/

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f


    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {

                mLastLocation = p0!!.locations.get(p0.locations.size - 1)
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude
                val latLng = LatLng(latitude, longitude)

                // Log.i("LatLng" ,latLng.latitude.toString())
                //Log.i("LatLng",latLng.longitude.toString())

                AsyncQuery().execute()

            }
            //1latitude= mLastLocation.latitude

        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_CODE
                )
            return false
        } else
            return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PERMISSION_GRANTED
                    ) {
                        if (checkLocationPermission()) {
                            Log.d("Check", "Good")
                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                        }
                    }
                }

            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    fun BuildLocation() {
        if (checkLocationPermission()) {
            buildLocationRequest()
            buildLocationCallBack()
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }
}


/************** Location *************************/
