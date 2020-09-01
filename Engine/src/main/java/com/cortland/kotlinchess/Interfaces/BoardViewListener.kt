package com.cortland.kotlinchess.Interfaces

import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.BoardView

interface BoardViewListener {

    fun onLocationTouched(boardView: BoardView, location: BoardLocation)

}