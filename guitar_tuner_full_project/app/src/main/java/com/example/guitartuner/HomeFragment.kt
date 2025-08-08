package com.example.guitartuner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import kotlin.concurrent.thread

data class Tuning(val name: String, val strings: List<Double>)
data class Song(val title: String, val artist: String, val tuning: Tuning)

class HomeFragment : Fragment() {
    private val tunings = listOf(
        Tuning("Standard E", listOf(82.41,110.00,146.83,196.00,246.94,329.63)),
        Tuning("C# Standard", listOf(69.30,92.50,123.47,164.81,220.00,293.66)),
        Tuning("Drop D", listOf(73.42,110.00,146.83,196.00,246.94,329.63))
    )
    private val songs = listOf(
        Song("In Your Atmosphere", "John Mayer", tunings[1]),
        Song("Everlong", "Foo Fighters", tunings[2])
    )
    private var currentTuning = tunings[0]
    private var dispatcher: AudioDispatcher? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        val searchBar = v.findViewById<EditText>(R.id.searchBar)
        val searchBtn = v.findViewById<Button>(R.id.searchBtn)
        val selected = v.findViewById<TextView>(R.id.selectedTuning)
        val startBtn = v.findViewById<Button>(R.id.startTuner)

        searchBtn.setOnClickListener {
            val q = searchBar.text.toString()
            val found = searchSong(q)
            if (found != null) {
                currentTuning = found.tuning
                selected.text = "Selected tuning: ${'$'}{currentTuning.name}"
            } else {
                selected.text = "Selected tuning: ${'$'}{currentTuning.name} (no match)"
            }
        }

        startBtn.setOnClickListener { startTuner() }
        return v
    }

    private fun searchSong(query: String): Song? {
        return songs.find { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) }
    }

    private fun startTuner() {
        if (dispatcher != null) return
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 2048, 0)
        val pdh = PitchDetectionHandler { res, _ ->
            val pitch = res.pitch
            if (pitch > 0) {
                activity?.runOnUiThread {
                    val msg = "Detected pitch: %.2f Hz".format(pitch)
                    view?.findViewById<TextView>(R.id.selectedTuning)?.text = "Detected: ${'$'}msg\nTuning: ${'$'}{currentTuning.name}"
                }
            }
        }
        val pp = PitchProcessor(PitchEstimationAlgorithm.YIN, 22050f, 2048, pdh)
        dispatcher?.addAudioProcessor(pp)
        thread { dispatcher?.run() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dispatcher?.stop()
        dispatcher = null
    }
}
