package fmf.findmyfood.Classes

import java.util.*

class LocationItem(
    var results: ArrayList<Results> = ArrayList<Results>(),
    var next_page_token: String = ""
)

class Results(
    var name: String = "",
    var price_level: Double = 0.0,
    var rating: Double = 0.0,
    var vicinity: String = "",
    var geometry: Geometry = Geometry(
        LocationDir(
            0.0,
            0.0
        )
    )
)

class Geometry(
    var location: LocationDir = LocationDir(0.0, 0.0)
)

class LocationDir(
    var lat: Double = 0.0,
    var lng: Double = 0.0
)