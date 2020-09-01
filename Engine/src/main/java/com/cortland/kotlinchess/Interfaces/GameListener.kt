package com.cortland.kotlinchess.Interfaces

import com.cortland.kotlinchess.Game

interface GameListener {
    fun gameDidChangeCurrentPlayer(game: Game)
}