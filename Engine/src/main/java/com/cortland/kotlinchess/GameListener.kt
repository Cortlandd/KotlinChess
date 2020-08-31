package com.cortland.kotlinchess

interface GameListener {
    fun gameDidChangeCurrentPlayer(game: Game)
}