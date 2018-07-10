package com.richmond.weatherapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    protected fun searchWeather(view:View){
        var newCity = getCity.text.toString()
        var url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+ newCity +"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        MyAsyncTask().execute(url)
    }

     inner class MyAsyncTask() :AsyncTask<String,String,String>(){

        override fun onPreExecute() {
            //
        }

        override fun doInBackground(vararg params: String?): String {
            try {
                var url = URL(params[0])
                var urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout = 8000
                var outputString = convertStreamToString(urlConnect.inputStream)
            publishProgress(outputString)
            }catch (ex:Exception){}
            return ""
        }

        override fun onProgressUpdate(vararg values: String?) {
            //super.onProgressUpdate(*values)
            try {
                var json = JSONObject(values[0])
                var query = json.getJSONObject("query")
                var results = query.getJSONObject("results")
                var channel = results.getJSONObject("channel")
                var astronomy = channel.getJSONObject("astronomy")
                var atmosphere = channel.getJSONObject("atmosphere")
                var humidity = atmosphere.getString("humidity").toInt()
                var wind = channel.getJSONObject("wind")
                var speed = wind.getString("speed").toInt()
                var sunrise = astronomy.getString("sunrise")
                var sunset = astronomy.getString("sunset")
                var item = channel.getJSONObject("item")
                var title = item.getString("title")
                var lat = item.getString("lat")
                var long = item.getString("long")
                var condition = item.getJSONObject("condition")
                var sky = condition.getString("text")
                var temp = condition.getString("temp").toInt()
                var temperature:String = ""
                var windSpeed:String = ""
                var level:String = ""
                if(temp <= 70){
                    temperature = "Low"
                }else{
                    temperature = "High"
                }

                if(speed <= 10){
                    windSpeed = "light"
                }else{
                    windSpeed = "heavy"
                }

                if(humidity <= 60){
                    level = "low"
                }else{
                    level = "high"
                }

                showWeather.text = "$title. $lat. $long. Characterised by $sky skies and $windSpeed winds with $level chances of rainfall. Sunrise is expected at $sunrise and sunset at $sunset. $temperature temperatures are prevailing at the moment."
            }catch (ex:Exception){
                showWeather.text = "Connection cannot be established....."
            }

        }

        override fun onPostExecute(result: String?) {
            //
        }
    }

      fun convertStreamToString(inputStream: InputStream):String {
          var bufferedReader = BufferedReader(InputStreamReader(inputStream))
          var line: String
          var totalString: String = ""
          try {
              do {
                  var line = bufferedReader.readLine()
                  if (line != null) {
                      totalString += line
                  }
              } while (line != null)
          } catch (ex: Exception) {}
          return totalString!!
      }

}
