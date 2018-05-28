package com.hy.app.ui.audio

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.hy.app.R
import com.hy.app.adapter.AudioAdapter
import com.hy.app.bean.AudioInfo
import com.hy.app.common.BaseActivity
import com.hy.app.util.ComUtil
import com.hy.frame.util.FileUtil
import com.hy.frame.util.HyUtil

import java.io.File
import java.util.ArrayList

/**
 * @author HeYan
 * @title
 * @time 2015/10/21 17:11
 */
class AudioActivity : BaseActivity(), AdapterView.OnItemClickListener {
    private var datas: MutableList<AudioInfo>? = null
    private var listView: ListView? = null
    private var adapter: AudioAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.act_audio
    }

    override fun initView() {
        initHeaderBack(R.string.audio)
        setOnClickListener<View>(R.id.button, null)
        setOnClickListener<View>(R.id.button2, null)
        listView = findViewById(R.id.listView, null)
    }

    override fun initData() {
        listView!!.onItemClickListener = this
        val path = HyUtil.getCachePathAudio(getCurContext()!!) + File.separator + "record.amr"

        addPath(path)
    }


    private fun requestData() {}

    override fun onViewClick(v: View) {
        when (v.id) {
            R.id.button -> recordAudio()
            R.id.button2 -> stopAudio()
        }

    }

    private var audioPath: String? = null
    private var recorder: MediaRecorder? = null

    private fun recordAudio() {
        if (recorder == null)
            recorder = MediaRecorder()

        //recorder.setAudioChannels(8000);
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        //recorder.setAudioEncoder(MediaRecorder);
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        val path = HyUtil.getCachePathAudioC(getCurContext()!!)
        audioPath = path + File.separator + System.currentTimeMillis() + ".amr"
        recorder!!.setOutputFile(audioPath)
        try {
            recorder!!.prepare()
            recorder!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun stopAudio() {
        if (recorder != null)
            try {
                recorder!!.stop()
                addPath(audioPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

    }

    private fun playAudio(path: String) {
        if (audioPath != null) {
            val player = MediaPlayer()
            try {
                player.setDataSource(path)
                player.prepare()
                player.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun addPath(path: String?) {
        if (datas == null)
            datas = ArrayList()
        try {
            val audio = AudioInfo()
            audio.path = path
            val kb = FileUtil.getFileOrFilesSize(path!!, 2)
            audio.kb = kb
            datas!!.add(audio)
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = AudioAdapter(getCurContext()!!, datas!!)
            //listView!!.adapter = adapter
        } else {
            //adapter!!.refresh(datas!!)
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val audio = parent.getItemAtPosition(position) as AudioInfo
        playAudio(audio.path)
    }
}
