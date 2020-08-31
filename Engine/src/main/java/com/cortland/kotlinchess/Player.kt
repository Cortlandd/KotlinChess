package com.cortland.kotlinchess

class Player(var color: Color, var game: Game) {

    enum class PieceMoveError {
        movingToSameLocation,
        noPieceToMove,
        pieceColorDoesNotMatchPlayerColor,
        pieceUnableToMoveToLocation;

        fun printDescription(): String {
            return when(this) {
                movingToSameLocation -> movingToSameLocation.printDescription()
                noPieceToMove -> noPieceToMove.printDescription()
                pieceColorDoesNotMatchPlayerColor -> pieceColorDoesNotMatchPlayerColor.printDescription()
                pieceUnableToMoveToLocation -> pieceUnableToMoveToLocation.printDescription()
            }
        }
    }

    fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation) {
        // Check if move is allowed
        val canMove = canMovePieceWithError(fromLocation, toLocation)
        println("canmove $canMove")

        // Move the piece
        game.board.movePiece(fromLocation, toLocation)
    }

    fun occupiesSquareAt(location: BoardLocation): Boolean {

        this.game.board.getPiece(location).let { piece ->
            if (piece?.color == this.color) {
                return true
            }
        }

        return false
    }

    fun canMovePiece(fromLocation: BoardLocation, toLocation: BoardLocation): Boolean {
        return canMovePieceWithError(fromLocation, toLocation)
    }

    fun canMovePieceWithError(fromLocation: BoardLocation, toLocation: BoardLocation): Boolean {

        // We can't move to our current location
        if (fromLocation == toLocation) {
            println("Cannot move piece to its current location")
            return false
        }

        // Get the piece
        val piece = this.game.board.getPiece(fromLocation) ?: return false

        // Check that the piece color matches the player color
        if (piece.color != this.color) {
            println("Player color $color cannot move piece of color ${piece.color}")
            return false
        }

        // Make sure the piece can move to the location
        if (!piece.movement.canPieceMove(fromLocation, toLocation, game.board)) {
            println("Piece at $fromLocation cannot move to $toLocation")
            return false
        }

        // Move the piece
        println("Piece can be moved")
        return true
    }

}