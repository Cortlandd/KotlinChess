package com.cortland.kotlinchess

import com.cortland.kotlinchess.AI.Move

open class Human: Player {

    constructor(color: Color) {
        this.color = color
    }

    // TODO: Kotlin has an issue in contrast to swift with holding values. When something below
    // runs and it runs on the board, it modifies the board as well, so when another method
    // runs, the values have changed and it can't move forward.
    @Throws(PieceMoveErrorException::class)
    fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation) {

        val game = this.game ?: return

        // Check that the game is in progress
        if (game.state != Game.State.InProgress) {
            throw PieceMoveError.gameIsNotInProgress.throwPlayerError()
        }

        // Check that we're the current player
        if (!(game.currentPlayer === this)) {
            throw PieceMoveError.notThisPlayersTurn.throwPlayerError()
        }

        // Hold copy before can canMovePiece to prevent board from changing
        val boardCopy = game.board.cloneBoard()

        // Check if move is allowed
        try {
            // Swift: will ignore the result
            // _ = canMovePiece(fromLocation, toLocation)
            canMovePiece(fromLocation, toLocation)
        } catch (e: PieceMoveErrorException) {
            throw e
        }

        game.board = boardCopy

        // Move the piece
        val operations = game.board.movePiece(fromLocation, toLocation)

        // Make pawn promotions
        val promotablePawnLocations = game.board.getLocationsOfPromotablePawns(color)
        if (BuildConfig.DEBUG && promotablePawnLocations.count() >= 2) {
            error("There should only be one pawn available for promotion at a time")
        }
        if (promotablePawnLocations.count() > 0) {

            val pawnLocation = promotablePawnLocations.first()

            game.gameListener?.promotedTypeForPawn(pawnLocation, this, Piece.PieceType.possiblePawnPromotionResultingTypes()) {

                val g = this.game ?: return@promotedTypeForPawn

                // Change the piece
                val newPiece = g.board.squares[pawnLocation.index].piece?.byChangingType(it)
                g.board.setPiece(newPiece!!, pawnLocation)

                // Add a transform piece operation
                val modifyOperation = BoardOperation(
                    BoardOperation.OperationType.transformPiece,
                    newPiece,
                    pawnLocation
                )
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

        val game = this.game ?: return

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