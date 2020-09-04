package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color
import com.cortland.kotlinchess.Piece

class BoardRaterPawnProgression(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {

        var rating = 0.toDouble()

        for (location in BoardLocation.all()) {

            val piece = board.getPiece(location) ?: continue

            if (piece.type != Piece.PieceType.pawn) {
                continue
            }

            val pawnRating = progressionRatingForPawn(location, piece.color)
            rating += if (piece.color == color) pawnRating else -pawnRating
        }

        return rating
    }

    fun progressionRatingForPawn(location: BoardLocation, color: Color): Double {

        var squaresAdvanced: Int = 0

        if (color == Color.white) {

            if (location.y < 2) {
                return 0.toDouble()
            }

            squaresAdvanced = location.y - 2
        } else {

            if (location.y > 5) {
                return 0.toDouble()
            }

            squaresAdvanced = 7 - (location.y + 2)
        }

        return (squaresAdvanced) * configuration.boardRaterPawnProgressionWeighting.value
    }

}