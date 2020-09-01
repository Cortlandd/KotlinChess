package com.cortland.kotlinchess.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.BoardView
import com.cortland.kotlinchess.Interfaces.BoardViewListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    BoardViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView.boardViewListener = this
    }

    override fun onLocationTouched(boardView: BoardView, location: BoardLocation) {
        println("My location is: ${location.index}")
    }
}
