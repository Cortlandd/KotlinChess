package com.cortland.kotlinchess

class Game {

    var board = Board(state = Board.InitialState.newGame)
    var whitePlayer: Player
    var blackPlayer: Player
    var currentPlayer: Player

    init {
        this.whitePlayer = Player(Color.white, game = this)
        this.blackPlayer = Player(Color.black, game = this)
        this.currentPlayer = this.whitePlayer
    }
}