package com.jameshill.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_exercise.*

class ExerciseActivity : AppCompatActivity() {

    private var restTimer: CountDownTimer? = null
    private var restProgress: Int = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

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
        super.onDestroy()
    }

    private fun setUpRestView() {
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
}
