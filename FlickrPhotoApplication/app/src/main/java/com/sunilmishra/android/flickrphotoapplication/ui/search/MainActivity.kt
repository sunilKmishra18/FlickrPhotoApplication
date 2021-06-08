package com.sunilmishra.android.flickrphotoapplication.ui.search

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunilmishra.android.flickrphotoapplication.R
import com.sunilmishra.android.flickrphotoapplication.data.ERROR
import com.sunilmishra.android.flickrphotoapplication.data.LIST
import com.sunilmishra.android.flickrphotoapplication.data.NO_DATA
import com.sunilmishra.android.flickrphotoapplication.data.Status
import com.sunilmishra.android.flickrphotoapplication.extensions.injectViewModel
import com.sunilmishra.android.flickrphotoapplication.utils.ItemDivider
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_no_results.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener,
    HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var photoSearchViewModel: PhotoSearchViewModel
    private val photoSearchAdapter = PhotoSearchAdapter()

    private var oldQuery: String = "computers"
    private var newQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoSearchViewModel = injectViewModel(viewModelFactory)

        val config = resources.configuration
        photosRecyclerView.layoutManager = GridLayoutManager(this, 3, getOrientation(config), false)
        val divider = resources.getDimensionPixelSize(R.dimen.divider)
        photosRecyclerView.addItemDecoration(ItemDivider(Color.TRANSPARENT, divider, divider))

        photosRecyclerView.adapter = photoSearchAdapter

        searchImageView.setOnClickListener(this)
        searchEditText.setOnEditorActionListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (photoSearchViewModel.oldQuery.isNotEmpty())
            oldQuery = photoSearchViewModel.oldQuery

        searchPhotos()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.searchImageView) {
                performSearch()
            }
        }

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    }

    private fun performSearch() {
        if (searchEditText.text!!.isNotEmpty()) {
            oldQuery = searchEditText.text.toString()

            searchPhotos(oldQuery)
        }
    }

    private fun searchPhotos(query: String = oldQuery) {
        dismissKeyboard()
        val data = photoSearchViewModel.search(query)

        data?.pagedList?.observe(this, Observer {

            photoSearchAdapter.submitList(it)

        })

        data?.networkState?.observe(this, Observer {

            when (it.status) {
                Status.RUNNING -> {
                    toggleLoading(true)
                }
                Status.SUCCESS -> {
                    toggleLoading(false)
                    show(LIST)
                    oldQuery = newQuery
                }
                Status.FAILED -> {
                    toggleLoading(false)
                    show(ERROR)
                    photoSearchViewModel.oldQuery = ""
                    oldQuery = ""
                }
                Status.NO_DATA -> {
                    toggleLoading(false)
                    if (photoSearchAdapter.itemCount == 0 || oldQuery != newQuery)
                        show(NO_DATA)

                }
            }
        })
    }

    @RecyclerView.Orientation
    private fun getOrientation(config: Configuration): Int {
        return when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                LinearLayoutManager.VERTICAL
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                LinearLayoutManager.HORIZONTAL
            }
            else -> {
                throw AssertionError("This should not be the case.")
            }
        }
    }

    private fun dismissKeyboard() {
        searchEditText.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun toggleLoading(show: Boolean) {
        searchImageView.visibility = if (show) View.GONE else View.VISIBLE
        pbLoading.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun show(what: Int = LIST) {

        when (what) {
            LIST -> {
                llNoResults.visibility = View.GONE
                photosRecyclerView.visibility = View.VISIBLE
            }

            ERROR -> {
                setDetails(R.drawable.ic_error, R.string.error, R.string.try_later)
                llNoResults.visibility = View.VISIBLE
                photosRecyclerView.visibility = View.GONE
            }

            NO_DATA -> {
                setDetails()
                llNoResults.visibility = View.VISIBLE
                photosRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setDetails(
        @DrawableRes icon: Int = R.drawable.no_results,
        @StringRes title: Int = R.string.no_results,
        @StringRes message: Int = R.string.try_different
    ) {
        iconImageView.setImageDrawable(ContextCompat.getDrawable(this, icon))
        titleTextView.text = resources.getString(title)
        messageTextView.text = resources.getString(message)
    }

    fun setTestViewModel(viewModel: PhotoSearchViewModel) {
        photoSearchViewModel = viewModel
    }

}