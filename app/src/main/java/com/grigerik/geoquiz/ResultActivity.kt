package com.grigerik.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private var playerResult = 0
    private var numberQuestions = 0
    private lateinit var mReplayButton: Button
    private lateinit var mResultText: TextView

    companion object {

        private val extra_playerResult = "com.grigerik.geoquiz.player_result"
        private val extra_nQuestions = "com.grigerik.geoquiz.nQuestions"

        fun newIntent(packageContext: Context, result: Int, size: Int) = Intent(packageContext, ResultActivity::class.java)
                .putExtra(extra_playerResult, result)
                .putExtra(extra_nQuestions, size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        mReplayButton = findViewById(R.id.replay_button)
        mReplayButton.setOnClickListener {

        }
        playerResult = intent.getIntExtra(extra_playerResult,0)
        numberQuestions = intent.getIntExtra(extra_nQuestions, 0)
        mResultText = findViewById(R.id.your_result)
        mResultText.text = "$playerResult/$numberQuestions"
    }
}
//TODO: сделать возможность повторной игры
