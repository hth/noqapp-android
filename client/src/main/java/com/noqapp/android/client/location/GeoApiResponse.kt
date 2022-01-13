package com.noqapp.android.client.location

class AddressComponent {
    var long_name: String? = null
    var short_name: String? = null
    var types: ArrayList<String>? = null
}

class Result {
    var address_components: ArrayList<AddressComponent>? = null
    var formatted_address: String? = null
    var place_id: String? = null
}

class GeoApiResponse {
    var results: ArrayList<Result>? = null
    var status: String? = null
}