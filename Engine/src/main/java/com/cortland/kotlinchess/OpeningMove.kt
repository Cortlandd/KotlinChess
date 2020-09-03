package com.cortland.kotlinchess

import com.cortland.kotlinchess.BoardLocation.GridPosition.*

data class OpeningMove(val board: Board, val fromLocation: BoardLocation, val toLocation: BoardLocation)

open class Opening {

    companion object {

        fun allOpenings(): ArrayList<Opening> {
            return arrayListOf(
                RuyLopez(),
                ItalianGame(),
                SicilianDefense(),
                QueensGambit(),
                KingsGambit()
            )
        }

        fun allOpeningMoves(color: Color): ArrayList<OpeningMove> {

            val openingMoves = ArrayList<OpeningMove>()

            allOpenings().forEach {
                openingMoves += it.moves(color)
            }

            return openingMoves
        }

    }

    fun moves(color: Color): List<OpeningMove> {

        var moves = ArrayList<OpeningMove>()

        val board = Board(Board.InitialState.newGame)
        for (locations in moveLocations()) {

            val move = OpeningMove(board, locations.first, locations.second)
            moves.add(move)

            board.movePiece(locations.first, locations.second)

        }

        val m = ArrayList<OpeningMove>()

        moves.filterNotNull().mapIndexed { index, value ->
            val c = if (color == Color.white) 0 else 1
            if (index % 2 == c) {
                m.add(value)
            } else {
                null
            }
        }

        return m

    }

    fun moveLocations(): List<Pair<BoardLocation, BoardLocation>> {
        return moveGridPositions().map {
            Pair(BoardLocation(it.first), BoardLocation(it.second.ordinal))
        }
    }

    open fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        throw Exception("Must override")
    }

}

// MARK: - Ruy Lopez

class RuyLopez: Opening() {

    override fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        val moves: ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> = arrayListOf(
            Pair(e2, e4), // white moves pawn to e4
            Pair(e7, e5), // black moves pawn to e5
            Pair(g1, f3), // white moves knight to f3
            Pair(b8, c6), // black moves knight to c6
            Pair(f1, b5)  // white moves bishop to b5
        )

        return moves
    }
}

// MARK: - Italian Game

class ItalianGame: Opening() {

    override fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        val moves: ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> = arrayListOf(
            Pair(e2, e4), // white moves pawn to e4
            Pair(e7, e5), // black moves pawn to e5
            Pair(g1, f3), // white moves knight to f3
            Pair(b8, c6), // black moves knight to c6
            Pair(f1, c4)  // white moves bishop to c4
        )

        return moves
    }
}

// MARK: - Sicilian Defense

class SicilianDefense: Opening() {

    override fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        val moves = arrayListOf(
            Pair(e2, e4), // white moves pawn to e4
            Pair(c7, c5)  // black moves pawn to c5
        )

        return moves
    }
}

// MARK: - Queens Gambit

class QueensGambit: Opening() {

    override fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        val moves = arrayListOf(
            Pair(d2, d4), // white moves pawn to d4
            Pair(d7, d5), // black moves pawn to d5
            Pair(c2, c4)  // white moves pawn to c4
        )

        return moves
    }
}

// MARK: - King's Gambit

class KingsGambit: Opening() {

    override fun moveGridPositions(): ArrayList<Pair<BoardLocation.GridPosition, BoardLocation.GridPosition>> {
        val moves = arrayListOf(
            Pair(e2, e4), // white moves pawn to e4
            Pair(e7, e5), // black moves pawn to e5
            Pair(f2, f4)  // white moves pawn to f4
        )

        return moves
    }
}