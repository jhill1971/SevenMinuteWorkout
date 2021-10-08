package com.jameshill.sevenminuteworkout

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_exercise.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        setSupportActionBar(toolbar_exercise_activity)
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar_exercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setUpRestView()

    }

    override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
            }

        if (player != null){
            player!!.stop()
        }
        super.onDestroy()
    }

    private fun setUpRestView() {
        try{
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player!!.isLooping = false
            player!!.start()

        }catch (e: Exception){
            e.printStackTrace()
        }



        ll_ExerciseView.visibility = View.GONE
        ll_restView.visibility = View.VISIBLE

        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        tv_upcomingExercise.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }


    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                progressBar.progress = 10 - restProgress
                tv_timer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition ++
                setUpExerciseView()
            }

        }.start()
    }

    private fun setUpExerciseView() {
        ll_restView.visibility = View.GONE
        ll_ExerciseView.visibility = View.VISIBLE

        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()

        iv_image.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tv_ExerciseName.text = exerciseList!![currentExercisePosition].getName()

    }


    private fun setExerciseProgressBar() {
        exerciseProgressBar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                exerciseProgress++
                exerciseProgressBar.progress = 30 - exerciseProgress
                tv_ExerciseTimer.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1){
                    setUpRestView()
                }else {
                    Toast.makeText(this@ExerciseActivity,
                    "Congratulations! You have completed the 7 minute workout.",
                    Toast.LENGTH_SHORT).show()
                }
            }

        }.start()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The language is not supported.")
            }
        }else{
            Log.e("TTS", "Initialization failure")
        }

    }

    private fun speakOut (text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH, null, )
    }
}
