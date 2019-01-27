package com.example.juan.aswitch.fragments


import android.os.Bundle
import android.view.*
import java.util.*
import androidx.appcompat.app.AppCompatActivity

import com.example.juan.aswitch.R
import com.example.juan.aswitch.helpers.FragmentHandler
import com.example.juan.aswitch.helpers.Utils
import com.example.juan.aswitch.models.User
import com.example.juan.aswitch.services.PlaceService
import com.example.juan.aswitch.services.UserService
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_filters.*
import org.json.JSONObject
import android.widget.ArrayAdapter
import moe.feng.common.view.breadcrumbs.DefaultBreadcrumbsCallback
import moe.feng.common.view.breadcrumbs.model.BreadcrumbItem
import kotlin.collections.ArrayList


class FiltersFragment : BaseFragment() {

    override fun getTitle(): String {
        return "Filters"
    }

    private lateinit var userService: UserService
    private lateinit var placeService: PlaceService
    private lateinit var user: User
    private lateinit var categoriesJSON: JSONObject
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var fragmentHandler: FragmentHandler
    private var filtersArrayList = ArrayList<String>()
    private var categoriesJSONKeys = ArrayList<String>()
    private var segmentsJSONKeys = ArrayList<String>()
    private var categoriesListSource = ArrayList<String>()
    private var categoriesSelectedPosition = ArrayList<Int>()

    companion object {
        fun getInstance(): FiltersFragment = FiltersFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filters, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentHandler = FragmentHandler(activity!! as AppCompatActivity, R.id.menu_fragment_container)

        userService = UserService(activity!!)
        placeService = PlaceService(activity!!)


        user = Utils.getSharedPreferencesUserObject(activity!!)


        filterRadiusSeekBar.progress = user.radius!!
        filtersArrayList = user.filters!!

        filtersArrayList.forEach {
            filterUserChipGroup.addView(createUserChip(it))
        }

        filterCategoriesBreadcrumbs.setItems(ArrayList(Arrays.asList(
                BreadcrumbItem.createSimpleItem(getString(R.string.filters_fragment_bread_crumbs_root))
        )))

        filterCategoriesBreadcrumbs.setCallback(object : DefaultBreadcrumbsCallback<BreadcrumbItem>() {
            override fun onNavigateBack(item: BreadcrumbItem, position: Int) {
                when(position) {
                    0 -> {
                        categoriesSelectedPosition.clear()
                        categoriesListSource.clear()
                        categoriesListSource.addAll(categoriesJSONKeys)
                    }
                    1 -> {
                        categoriesSelectedPosition.remove(1)
                        categoriesListSource.clear()
                        categoriesListSource.addAll(segmentsJSONKeys)
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onNavigateNewLocation(newItem: BreadcrumbItem, changedPosition: Int) {}
        })

        arrayAdapter = ArrayAdapter(context!!, R.layout.custom_text_view, categoriesListSource)

        filterCategoriesList.apply {
            adapter = arrayAdapter

            setOnItemClickListener { _, _, position, _ ->
                when(categoriesSelectedPosition.size) {
                    0 -> {
                        filterCategoriesBreadcrumbs.addItem(BreadcrumbItem.createSimpleItem(categoriesJSONKeys[position]))
                        val segmentKeys= (categoriesJSON[categoriesJSONKeys[position]] as JSONObject).keys()
                        segmentsJSONKeys.clear()
                        while(segmentKeys.hasNext()) { segmentsJSONKeys.add(segmentKeys.next()) }
                        categoriesListSource.clear()
                        categoriesListSource.addAll(segmentsJSONKeys)
                        categoriesSelectedPosition.add(position)
                    }
                    1 -> {
                        filterCategoriesBreadcrumbs.addItem(BreadcrumbItem.createSimpleItem(segmentsJSONKeys[position]))
                        val segments = categoriesJSON[categoriesJSONKeys[categoriesSelectedPosition[0]]] as JSONObject
                        categoriesListSource.clear()
                        Utils.toStringArray(segments.getJSONArray(segmentsJSONKeys[position]))!!.forEach { if(it != null) categoriesListSource.add(it) }
                        categoriesSelectedPosition.add(position)
                    }
                    2 -> {
                        filterUserChipGroup.addView(createUserChip(categoriesListSource[position]))
                        filtersArrayList.add(categoriesListSource[position])
                        filterCategoriesBreadcrumbs.removeLastItem()
                        filterCategoriesBreadcrumbs.removeLastItem()
                        categoriesSelectedPosition.clear()
                        categoriesListSource.clear()
                        categoriesListSource.addAll(categoriesJSONKeys)
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }
        }

        placeService.getCategoriesGroups {
            activity!!.runOnUiThread {
                categoriesJSON = it.getJSONObject("data")
                val keys = categoriesJSON.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (categoriesJSON.get(key) is JSONObject) categoriesJSONKeys.add(key)
                }
                categoriesListSource.addAll(categoriesJSONKeys)
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.doneAction -> {
                user.radius = filterRadiusSeekBar.progress
                user.filters = filtersArrayList
                Utils.updateSharedPreferencesUserObject(activity!!, user)
                fragmentHandler.add(SwipeFragment.getInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun createUserChip(text: String): Chip {
        val userChip = Chip(filterUserChipGroup.context)
        userChip.text= text
        userChip.isClickable = true
        userChip.isCheckable = true
        userChip.isCloseIconVisible = true
        userChip.setOnCloseIconClickListener {
            filterUserChipGroup.removeView(userChip)
            filtersArrayList.remove(text)
        }
        return userChip
    }

}
