package com.aajtech.mobile.goproshowcase

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aajtech.mobile.goproshowcase.dto.GoProPrimaryModes
import com.aajtech.mobile.goproshowcase.dto.GoProSecondaryModes
import com.aajtech.mobile.goproshowcase.dto.GoProShutterModes
import com.aajtech.mobile.goproshowcase.service.*
import kotlinx.android.synthetic.main.fragment_photos.*
import kotlin.concurrent.thread


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PhotosFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotosFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_photos, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onResume() {
        super.onResume()
        photo_manual_trigger.setOnClickListener {
            thread {
                sendWoL()
                val primaryMode = retrofit.create(GoProPrimaryModeService::class.java)
                var response = primaryMode.setPrimaryMode(GoProPrimaryModes.PHOTO.mode).execute()

                if (!response.isSuccessful && response.code() == 500)
                    response = primaryMode.setPrimaryMode(GoProPrimaryModes.PHOTO.mode).execute()

                //assert(response.isSuccessful)
                Log.d(this.javaClass.name,response.body().string())
                val secondaryMode = retrofit.create(GoProSecondaryModeService::class.java)
                val response2 = secondaryMode.setSubMode(
                        GoProSecondaryModes.SINGLE_PHOTO.mode,
                        GoProSecondaryModes.SINGLE_PHOTO.subMode).execute()
                //assert(response2.isSuccessful)
                Log.d(this.javaClass.name,response2?.body()?.string())

                val trigger = retrofit.create(GoProShutterService::class.java)
                val response3 = trigger.shutterToggle(GoProShutterModes.TRIGGER_SHUTTER.mode).execute()

                //assert(response3.isSuccessful)
                Log.d(this.javaClass.name,response3?.body()?.string())
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment PhotosFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): PhotosFragment {
            val fragment = PhotosFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
