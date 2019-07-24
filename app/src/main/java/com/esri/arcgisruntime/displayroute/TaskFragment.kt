package com.esri.arcgisruntime.displayroute

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.net.MalformedURLException
import java.util.ArrayList
import java.util.concurrent.ExecutionException

class TaskFragment : Fragment() {
    companion object {
        fun newInstance() = TaskFragment()
    }

    private var mMapView: MapView? = null
    private lateinit var mGraphicsOverlay: GraphicsOverlay
    private lateinit var mStart: Point
    private lateinit var mEnd: Point

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.layout_only_map, container, false)
        mMapView = view.findViewById(R.id.mapView)
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

    private fun setMapMarker(location: Point) {
        val pointGraphic = Graphic(location, marker())
        mGraphicsOverlay.getGraphics().add(pointGraphic)
    }

    private fun setStartMarker(location: Point) {
        mGraphicsOverlay.getGraphics().clear()
        setMapMarker(location)
        mStart = location
    }

    private fun setEndMarker(location: Point) {
        setMapMarker(location)
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

    private fun marker(): PictureMarkerSymbol {
        val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_truck)
        val drawable = BitmapDrawable(resources, icon)
        val markerSymbol = PictureMarkerSymbol(drawable)
        markerSymbol.height = 40f
        markerSymbol.width = 40f
        markerSymbol.offsetY = markerSymbol.height / 2
        markerSymbol.loadAsync()
        return markerSymbol
    }
}