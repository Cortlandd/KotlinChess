package com.cortland.kotlinchess

interface BoardViewListener {

    fun onLocationTouched(boardView: BoardView, location: BoardLocation)

}