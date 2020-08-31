package com.cortland.kotlinchess

class Game: PlayerListener {

    var board = Board(state = Board.InitialState.newGame)
    var whitePlayer: Player
    var blackPlayer: Player
    var currentPlayer: Player

    var gameListener: GameListener? = null

    init {
        this.whitePlayer = Player(Color.white, game = this)
        this.whitePlayer.playerListener = this

        this.blackPlayer = Player(Color.black, game = this)
        this.blackPlayer.playerListener = this

        this.currentPlayer = this.whitePlayer
    }

    override fun playerDidMakeMove(player: Player) {

        // This shouldn't happen, but print a message in case it does
        if (player != currentPlayer) {
            print("Warning - Wrong player took turn")
        }

        // Switch to the other player
        currentPlayer = if (player == whitePlayer) {
            blackPlayer
        } else{
            whitePlayer
        }

        // Inform the delegate
        this.gameListener?.gameDidChangeCurrentPlayer(this)

    }
}