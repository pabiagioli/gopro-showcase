package com.aajtech.mobile.goproshowcase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.design.widget.Snackbar
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
 * [GoProPhotosFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GoProPhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GoProPhotosFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    var sr: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
        sr = SpeechRecognizer.createSpeechRecognizer(this.context)
        sr?.setRecognitionListener(srListener)
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
        photo_manual_listen.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test")
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            sr?.startListening(intent)
        }
        photo_manual_trigger.setOnClickListener {
            thread { takeSinglePhotoRunnable.run() }
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

    override fun onDestroy() {
        super.onDestroy()
        sr?.destroy()
        val refWatcher = GoProShowcaseApplication.getRefWatcher(activity)
        refWatcher.watch(this)
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

    val takeSinglePhotoRunnable = object : Runnable {
        override fun run() {
            sendWoL()
            //val primaryMode = retrofit.create(GoProPrimaryModeService::class.java)
            //var response = primaryMode.setPrimaryMode(GoProPrimaryModes.PHOTO.mode).execute()

            //if (!response.isSuccessful && response.code() == 500)
            //    response = primaryMode.setPrimaryMode(GoProPrimaryModes.PHOTO.mode).execute()

            //assert(response.isSuccessful)
            //Log.d(this.javaClass.name,response.body().string())
            val secondaryMode = retrofit.create(GoProSecondaryModeService::class.java)
            var response2 = secondaryMode.setSubMode(
                    GoProSecondaryModes.SINGLE_PHOTO.mode,
                    GoProSecondaryModes.SINGLE_PHOTO.subMode).execute()
            //assert(response2.isSuccessful)
            if (!response2.isSuccessful && response2.code() == 500)
                response2 = secondaryMode.setSubMode(
                        GoProSecondaryModes.SINGLE_PHOTO.mode,
                        GoProSecondaryModes.SINGLE_PHOTO.subMode).execute()

            if(response2.isSuccessful)
                Log.d(this.javaClass.name, response2?.body()?.string())

            val trigger = retrofit.create(GoProShutterService::class.java)
            val response3 = trigger.shutterToggle(GoProShutterModes.TRIGGER_SHUTTER.mode).execute()

            //assert(response3.isSuccessful)
            if(response3.isSuccessful)
                Log.d(this.javaClass.name,response3?.body()?.string())
        }
    }

    val srListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {
        }

        override fun onEndOfSpeech() {
        }

        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(javaClass.name,"onReadyForSpeech")
            Snackbar.make(photos_frame_layout, "dale trigo! ", Snackbar.LENGTH_SHORT).show()
        }

        override fun onBufferReceived(buffer: ByteArray?) {
        }

        override fun onPartialResults(partialResults: Bundle?) {
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
        }

        override fun onBeginningOfSpeech() {
        }

        override fun onError(error: Int) {
            Log.d(javaClass.name,"Error: $error")
            Snackbar.make(photos_frame_layout,"Error $error",Snackbar.LENGTH_LONG).show()
        }

        override fun onResults(results: Bundle?) {
            var str = String()
            Log.d(javaClass.name, "onResults " + results)
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            try {
                for (i in 0..data?.size?.minus(1)!!) {
                    Log.d(javaClass.name, "result " + data!![i])
                    Snackbar.make(photos_frame_layout, "results: " + data[i], Snackbar.LENGTH_LONG).show()
                    str += data[i]

                }
                //mText.setText("results: " + String.valueOf(data.size()))
                Snackbar.make(photos_frame_layout, "results: " + data?.size, Snackbar.LENGTH_LONG).show()
                if (str.contains("snap", true) || str.contains("photo", true))
                    thread { takeSinglePhotoRunnable.run() }
                    //photo_manual_trigger.performClick()
            }catch (e:Exception){
                Snackbar.make(photos_frame_layout, "hubo un error sarpado", Snackbar.LENGTH_LONG).show()
            }
        }
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
         * @return A new instance of fragment GoProPhotosFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): GoProPhotosFragment {
            val fragment = GoProPhotosFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
