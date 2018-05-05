package com.grigerik.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.math.max

class QuizActivity : AppCompatActivity() {

    private val tag = "QuizActivity"
    private val keyIndex = "index"
    private val keyAnswers = "answers"
    private val keyCheats = "cheats"
    private val keyCorrect = "correctAnswers"
    private val keyNumberCheats = "numberCheats"
    private val requestCodeCheat = 0

    private lateinit var mTrueButton: Button
    private lateinit var mFalseButton: Button
    private lateinit var mCheatButton: Button
    private lateinit var mQuestionTextView: TextView
    private lateinit var mNCheatsTextView: TextView
    private var mCurrentIndex = 0
    private var mCorrectAnswers = 0
    private var nCheats = 3

    private var mQuestionBank: Array<Question> = arrayOf(
        Question(R.string.question_europe, true),
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private var arrIsCheater = BooleanArray(mQuestionBank.size, {false})
    private var arrAnswers = BooleanArray(mQuestionBank.size, {false})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate(Bundle) called")
        setContentView(R.layout.activity_quiz)

        mTrueButton = findViewById(R.id.true_button)
        mFalseButton = findViewById(R.id.false_button)
        mCheatButton = findViewById(R.id.cheat_button)
        mQuestionTextView = findViewById(R.id.question_text_view)
        mNCheatsTextView = findViewById(R.id.tries_text_view)

        mCurrentIndex = savedInstanceState?.getInt(keyIndex, 0) ?: 0
        mCorrectAnswers = savedInstanceState?.getInt(keyCorrect, 0) ?: 0
        nCheats = savedInstanceState?.getInt(keyNumberCheats, 3) ?: 3
        arrAnswers = savedInstanceState?.getBooleanArray(keyAnswers) ?: BooleanArray(mQuestionBank.size, {false})
        arrIsCheater = savedInstanceState?.getBooleanArray(keyCheats) ?: BooleanArray(mQuestionBank.size, {false})
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart() called")
        updateQuestion()
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause() called")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(tag, "onSaveInstanceState")

        outState?.putInt(keyIndex, mCurrentIndex)
        outState?.putInt(keyCorrect, mCorrectAnswers)
        outState?.putInt(keyNumberCheats, nCheats)
        outState?.putBooleanArray(keyAnswers, arrAnswers)
        outState?.putBooleanArray(keyCheats, arrIsCheater)
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy() called")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK){
            return
        }
        if (requestCode == requestCodeCheat){
            if (data == null){
                return
            }
            arrIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data)
        }
    }

    private fun checkButtons(){
        if (arrAnswers[mCurrentIndex]) {
            mTrueButton.isEnabled = false
            mFalseButton.isEnabled = false
        } else {
            mTrueButton.isEnabled = true
            mFalseButton.isEnabled = true
        }
        if (mCheatButton.isEnabled){
            var usedCheats = 0
            for (question in arrIsCheater){
                if (question) {
                    usedCheats += 1
                }
            }
            nCheats = max(3-usedCheats, 0)
            if (nCheats == 0){
                mCheatButton.isEnabled = false
            }
        }
    }

    private fun updateQuestion(){
        checkButtons()
        val question = mQuestionBank[mCurrentIndex].mTextResId
        mQuestionTextView.setText(question)
        mNCheatsTextView.text = if (nCheats>0) "You have $nCheats tips" else "You have no more hints"
    }

    private fun checkAnswer(userPressedTrue: Boolean){
        val answerIsTrue = mQuestionBank[mCurrentIndex].mAnswerTrue
        val messageResId: Int
        var mComplete = true
        arrAnswers[mCurrentIndex] = true
        checkButtons()
        if (arrIsCheater[mCurrentIndex]){
            messageResId = R.string.judgment_toast
        } else {
            if (userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast
                mCorrectAnswers += 1
            } else {
                messageResId = R.string.incorrect_toast
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show()
        for (answer in arrAnswers){
            mComplete = mComplete and answer
            if (!mComplete){
                break
            }
        }
        if (mComplete){
            val intent = ResultActivity.newIntent(this@QuizActivity, mCorrectAnswers, mQuestionBank.size)
            startActivity(intent)
        } else {
            mCurrentIndex = (mCurrentIndex+1) % mQuestionBank.size
            updateQuestion()
        }
    }

    fun trueOnClick(v:View){
        checkAnswer(true)
    }

    fun falseOnClick(v: View){
        checkAnswer(false)
    }

    fun cheatOnClick(v: View){
        val answerIsTrue = mQuestionBank[mCurrentIndex].mAnswerTrue
        val intent = CheatActivity.newIntent(this@QuizActivity, answerIsTrue)
        startActivityForResult(intent, requestCodeCheat)
    }

    fun moveOnClick(v: View){
        val size = mQuestionBank.size
        when (v.id){
            R.id.next_button, R.id.question_text_view -> mCurrentIndex = (mCurrentIndex+1) % size
            R.id.prev_button -> mCurrentIndex = (mCurrentIndex+size-1) % size
        }
        updateQuestion()
    }
}
//TODO: Доделать ограничение на подсказки
