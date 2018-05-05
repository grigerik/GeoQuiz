package com.grigerik.geoquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Button
import android.widget.TextView

class CheatActivity : AppCompatActivity() {

    private val tag = "CheatActivity"
    private val youCheater = "playerCheater"
    private var mAnswerIsTrue = true
    private var wasShown = false
    private lateinit var mAnswerTextView: TextView
    private lateinit var mSdkTextView: TextView
    private lateinit var mShowAnswerButton: Button

    companion object {

        private val extra_answerIsTrue = "com.grigerik.geoquiz.answer_is_true"
        private val extra_answerShown = "com.grigerik.geoquiz.answer_shown"

        fun newIntent(packageContext: Context, answerIsTrue:Boolean) = Intent(packageContext, CheatActivity::class.java)
                .putExtra(extra_answerIsTrue, answerIsTrue)

        fun wasAnswerShown(result: Intent) = result.getBooleanExtra(extra_answerShown, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        mAnswerIsTrue = intent.getBooleanExtra(extra_answerIsTrue, false)
        wasShown = savedInstanceState?.getBoolean(youCheater, false) ?: false
        mAnswerTextView = findViewById(R.id.answer_text_view)
        mShowAnswerButton = findViewById(R.id.show_answer_button)
        mSdkTextView = findViewById(R.id.version_android_text)
        mSdkTextView.text = "API Level ${Build.VERSION.SDK_INT}"
        setAnswerShownResult(wasShown)

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(tag, "onSaveInstanceState")
        outState?.putBoolean(youCheater, wasShown)
    }

    fun showAnswerOnClick(v:View){
        mAnswerTextView.setText(if (mAnswerIsTrue) R.string.true_button else R.string.false_button)
        wasShown = true
        setAnswerShownResult(wasShown)
        val cx = mShowAnswerButton.width / 2
        val cy = mShowAnswerButton.height / 2
        val radius = mShowAnswerButton.width.toFloat()
        val anim = ViewAnimationUtils
                .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0f)
        anim.addListener(object:AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation:Animator) {
                super.onAnimationEnd(animation)
                mShowAnswerButton.visibility = View.INVISIBLE
            }
        })
        anim.start()
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean){
        val data = Intent()
                .putExtra(extra_answerShown, isAnswerShown)
        setResult(Activity.RESULT_OK, data)
    }
}
