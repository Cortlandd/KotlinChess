package com.cortland.kotlinchess

import com.cortland.kotlinchess.Interfaces.PlayerListener

open class Player {

    lateinit var color: Color
    var game: Game? = null
    var playerListener: PlayerListener? = null

    class PieceMoveErrorException(error: PieceMoveError): Exception(error.description)

    enum class PieceMoveError(val description: String) {
        notThisPlayersTurn("Not this players turn"),
        movingToSameLocation("Moving to the same location"),
        noPieceToMove("No Piece to move"),
        pieceColorDoesNotMatchPlayerColor("Piece color does not match player color"),
        pieceUnableToMoveToLocation("Piece is unable to move to this location"),
        playerMustMoveOutOfCheck("Player must move out of check"),
        cannotMoveInToCheck("Cannot move in to check"),
        gameIsNotInProgress("Game is currently not in progress");

        fun throwPlayerError(): PieceMoveErrorException {
            return when(this) {
                notThisPlayersTurn -> PieceMoveErrorException(notThisPlayersTurn)
                movingToSameLocation -> PieceMoveErrorException(movingToSameLocation)
                noPieceToMove -> PieceMoveErrorException(noPieceToMove)
                pieceColorDoesNotMatchPlayerColor -> PieceMoveErrorException(pieceColorDoesNotMatchPlayerColor)
                pieceUnableToMoveToLocation -> PieceMoveErrorException(pieceUnableToMoveToLocation)
                playerMustMoveOutOfCheck -> PieceMoveErrorException(playerMustMoveOutOfCheck)
                cannotMoveInToCheck -> PieceMoveErrorException(cannotMoveInToCheck)
                gameIsNotInProgress -> PieceMoveErrorException(gameIsNotInProgress)
            }
        }

//        override fun equals(other: Any?): Boolean {
//            val lhs = this
//            val rhs = this
//            return lhs.name == rhs.name
//        }
    }

    fun occupiesSquareAt(location: BoardLocation): Boolean {

        val game = this.game ?: return false

        game.board.getPiece(location)?.let { piece ->
            if (piece.color == this.color) {
                return true
            }
        }

        return false
    }

    @Throws(PieceMoveErrorException::class)
    fun canMovePiece(fromLocation: BoardLocation, toLocation: BoardLocation): Boolean {
        val game = this.game ?: return false

        // Keep board copy

        // We can't move to our current location
        if (fromLocation == toLocation) {
            throw PieceMoveError.movingToSameLocation.throwPlayerError()
        }

        // Get the piece
        val piece = game.board.getPiece(fromLocation) ?: throw PieceMoveError.noPieceToMove.throwPlayerError()

        // Check that the piece color matches the player color
        if (piece.color != this.color) {
            throw PieceMoveError.pieceColorDoesNotMatchPlayerColor.throwPlayerError()
        }

        // Make sure the piece can move to the location
        if (!piece.movement.canPieceMove(fromLocation = fromLocation, toLocation = toLocation, board = game.board)) {
            println("Piece at $fromLocation cannot move to $toLocation")
            throw PieceMoveError.pieceUnableToMoveToLocation.throwPlayerError()
        }

        // Make sure we are not leaving the board state in check
        val inCheckBeforeMove = game.board.isColorInCheck(this.color)

        val board = game.board.cloneBoard()

        // Move the Piece
        board.movePiece(fromLocation, toLocation)

        val inCheckAfterMove = board.isColorInCheck(this.color)

        // Return
        if (inCheckBeforeMove && inCheckAfterMove) {
            throw PieceMoveError.playerMustMoveOutOfCheck.throwPlayerError()
        }

        if (!inCheckBeforeMove && inCheckAfterMove) {
            throw PieceMoveError.cannotMoveInToCheck.throwPlayerError()
        }

        return true
    }

}