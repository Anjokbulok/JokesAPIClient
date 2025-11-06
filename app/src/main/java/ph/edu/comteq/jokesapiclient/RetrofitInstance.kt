package ph.edu.comteq.jokesapiclient

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //base url
    private const val BASE_URL = "https://programmingwizards.tech/"

    //logging interceptor - helpsuse seewhat is happening
    private val loggingIterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY

    }
    //OkHttp
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingIterceptor).build()

    //Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL).client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val jokesAPI: JokeAPIService = retrofit.create(JokeAPIService::class.java)
}