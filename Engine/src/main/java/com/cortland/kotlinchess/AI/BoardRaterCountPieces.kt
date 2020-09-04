package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.Color

class BoardRaterCountPieces(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {

        var rating: Double = 0.toDouble()

        for (square in board.squares) {

            val piece = square.piece ?: continue

            rating += if (piece.color == color) piece.value else piece.value.unaryMinus()
        }

        return rating * configuration.boardRaterCountPiecesWeighting.value

    }

}