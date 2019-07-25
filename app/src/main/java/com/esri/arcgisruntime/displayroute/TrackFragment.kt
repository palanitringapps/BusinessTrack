package com.esri.arcgisruntime.displayroute

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler
import com.esri.arcgisruntime.security.OAuthConfiguration
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask
import com.esri.arcgisruntime.tasks.networkanalysis.Stop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import java.net.MalformedURLException
import java.util.ArrayList
import java.util.concurrent.ExecutionException

class TrackFragment : Fragment() {
    companion object {
        fun newInstance() = TrackFragment()
    }

    private var mMapView: MapView? = null
    private lateinit var mGraphicsOverlay: GraphicsOverlay
    private lateinit var mStart: Point
    private lateinit var mEnd: Point
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)
        mMapView = view.findViewById(R.id.mapView)
        val layoutBottomSheet = view.findViewById<ConstraintLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_SETTLING -> bottomSheetBehavior!!.setHideable(false)
                }
            }

        })

        setupMap()
        createGraphicsOverlay()
        setupOAuthManager()
        val start = Point(73.791160, 18.706210, SpatialReferences.getWgs84())
        setStartMarker(start)
        val end = Point(80.218960, 12.971880, SpatialReferences.getWgs84())
        setEndMarker(end)
        return view
    }


    private fun createGraphicsOverlay() {
        mGraphicsOverlay = GraphicsOverlay()
        mMapView?.getGraphicsOverlays()?.add(mGraphicsOverlay)
    }

    private fun setMapMarker(location: Point, start: Boolean) {
        val pointGraphic = Graphic(location, marker(start))
        mGraphicsOverlay.getGraphics().add(pointGraphic)
    }

    private fun setStartMarker(location: Point) {
        mGraphicsOverlay.getGraphics().clear()
        setMapMarker(location, true)
        mStart = location
    }

    private fun setEndMarker(location: Point) {
        setMapMarker(location, false)
        mEnd = location
        findRoute()
    }

    // Find a route from mStart point to mEnd point.
    private fun findRoute() {
        val routeServiceURI = resources.getString(R.string.routing_url)
        val solveRouteTask = RouteTask(activity?.applicationContext, routeServiceURI)
        solveRouteTask.loadAsync()
        solveRouteTask.addDoneLoadingListener {
            if (solveRouteTask.getLoadStatus() == LoadStatus.LOADED) {
                val routeParamsFuture = solveRouteTask.createDefaultParametersAsync()
                routeParamsFuture.addDoneListener {
                    try {
                        val routeParameters = routeParamsFuture.get()
                        val stops = ArrayList<Stop>()
                        stops.add(Stop(mStart))
                        stops.add(Stop(mEnd))
                        routeParameters.setStops(stops)
                        val routeResultFuture = solveRouteTask.solveRouteAsync(routeParameters)
                        routeResultFuture.addDoneListener {
                            try {
                                val routeResult = routeResultFuture.get()
                                val firstRoute = routeResult.getRoutes().get(0)
                                val routePolyline = firstRoute.getRouteGeometry()
                                val routeSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 6.0f)
                                val routeGraphic = Graphic(routePolyline, routeSymbol)
                                mGraphicsOverlay.getGraphics().add(routeGraphic)
                            } catch (e: InterruptedException) {
                                showError("Solve RouteTask failed " + e.message)
                            } catch (e: ExecutionException) {
                                showError("Solve RouteTask failed " + e.message)
                            }
                        }
                    } catch (e: InterruptedException) {
                        showError("Cannot create RouteTask parameters " + e.message)
                    } catch (e: ExecutionException) {
                        showError("Cannot create RouteTask parameters " + e.message)
                    }
                }
            } else {
                showError("Unable to load RouteTask " + solveRouteTask.getLoadStatus().toString())
            }
        }
    }

    private fun showError(message: String?) {
        Log.d("FindRoute", message)
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setupMap() {
        if (mMapView != null) {
            val basemapType = Basemap.Type.STREETS
            val latitude = 12.971880
            val longitude = 80.218960
            val levelOfDetail = 12
            val map = ArcGISMap(basemapType, latitude, longitude, levelOfDetail)
            mMapView!!.setMap(map)
        }
    }

    private fun setupOAuthManager() {
        val clientId = resources.getString(R.string.client_id)
        val redirectUri = resources.getString(R.string.redirect_uri)

        try {
            val oAuthConfiguration = OAuthConfiguration("https://www.arcgis.com", clientId, redirectUri)
            val authenticationChallengeHandler = DefaultAuthenticationChallengeHandler(activity)
            AuthenticationManager.setAuthenticationChallengeHandler(authenticationChallengeHandler)
            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration)
        } catch (e: MalformedURLException) {
            showError(e.message)
        }

    }

    override fun onPause() {
        mMapView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.resume()
    }

    override fun onDestroy() {
        mMapView?.dispose()
        super.onDestroy()
    }

    private fun marker(start: Boolean): PictureMarkerSymbol {

        val drawable = BitmapDrawable(resources, bitmap(start))
        val markerSymbol = PictureMarkerSymbol(drawable)
        markerSymbol.height = 40f
        markerSymbol.width = 40f
        markerSymbol.offsetY = markerSymbol.height / 2
        markerSymbol.loadAsync()
        return markerSymbol
    }

    private fun bitmap(start: Boolean): Bitmap {
        if (start) return BitmapFactory.decodeResource(resources, R.drawable.ic_car)
        else return BitmapFactory.decodeResource(resources, R.drawable.ic_loc_marker)
    }
}
