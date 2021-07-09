package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.BoardStride
import com.cortland.kotlinchess.Color

class BoardRaterKingSurroundingPossession(configuration: AIConfiguration) : BoardRater(configuration) {


    override fun ratingFor(board: Board, color: Color): Double {

        val squareValue = 1.toDouble()
        var rating = 0.toDouble()

        val ownKingLocations = locationsSurroundingKing(color, board)
        val opponentKingLocations = locationsSurroundingKing(color.opposite, board)

        // The kings will be able to move to their surrounding locations, so remove them from the board
        var noKingsBoard = board
        noKingsBoard.removePiece(noKingsBoard.getKingLocation(Color.white))
        noKingsBoard.removePiece(noKingsBoard.getKingLocation(Color.black))

        // we don't want to encourage the king to move out in to the open
        rating += (8 - ownKingLocations.count()).toDouble() * squareValue * 3

        for (location in ownKingLocations) {

            if (noKingsBoard.doesColorOccupyLocation(color, location)) {
                rating += squareValue
                continue
            }

            if (noKingsBoard.doesColorOccupyLocation(color.opposite, location)) {
                rating -= squareValue
                continue
            }

            if (noKingsBoard.canColorMoveAnyPieceToLocation(color, location)) {
                rating += squareValue
            } else if (noKingsBoard.canColorMoveAnyPieceToLocation(color.opposite, location)) {
                rating -= squareValue
            }

        }

        for (location in opponentKingLocations) {

            if (noKingsBoard.doesColorOccupyLocation(color, location)) {
                rating += squareValue
                continue
            }

            if (noKingsBoard.doesColorOccupyLocation(color.opposite, location)) {
                rating -= squareValue
                continue
            }

            if (noKingsBoard.canColorMoveAnyPieceToLocation(color, location)) {
                rating += squareValue
            } else if (noKingsBoard.canColorMoveAnyPieceToLocation(color.opposite, location)) {
                rating -= squareValue
            }
        }

        return rating * configuration.boardRaterKingSurroundingPossessionWeighting.value
    }

    fun locationsSurroundingKing(color: Color, board: Board): ArrayList<BoardLocation> {

        val kingLocation = board.getKingLocation(color)

        val strides = listOf(
            BoardStride(0, 1),   // N
            BoardStride(1, 1),   // NE
            BoardStride(1, 0),   // E
            BoardStride(1, -1),  // SE
            BoardStride(0, -1),  // S
            BoardStride(-1, -1), // SW
            BoardStride(-1, 0),  // W
            BoardStride(-1, 1)   // NW
        )

        var surroundingLocations = ArrayList<BoardLocation>()

        for (stride in strides) {

            if (kingLocation.canIncrement(stride)) {
                val location = kingLocation.incremented(stride)
                surroundingLocations.add(location)
            }
        }

        return surroundingLocations
    }

}