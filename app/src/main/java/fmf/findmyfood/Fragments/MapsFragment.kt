package fmf.findmyfood.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fmf.findmyfood.Classes.LocationItem
import fmf.findmyfood.R
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "width"
private const val ARG_PARAM2 = "height"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MapsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MapsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class MapsFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var width: Int = 0
    private var height: Int = 0
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var mView: View
    private var items: LocationItem = LocationItem(
        ArrayList(), ""
    )
    private var random = 0
    lateinit var latLong: LatLng

    var initialLat = 43.826070
    var initialLong = -111.789530
    var initialMarker = "Restaurant"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            width = it.getInt(ARG_PARAM1)
            height = it.getInt(ARG_PARAM2)


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.fragment_maps, container, false)



        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = mView.findViewById(R.id.MapView)
        if (mMapView != null) {
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onMapReady(map: GoogleMap) {
        val Rexburg = LatLng(
            items.results[random].geometry.location.lat,
            items.results[random].geometry.location.lng
        )
        MapsInitializer.initialize(context)
        mMap = map
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.addMarker(MarkerOptions().position(Rexburg).title(items.results[random].name).snippet("Recommended Restaurant"))
        mMap.addMarker(MarkerOptions().position(latLong).title("You").snippet("You are here"))
        var position: CameraPosition =
            CameraPosition.builder().target(Rexburg).zoom(16.toFloat()).bearing(0.toFloat())
                .tilt(45.toFloat()).build()

        // mMap.addMarker(MarkerOptions().position(Rexburg).title(initialMarker))
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position))

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    fun update(incItem: LocationItem, incRandom: Int) {
        items = incItem
        random = incRandom
        for (results in items.results) {
        }

        //FragmentText.text = "\nHave you tried?" + incItem.results[random].name + "\n At: " + incItem.results[random].vicinity
    }

    fun emptyUpdate() {
        //FragmentText.text = "\n No restaurants founds"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            param1: Int,
            param2: Int,
            incItems: LocationItem,
            incRandom: Int,
            incLatitude: Double,
            incLongitude: Double
        ) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                    items = incItems
                    random = incRandom
                    latLong = LatLng(incLatitude, incLongitude)

                }
            }
    }
}
