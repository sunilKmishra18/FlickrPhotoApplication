package com.sunilmishra.android.flickrphotoapplication.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class FlickrApiTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: FlickrApi

    private lateinit var mockWebServer: MockWebServer

    val map = HashMap<String, String>()

    init {
        map["method"] = "flickr.photos.search"
        map["api_key"] = "f3ca28946e197f328d1ef2b974535324"
        map["format"] = "json"
    }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlickrApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchWhenValidSearchTextAndResponseAsSuccess() {
        enqueueResponse("search_photos_response.json")
        map["text"] = "computers"
        val resultResponse = service.searchPhotos(5, 1, map, 1).execute().body()

        /*check request type and it's end point*/
        val request = mockWebServer.takeRequest()
        Assert.assertThat(
            request.path,
            CoreMatchers.`is`("/?per_page=5&page=1&method=flickr.photos.search&api_key=f3ca28946e197f328d1ef2b974535324&format=json&text=computers&nojsoncallback=1")
        )

        /* assert response */
        Assert.assertNotNull(resultResponse)
        val response = resultResponse!!.photos
        Assert.assertEquals(response.page, 1)
        Assert.assertEquals(response.pages, 28945)
        Assert.assertEquals(response.perpage, 5)
        Assert.assertEquals(response.total, 144724)

        Assert.assertThat(response.photos.size, CoreMatchers.`is`(5))

        val searchResult = response.photos[0]

        Assert.assertThat(searchResult.id, CoreMatchers.`is`("49242902122"))
        Assert.assertThat(searchResult.owner, CoreMatchers.`is`("22539273@N00"))
        Assert.assertThat(searchResult.secret, CoreMatchers.`is`("c5b6c48cf5"))
        Assert.assertThat(searchResult.farm, CoreMatchers.`is`(66))
        Assert.assertThat(
            searchResult.title,
            CoreMatchers.`is`("Car park @ Secteur du Berger @ Semnoz @ Annecy")
        )
        Assert.assertThat(searchResult.isfamily, CoreMatchers.`is`(0))
        Assert.assertThat(searchResult.isfriend, CoreMatchers.`is`(0))
        Assert.assertThat(searchResult.ispublic, CoreMatchers.`is`(1))
    }

    @Test
    fun searchWhenSearchTextIsEmpty() {
        enqueueResponse("search_photos_response.json")
        map["text"] = ""
        val resultResponse = service.searchPhotos(5, 1, map, 1).execute().body()

        /*check request type and it's end point*/
        val request = mockWebServer.takeRequest()
        Assert.assertThat(
            request.path,
            CoreMatchers.`is`("/?per_page=5&page=1&method=flickr.photos.search&api_key=f3ca28946e197f328d1ef2b974535324&format=json&text=&nojsoncallback=1")
        )
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream("response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse.setBody(
                source.readString(Charsets.UTF_8)
            )
        )
    }

}