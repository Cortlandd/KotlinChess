package com.cortland.kotlinchess.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.BoardView
import com.cortland.kotlinchess.BoardViewListener
import com.cortland.kotlinchess.Game
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BoardViewListener {

    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView.boardViewListener = this

        game = Game()
        game.board.printBoardState()

    }

    override fun onLocationTouched(boardView: BoardView, location: BoardLocation) {
        println("My location is: ${location.index}")
    }
}
