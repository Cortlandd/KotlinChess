package com.cortland.kotlinchess

import android.util.Log
import com.cortland.kotlinchess.AI.AIPlayer
import com.cortland.kotlinchess.Interfaces.GameListener
import com.cortland.kotlinchess.Interfaces.PlayerListener

class Game: PlayerListener {

    var board = Board(state = Board.InitialState.newGame)

    var whitePlayer: Player
    var blackPlayer: Player
    var currentPlayer: Player

    var gameListener: GameListener? = null

    constructor(firstPlayer: Player, secondPlayer: Player) {
        // Assign to correct colors
        if (firstPlayer.color == secondPlayer.color) {
            val TAG = Game::class.java.simpleName
            Log.d(TAG, "Both players cannot have the same color")
        }

        this.whitePlayer = if (firstPlayer.color == Color.white) firstPlayer else secondPlayer
        this.blackPlayer = if (firstPlayer.color == Color.black) firstPlayer else secondPlayer

        this.whitePlayer.game = this
        this.whitePlayer.playerListener = this

        this.blackPlayer.game = this
        this.blackPlayer.playerListener = this

        this.currentPlayer = this.whitePlayer
    }

    override fun playerDidMakeMove(player: Player, boardOperations: ArrayList<BoardOperation>) {

        // This shouldn't happen, but we'll print a message in case it does
        if (player !== currentPlayer) {
            println("Warning - Wrong player took turn")
        }

        // Process board operations
        processBoardOperations(boardOperations)

        // Check for game ended
        if (board.isColorInCheckMate(currentPlayer.color.opposite())) {
            gameListener?.gameWonByPlayer(this, currentPlayer)
            return
        }

        // Check for stalemate
        if (board.isColorInStalemate(currentPlayer.color.opposite())) {
            gameListener?.gameEndedInStaleMate(this)
            return
        }

        // Switch to the other player
        if (player === whitePlayer) {
            currentPlayer = blackPlayer
        } else {
            currentPlayer = whitePlayer
        }

        this.gameListener?.gameDidChangeCurrentPlayer(this)

    }

    fun processBoardOperations(boardOperations: ArrayList<BoardOperation>) {

        for (boardOperation in boardOperations) {
            when(boardOperation.type) {
                BoardOperation.OperationType.movePiece -> this.gameListener?.gameDidMovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.removePiece -> this.gameListener?.gameDidRemovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.transformPiece -> throw Exception("ERROR on transform piece")
            }

        }
    }

}