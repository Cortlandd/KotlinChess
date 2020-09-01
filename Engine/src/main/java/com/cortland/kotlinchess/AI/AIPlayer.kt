package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.*

data class Move(val sourceLocation: BoardLocation, val targetLocation: BoardLocation, val rating: Float)

interface BoardRater {
    fun ratingfor(board: Board, color: Color): Float
}
open class AIPlayer: Player {

    var boardRaters: ArrayList<BoardRater> = arrayListOf(BoardRaterCountPieces)

    constructor(color: Color, game: Game) {
        this.color = color
        this.game = game
    }

    fun makeMove() {

        val board = game.board

        // Build list of possible moves with ratings

        var possibleMoves = ArrayList<Move>()

        var boardLocations = BoardLocation.all()
        for (sourceLocation in boardLocations) {

            val piece = board.getPiece(sourceLocation) ?: continue

            if (piece.color != color) {
                continue
            }

            for (targetLocation in boardLocations) {

                if (!canMovePiece(sourceLocation, targetLocation)) {
                    continue
                }

                var resultBoard = board
                resultBoard.movePiece(sourceLocation, targetLocation)
                val rating = ratingForBoard(resultBoard)
                val move = Move(sourceLocation, targetLocation, rating)
                possibleMoves.add(move)
            }
            println("finished here")
        }

        print("Found ${possibleMoves.count()} possible moves")

        // If there are no possible moves
        if (possibleMoves.count() == 0) {
            print("There are no possible moves!!!!");
        }

        // Choose move with highest rating
        var highestRating = possibleMoves.first().rating
        var highestRatedMove = possibleMoves.first()

        for (move in possibleMoves) {

            if (move.rating > highestRating) {
                highestRating = move.rating
                highestRatedMove = move
            }

            print("rating: ${move.rating}")
        }

        print("HIGHEST MOVE RATING: $highestRating")

        // Make move
        val operations = game.board.movePiece(highestRatedMove.sourceLocation, highestRatedMove.targetLocation)

        this.game.playerDidMakeMove(this, operations)

    }

    fun ratingForBoard(board: Board): Float {

        var rating: Float = 0f

        for (boardRater in boardRaters) {
            rating += boardRater.ratingfor(board, color)
        }

        return rating
    }

}

fun AIPlayer.ratingfor(board: Board, color: Color) = object : BoardRater {
    override fun ratingfor(board: Board, color: Color): Float {
        return 0f
    }
}
