package com.example.mayank.samplemap.sample

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.animation.LinearInterpolator
import com.example.mayank.samplemap.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import com.example.mayank.samplemap.sample.animation.LatLngInterpolator
import com.example.mayank.samplemap.sample.animation.MarkerAnimation
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.CameraPosition









/**
 * Created by Mayank on 29/03/2018.
 */
class MyActivity : AppCompatActivity(), OnMapReadyCallback {

    private var TAG : String = MyActivity::class.java.simpleName

    private lateinit var mMap: GoogleMap
    var markerPoints: ArrayList<LatLng>? = null
    lateinit var marker : Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        markerPoints = ArrayList<LatLng>()

    }

    fun showLogDebug(tag : String , message : String){
        Log.d(tag, message)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true

        val myHome = LatLng(23.300796,77.397697)
        cameraPosition(myHome)



        mMap.setOnMapClickListener { point ->
            if (markerPoints?.size!! >1){
                markerPoints?.clear()
                mMap.clear()
            }

            markerPoints?.add(point)

            val markerOptions = MarkerOptions()

            markerOptions.position(point)
            if (markerPoints?.size == 1){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            }else if(markerPoints?.size==2){
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }

            mMap.addMarker(markerOptions)

            if (markerPoints?.size!! >=2){
                val origin = markerPoints!![0]
                val destination = markerPoints!![1]
                marker = mMap.addMarker(MarkerOptions().position(markerPoints!![0]))

                val url = getUrl(origin, destination)
                showLogDebug(TAG, "OnMapClick : Url : $url")

                val fetchUrl = FetchUrl()
                fetchUrl.execute(url)




//                mMap.addMarker(MarkerOptions().position(origin).title("Marker in Bhopal on MyHome").draggable(true))
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15F))
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin))

            }
        }

    }

    fun cameraPosition(latLng: LatLng){
        showLogDebug(TAG, "Inside Camera position")
        val cameraPosition = CameraPosition.Builder().target(latLng).tilt(60f).zoom(18f).bearing(0f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.addMarker(MarkerOptions().title("Demo").position(latLng))
        mMap.setPadding(0,320,0,0)
    }

    

    fun animateCamera(zoomTo : Float){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomTo))
    }

    private fun getUrl(origin: LatLng, destination: LatLng): String {
        val waypoints = ("waypoints=optimize:true|"
                + origin.latitude + "," + origin.longitude
                + "|" + "|" + destination.latitude + ","
                + destination.longitude)
        val OriDest = "origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude

        val sensor = "sensor=false"
        val params = "$OriDest&%20$waypoints&$sensor"
        val output = "json"
        return ("https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params)
    }


    private inner class FetchUrl : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg url: String): String {
            showLogDebug(TAG, "Url Fetch ${url[0]}")
            var data = ""

            try {
                data = downloadUrl(url[0])
                showLogDebug(TAG, "FetchUrl : $data")
            }catch (e : Exception){
                showLogDebug(TAG, "FRetch Url Error : $e")
            }
            return data
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            showLogDebug(TAG, "Post Execute result $result")
            val parserTask = ParserTask()
            parserTask.execute(result)
        }
    }

    val mark : Marker?= null

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {


        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>> {
            val jsonObject : JSONObject
            try {
                jsonObject = JSONObject(jsonData[0])
                showLogDebug(TAG, "Parser Task Json Data : ${jsonData[0]}")
                val parser = DataParser()
                showLogDebug(TAG, "ParserTask DataParser : $parser")

                var routes : List<List<HashMap<String, String>>> = parser.parse(jsonObject)
                showLogDebug(TAG, "Routes : $routes")
                return routes
            }catch (e : Exception){
                showLogDebug(TAG, "Parser Task Error : $e")
            }
            var r : List<List<HashMap<String, String>>> = ArrayList<ArrayList<HashMap<String, String>>>()
            return r

        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            super.onPostExecute(result)
            var points : ArrayList<LatLng>? = null
            var polyLineOptions : PolylineOptions? = null
            for (i in result?.indices!!){
                points = ArrayList<LatLng>()
                polyLineOptions = PolylineOptions()

                val path = result[i]
                for(j in path.indices){
                    val point = path[j]
                    val lat = java.lang.Double.parseDouble(point["lat"])
                    val lng = java.lang.Double.parseDouble(point["lng"])
                    val position = LatLng(lat, lng)
                    points.add(position)
                }

                polyLineOptions.addAll(points)
                polyLineOptions.width(10F)
                polyLineOptions.startCap(SquareCap())
                polyLineOptions.endCap(SquareCap())
                polyLineOptions.jointType(JointType.ROUND)
                polyLineOptions.color(Color.BLUE)

                showLogDebug(TAG, "Polyline options decodes")
            }
            if (polyLineOptions!=null){
                val polyLine : Polyline
                polyLine = mMap.addPolyline(polyLineOptions)
                showLogDebug(TAG, "Polyline added")
//                MarkerAnimation.animateMarkerToICS(marker, markerPoints!![1], LatLngInterpolator.Linear())


                val handler = Handler()
                var index = -1
                var next = 1
                var startPosition : LatLng?= null
                var endPosition : LatLng?= null
                handler.postDelayed(Runnable {
                    if (index < points?.size!!-1){
                        index++
                        next = index +1
                    }
                    if (index < points?.size!!-1){
                        startPosition = points!![index]
                        endPosition = points!![next]
                    }
                    val valueAnimator = ValueAnimator.ofFloat(0f,1f)
                    valueAnimator.duration = 3000
                    valueAnimator.interpolator = LinearInterpolator()
                    valueAnimator.addUpdateListener {
                        val v = valueAnimator.animatedFraction
                        val lng = v * endPosition?.longitude!! + (1- v) * startPosition?.longitude!!
                        val lat = v * endPosition?.latitude!! + (1 - v) * startPosition?.latitude!!
                        val newPosition = LatLng(lat, lng)
                        marker.position = newPosition
                        marker.setAnchor(0.5F,0.5F)
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_top_view))
                        marker.rotation = getBearing(startPosition!!, newPosition)
                        marker.isFlat =true
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(newPosition)
                                .zoom(18f).build()))

                    }
                    valueAnimator.start()

                },3000)
            }else{
                showLogDebug(TAG, "Without Polylines")
            }
        }

    }

    private fun getBearing(startPosition: LatLng, newPosition: LatLng): Float {
        val lat : Double = Math.abs(startPosition.latitude - newPosition.latitude)
        val lng : Double = Math.abs(startPosition.longitude - newPosition.longitude)
        if (startPosition.latitude < newPosition.latitude && startPosition.longitude < newPosition.longitude){
            return Math.toDegrees(Math.atan(lng/lat)).toFloat()
        }else if(startPosition.latitude >= newPosition.latitude && startPosition.longitude < newPosition.longitude){
            return 90- Math.toDegrees(Math.atan(lng/lat) + 90).toFloat()
        }else if(startPosition.latitude >= newPosition.latitude && startPosition.longitude >= newPosition.longitude){
            return Math.toDegrees(Math.atan(lng/lat) + 180).toFloat()
        }else if (startPosition.longitude >= newPosition.longitude && startPosition.latitude < newPosition.latitude){
            return 90- Math.toDegrees(Math.atan(lng/lat) + 270).toFloat()
        }
        return (-1).toFloat()
    }

    @Throws(IOException::class)
    private fun downloadUrl(url : String): String {
        var data =""
        var inputStream : InputStream? = null
        var httpUrlConnection : HttpURLConnection? =  null
        val stringBuffer = StringBuffer()
        try {
            val url = URL(url)

            httpUrlConnection = url.openConnection() as HttpURLConnection?
            httpUrlConnection?.connect()
            inputStream = httpUrlConnection?.inputStream
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            var line:String? = ""

            var read:String? = bufferReader.readLine()
            while (read != null) {
                stringBuffer.append(read)
                read = bufferReader.readLine()
            }

            data = stringBuffer.toString()
            showLogDebug(TAG, "Download Url : ${data}")
            bufferReader.close()
        }catch (e : Exception){
            showLogDebug(TAG, "DownloadUrl Error : $e")
        }finally {
            inputStream?.close()
            httpUrlConnection?.disconnect()
        }
        return data
    }
}