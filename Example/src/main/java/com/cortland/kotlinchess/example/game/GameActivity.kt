package com.cortland.kotlinchess.example.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.example.BoardView
import com.cortland.kotlinchess.example.BoardViewListener
import com.cortland.kotlinchess.example.R
import kotlinx.android.synthetic.main.content_game.*

class GameActivity : AppCompatActivity(), BoardViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setSupportActionBar(findViewById(R.id.toolbar))

        boardView.boardViewListener = this
    }

    override fun onLocationTouched(boardView: BoardView, location: BoardLocation) {

    }


}