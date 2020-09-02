package com.cortland.kotlinchess

open class Human: Player {

    constructor(color: Color) {
        this.color = color
    }

    @Throws(PieceMoveErrorException::class)
    fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation) {

        // Check that we're the current player
        if (game.currentPlayer !== this) {
            throw PieceMoveError.notThisPlayersTurn.throwPlayerError()
        }

        // Check if move is allowed
        val canMove = canMovePieceWithError(fromLocation, toLocation)
        if (canMove.second != null) {
            throw canMove.second!!.throwPlayerError()
        }

        // Move the piece
        val operations = game.board.movePiece(fromLocation, toLocation)

        // Inform Player Listener
        playerListener?.playerDidMakeMove(this, operations)
    }

}