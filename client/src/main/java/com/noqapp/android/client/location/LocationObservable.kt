package com.noqapp.android.client.location

import com.noqapp.android.client.presenter.beans.body.Location

interface LocationObservable {
    fun onLocationUpdated(location: Location, address: String)
}
