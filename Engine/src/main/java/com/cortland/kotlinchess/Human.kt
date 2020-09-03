package com.cortland.kotlinchess

import com.cortland.kotlinchess.AI.Move

open class Human: Player {

    constructor(color: Color) {
        this.color = color
    }

    @Throws(PieceMoveErrorException::class)
    fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation) {

        // Check that the game is in progress
        if (game.state != Game.State.InProgress) {
            throw PieceMoveError.gameIsNotInProgress.throwPlayerError()
        }

        // Check that we're the current player
        if (!(game.currentPlayer === this)) {
            throw PieceMoveError.notThisPlayersTurn.throwPlayerError()
        }

        // Check if move is allowed
        try {
            canMovePiece(fromLocation, toLocation)
        } catch (e: PieceMoveErrorException) {
            throw e
        }

        // Move the piece
        val operations = game.board.movePiece(fromLocation, toLocation)

        // Make pawn promotions
        val promotablePawnLocations = game.board.getLocationsOfPromotablePawns(color)
        assert(promotablePawnLocations.count() < 2, { "There should only be one pawn available for promotion at a time"})
        if (promotablePawnLocations.count() > 0) {

            val pawnLocation = promotablePawnLocations.first()

            this.game.gameListener?.promotedTypeForPawn(pawnLocation, this, Piece.PieceType.possiblePawnPromotionResultingTypes()) {
                // Change the piece
                val newPiece = this.game.board.squares[pawnLocation.index].piece?.byChangingType(it)
                this.game.board.setPiece(newPiece!!, pawnLocation)

                // Add a transform piece operation
                val modifyOperation = BoardOperation(BoardOperation.OperationType.transformPiece, newPiece, pawnLocation)
                operations.add(modifyOperation)

                // Inform the delegate that we've finished
                this.playerListener?.playerDidMakeMove(this, operations)
            }

        } else { // ... Or if no pawn promotions, end move

            // Inform the delegate that we made a move
            playerListener?.playerDidMakeMove(this, operations)
        }

    }

    public fun performCastleMove(side: CastleSide) {

        // Check that we're the current player
        if (!(game.currentPlayer === this)) {
            return
        }

        // Check that the castling move can be performed
        if (game.board.canColorCastle(color, side) == false) {
            return
        }

        // Make the move
        val operations = game.board.performCastle(color, side)

        // TODO: Investigate
        // Inform the delegate that we made a move
        playerListener?.playerDidMakeMove(this, operations as ArrayList<BoardOperation>)
    }

}