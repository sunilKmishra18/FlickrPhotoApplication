package com.sunilmishra.android.flickrphotoapplication.ui.search

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sunilmishra.android.flickrphotoapplication.*
import com.sunilmishra.android.flickrphotoapplication.data.Data
import com.sunilmishra.android.flickrphotoapplication.data.NetworkState
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import com.sunilmishra.android.flickrphotoapplication.repo.PhotoSearchRepo
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var searchRepo = Mockito.mock(PhotoSearchRepo::class.java)
    private var viewModel = PhotoSearchViewModel(searchRepo)

    private var mockPagedList = MutableLiveData<PagedList<Photo>>()
    private var mockNetworkState = MutableLiveData<NetworkState>()
    private var mockData = Data(mockPagedList, mockNetworkState)

    lateinit var scenario: InjectableActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = injectableActivityScenario<MainActivity> {
            injectActivity {
                setTestViewModel(viewModel)
                Mockito.`when`(
                    viewModel.search("computers")
                ).thenReturn(mockData)
            }
        }.launch()
    }

    @Test
    fun checkErrorState() {
        scenario.runOnMainThread {
            mockPagedList.postValue(com.sunilmishra.android.flickrphotoapplication.emptyList.asPagedList())
            mockNetworkState.postValue(NetworkState.error("No Internet"))
        }

        Espresso.onView(withId(R.id.llNoResults))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.photosRecyclerView))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        checkDetails(R.drawable.ic_error, R.string.error, R.string.try_later)
    }

    @Test
    fun checkSuccessState() {

        scenario.runOnMainThread {
            mockNetworkState.postValue(NetworkState.LOADED)
            mockPagedList.postValue(photoList.asPagedList())
        }

        Espresso.onView(withId(R.id.photosRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.llNoResults))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun checkNoDataState() {

        scenario.runOnMainThread {
            mockPagedList.postValue(com.sunilmishra.android.flickrphotoapplication.emptyList.asPagedList())
            mockNetworkState.postValue(NetworkState.noData())
        }

        Espresso.onView(withId(R.id.llNoResults))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.photosRecyclerView))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        checkDetails(R.drawable.no_results, R.string.no_results, R.string.try_different)
    }

    private fun checkDetails(
        @DrawableRes icon: Int,
        @StringRes title: Int,
        @StringRes message: Int
    ) {
        Espresso.onView(withId(R.id.titleTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(
                        title
                    )
                )
            )
        )

        Espresso.onView(withId(R.id.messageTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(
                        message
                    )
                )
            )
        )

        Espresso.onView(withId(R.id.iconImageView)).check(
            ViewAssertions.matches(
                withDrawable(icon)
            )
        )

    }

    @After
    fun close() {
        scenario.close()
    }
}