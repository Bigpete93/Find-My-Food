package fmf.findmyfood.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import fmf.findmyfood.Classes.LocationItem
import fmf.findmyfood.R
import kotlinx.android.synthetic.main.fragment_information.*
import kotlinx.android.synthetic.main.fragment_information.view.*
import java.util.*
import kotlin.math.round


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Information : Fragment() {
    // TODO: Rename and change types of parameters
    private var height: Int = 0
    private var width: Int = 0
    private var items: LocationItem = LocationItem(
        ArrayList(), ""
    )
    private var listener: OnFragmentInteractionListener? = null
    private var random: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            //location = https@ //maps.googleapis.com/maps/api/place/nearbysearch/json?location=
            // 43.826070,-111.789530&radius=1500&type=restaurant&key=AIzaSyBTK8UXAbjTgDLlqWmcnipHXg2SBhtVx2A


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        var logo = view.fragmentText.layoutParams as FrameLayout.LayoutParams
        view.fragmentText.text = "Find My Food"
        view.fragmentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50.toFloat())
        view.fragmentText.layoutParams.width = width
        logo.setMargins(0, (height * 0.05).toInt(), 0, 0)


        var buttLocation = view.infoButt.layoutParams as FrameLayout.LayoutParams
        //We're going to have some fun readjusting everything.... EVERYTHING!!
        view.infoButt.layoutParams.height = (height * 0.4).toInt()
        view.infoButt.layoutParams.width = (width * 0.9).toInt()
        buttLocation.setMargins(
            (width * 0.05).toInt(),
            (height * 0.17).toInt(),
            (width * 0.05).toInt(),
            0
        )
        view.setOnClickListener {

        }


        buttLocation = view.directionsButt.layoutParams as FrameLayout.LayoutParams
        //We're going to have some fun readjusting everything.... EVERYTHING!!
        view.directionsButt.layoutParams.height = (height * 0.1).toInt()
        view.directionsButt.layoutParams.width = (width * 0.9).toInt()
        view.directionsButt.text = "Directions?"
        buttLocation.setMargins(
            (width * 0.05).toInt(),
            (height * 0.57).toInt(),
            (width * 0.05).toInt(),
            0
        )


        buttLocation = view.newChoiceButt.layoutParams as FrameLayout.LayoutParams
        //We're going to have some fun readjusting everything.... EVERYTHING!!
        view.newChoiceButt.layoutParams.height = (height * 0.1).toInt()
        view.newChoiceButt.layoutParams.width = (width * 0.3).toInt()
        view.newChoiceButt.text = "New Choice!"
        buttLocation.setMargins(
            (width * 0.05).toInt(),
            (height * 0.67).toInt(),
            (width * 0.65).toInt(),
            0
        )



        buttLocation = view.likeButt.layoutParams as FrameLayout.LayoutParams
        //We're going to have some fun readjusting everything.... EVERYTHING!!
        view.likeButt.layoutParams.height = (height * 0.1).toInt()
        view.likeButt.layoutParams.width = (width * 0.3).toInt()
        view.likeButt.text = "Like!"
        buttLocation.setMargins(
            (width * 0.35).toInt(),
            (height * 0.67).toInt(),
            (width * 0.35).toInt(),
            0
        )

        buttLocation = view.dislikeButt.layoutParams as FrameLayout.LayoutParams
        //We're going to have some fun readjusting everything.... EVERYTHING!!
        view.dislikeButt.layoutParams.height = (height * 0.1).toInt()
        view.dislikeButt.layoutParams.width = (width * 0.3).toInt()
        view.dislikeButt.text = "Dislike!"
        buttLocation.setMargins(
            (width * 0.65).toInt(),
            (height * 0.67).toInt(),
            (width * 0.05).toInt(),
            0
        )


        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    /**  Waits until the item has been requested **/


    fun update(incItem: LocationItem, incRandom: Int) {
        items = incItem
        random = incRandom
        if (incItem.results.size != 0) {
            var rating: String = ""
            var price: String = ""
            when (round(incItem.results[random].rating).toInt()) {
                1 -> rating = " ○ ◙ ◙ ◙ ◙"
                2 -> rating = " ○ ○ ◙ ◙ ◙"
                3 -> rating = " ○ ○ ○ ◙ ◙"
                4 -> rating = " ○ ○ ○ ○ ◙"
                5 -> rating = " ○ ○ ○ ○ ○"
            }
            when (incItem.results[random].price_level.toInt()) {
                1 -> price = " $ "
                2 -> price = " $ $ "
                3 -> price = " $ $ $ "
                4 -> price = " $ $ $ $ "
            }

            infoButt.text =
                "\nHave you tried?\n\n-" + incItem.results[random].name + "-\n " + incItem.results[random].vicinity +
                        "\n\n" + rating + "\n" + incItem.results[random].rating.toString() + "//5.0\n\nPrice: " + price
        }
    }

    fun emptyUpdate() {
        //FragmentText.text = "\n No restaurants founds"
        infoButt.text = "No restaurants found\n"
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: LocationItem, incRandom: Int, incHeight: Int, incWidth: Int) =
            Information().apply {
                arguments = Bundle().apply {
                    items = param1
                }
                random = incRandom
                height = incHeight
                width = incWidth
            }
    }
}
