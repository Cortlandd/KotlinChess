package com.cortland.kotlinchess

import com.cortland.kotlinchess.Interfaces.PlayerListener

open class Player {

    lateinit var color: Color
    lateinit var game: Game
    var playerListener: PlayerListener? = null

    class PieceMoveErrorException(error: PieceMoveError): Exception(error.description)

    enum class PieceMoveError(val description: String) {
        notThisPlayersTurn("Not this players turn"),
        movingToSameLocation("Moving to the same location"),
        noPieceToMove("No Piece to move"),
        pieceColorDoesNotMatchPlayerColor("Piece color does not match player color"),
        pieceUnableToMoveToLocation("Piece is unable to move to this location"),
        playerMustMoveOutOfCheck("Player must move out of check"),
        cannotMoveInToCheck("Cannot move in to check");

        fun throwPlayerError(): PieceMoveErrorException {
            return when(this) {
                notThisPlayersTurn -> PieceMoveErrorException(notThisPlayersTurn)
                movingToSameLocation -> PieceMoveErrorException(movingToSameLocation)
                noPieceToMove -> PieceMoveErrorException(noPieceToMove)
                pieceColorDoesNotMatchPlayerColor -> PieceMoveErrorException(pieceColorDoesNotMatchPlayerColor)
                pieceUnableToMoveToLocation -> PieceMoveErrorException(pieceUnableToMoveToLocation)
                playerMustMoveOutOfCheck -> PieceMoveErrorException(playerMustMoveOutOfCheck)
                cannotMoveInToCheck -> PieceMoveErrorException(cannotMoveInToCheck)
            }
        }
    }

    fun occupiesSquareAt(location: BoardLocation): Boolean {

        this.game.board.getPiece(location)?.let { piece ->
            if (piece.color == this.color) {
                return true
            }
        }

        return false
    }

    fun canMovePiece(fromLocation: BoardLocation, toLocation: BoardLocation): Boolean {
        return canMovePieceWithError(fromLocation, toLocation).first
    }

    fun canMovePieceWithError(fromLocation: BoardLocation, toLocation: BoardLocation): Pair<Boolean, PieceMoveError?> {

        // We can't move to our current location
        if (fromLocation == toLocation) {
            return Pair(false, PieceMoveError.movingToSameLocation)
        }

        // Get the piece
        val piece = this.game.board.getPiece(fromLocation) ?: return Pair(false, PieceMoveError.noPieceToMove)

        // Check that the piece color matches the player color
        if (piece.color != this.color) {
            return Pair(false, PieceMoveError.pieceColorDoesNotMatchPlayerColor)
        }

        // Make sure the piece can move to the location
        if (!piece.movement.canPieceMove(fromLocation, toLocation, game.board)) {
            println("Piece at $fromLocation cannot move to $toLocation")
            return Pair(false, PieceMoveError.pieceUnableToMoveToLocation)
        }

        // Make sure we are not leaving the board state in check
        val inCheckBeforeMove = this.game.board.isColorInCheck(this.color)

        val board = this.game.board

        // TODO: Look back at this
        //board.movePiece(fromLocation, toLocation)
        val inCheckAfterMove = board.isColorInCheck(this.color)

        if (inCheckBeforeMove && inCheckAfterMove) {
            return Pair(false, PieceMoveError.playerMustMoveOutOfCheck)
        }

        if (!inCheckBeforeMove && inCheckAfterMove) {
            return Pair(false, PieceMoveError.cannotMoveInToCheck)
        }

        return Pair(true, null)
    }

}