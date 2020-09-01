package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.Color

object BoardRaterCountPieces: BoardRater {

    override fun ratingfor(board: Board, color: Color): Float {
        var rating: Float = 0f
        for (square in board.squares) {
            val piece = square.piece ?: continue

            if (piece.color == color) {
                rating += piece.value()
            } else {
                rating += piece.value().unaryMinus()
            }
        }
        return rating
    }

}