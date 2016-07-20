package com.aajtech.mobile.goproshowcase

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aajtech.mobile.goproshowcase.dto.GoProStatusDTO
import com.aajtech.mobile.goproshowcase.dto.GoProStatusResponse
import com.aajtech.mobile.goproshowcase.dto.buildViewHolderData

import com.aajtech.mobile.goproshowcase.dummy.DummyContent
import com.aajtech.mobile.goproshowcase.dummy.DummyContent.DummyItem
import com.aajtech.mobile.goproshowcase.service.GoProInfoService
import com.aajtech.mobile.goproshowcase.service.MagicPacket
import com.aajtech.mobile.goproshowcase.service.retrofit
import com.aajtech.mobile.goproshowcase.service.sendWoL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class GoProStatusDTOFragment : Fragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_goprostatusdto_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            val statusList = emptyList<GoProStatusDTO>().toMutableList()
            view.adapter = MyGoProStatusDTORecyclerViewAdapter(statusList, mListener)

            val statusService = retrofit.create(GoProInfoService::class.java)

            thread {
                if (ActivityCompat.checkSelfPermission(this@GoProStatusDTOFragment.activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this@GoProStatusDTOFragment.activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@GoProStatusDTOFragment.activity, arrayOf(Manifest.permission.INTERNET),200)
                    return@thread
                }
                sendWoL()
                Log.d(this.tag,"before WoL")
                Log.d(this.tag,"before WS")
                try {
                    var retryCount = 1
                    val totalRetries = 4
                    var response = statusService.status().execute()
                    //I may have to retry the request a couple of times until the camera is fully initialized
                    if(response.code() == 500){
                        do {
                            println("First attempt failed!\nAttempting retry #$retryCount")
                            response = statusService.status().execute()
                            retryCount++
                        } while (response.code() != 200 && (retryCount < totalRetries))
                    }
                    Log.d(this@GoProStatusDTOFragment.tag, "after WS")
                    if (response != null && response.isSuccessful) {
                        Log.d(this@GoProStatusDTOFragment.tag, "successful WS")
                        this@GoProStatusDTOFragment.activity.runOnUiThread {
                            statusList.addAll(response.body().buildViewHolderData())
                            view.adapter.notifyDataSetChanged()
                        }
                    }
                }catch (ste:SocketTimeoutException){
                    ste.printStackTrace()
                }finally {

                }
                statusService.powerOff()
                Log.d(this@GoProStatusDTOFragment.tag, "successful powerOff")
            }
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context as OnListFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: GoProStatusDTO)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        fun newInstance(columnCount: Int): GoProStatusDTOFragment {
            val fragment = GoProStatusDTOFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
