package edu.temple.audiobb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment


class ControlFragment : Fragment() {

    lateinit var parentInterface: ControlInterface
    lateinit var seekBar: SeekBar
    lateinit var nowPlaying: TextView
    lateinit var play: Button
    lateinit var pause: Button
    lateinit var stop: Button

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_control, container, false)
        seekBar = layout.findViewById(R.id.seekBar)

        play = layout.findViewById(R.id.play)
        play.setOnClickListener {
            parentInterface.play()
        }
        pause = layout.findViewById(R.id.pause)
        pause.setOnClickListener {
            parentInterface.pause()
        }
        stop = layout.findViewById(R.id.stopButton)
        stop.setOnClickListener {
            parentInterface.stop()
        }
        nowPlaying = layout.findViewById(R.id.nowPlaying)


        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //Log.d( "PROGRESS", "POSITION: "+i.toString())
                updateProgress(i)
                if (b) parentInterface.changeTime(i)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        return layout
    }

    fun setPlaying(title: String){
        nowPlaying.text = title
    }
    fun updateProgress(position: Int){
        seekBar.progress = position
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ControlInterface) parentInterface =
            context as ControlInterface else throw RuntimeException("Please implement ControlFragment.ControlInterface")
    }


    interface ControlInterface {
        fun play()
        fun pause()
        fun stop()
        fun changeTime(position: Int)
}
}