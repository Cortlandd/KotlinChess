package com.cortland.kotlinchess

import com.cortland.kotlinchess.AI.AIPlayer
import com.cortland.kotlinchess.Interfaces.GameListener
import com.cortland.kotlinchess.Interfaces.PlayerListener

class Game: PlayerListener {

    var board = Board(state = Board.InitialState.newGame)

    var whitePlayer: Player
    var blackPlayer: Player
    var currentPlayer: Player

    var gameListener: GameListener? = null

    init {
        this.whitePlayer = Human(Color.white, game = this)
        this.whitePlayer.playerListener = this

        this.blackPlayer = Human(Color.black, game = this)
        this.blackPlayer.playerListener = this

        this.currentPlayer = this.whitePlayer
    }

    override fun playerDidMakeMove(player: Player, boardOperations: ArrayList<BoardOperation>) {

        // This shouldn't happen, but print a message in case it does
        if (player !== currentPlayer) {
            print("Warning - Wrong player took turn")
        }

        // Switch to the other player
        currentPlayer = if (player === whitePlayer) {
            blackPlayer
        } else {
            whitePlayer
        }

        // Process board operations
        processBoardOperations(boardOperations)

        // Inform the delegate
        this.gameListener?.gameDidChangeCurrentPlayer(this)

    }

    fun processBoardOperations(boardOperations: ArrayList<BoardOperation>) {

        for (boardOperation in boardOperations) {
            when(boardOperation.type) {
                BoardOperation.OperationType.movePiece -> this.gameListener?.gameDidMovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.removePiece -> this.gameListener?.gameDidMovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.transformPiece -> throw Exception("ERROR on transform piece")
            }

        }
    }

}