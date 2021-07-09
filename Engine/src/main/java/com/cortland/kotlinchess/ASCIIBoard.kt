package com.cortland.kotlinchess

import android.util.Log
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.Color.*
import kotlin.math.min

fun transformASCIIBoardInput(input: String): String {

    var boardArt = input.replace(" ", "")

    var transformedArt = StringBuilder()

    for (y in (0..7).reversed()) {
        for (x in (0..7)) {
            val index =  y*8 + x
            transformedArt.append(boardArt[index])
        }
    }

    return transformedArt.toString()
}

data class ASCIIBoard(var pieces: String = "", var colors: String = "") {

    var artString: String
    var stringContainsColors: Boolean = false

    init {

        var artString = ""

        if (pieces != "") {
            artString = pieces
        } else if (colors != "") {
            artString = colors
        }

        // Transform
        artString = transformASCIIBoardInput(artString)

        if (BuildConfig.DEBUG && artString.count() != 64) {
            error("ASCII board art must be 128 characters long")
        }

        this.artString = artString
        if (pieces != "") {
            this.stringContainsColors = false
        } else if (colors != "") {
            this.stringContainsColors = true
        }
    }

    fun board(): Board {
        var boardArt = artString

        // We only care about colours, not piece types, so just make pawns
        if (stringContainsColors) {
            boardArt = boardArt.replace("B", "p")
            boardArt = boardArt.replace("W", "P")
        }

        var board = Board(state = Board.InitialState.empty)

        // Clear all pieces on the board
        BoardLocation.all().forEach {
            board.removePiece(it)
        }

        // Setup pieces from ascii art
        (0..64-1).forEach { n ->
            val character = boardArt[n]

            val piece = piece(character)

            if (piece != null) {
                board.setPiece(piece, BoardLocation(n))
            }
        }

        return board
    }

    fun piece(character: Char): Piece? {

        var piece: Piece?

        when (character) {
            'R' ->
            piece = Piece(rook, white)
            'K' ->
            piece = Piece(knight, white)
            'B' ->
            piece = Piece(bishop, white)
            'Q' ->
            piece = Piece(queen, white)
            'G' ->
            piece = Piece(king, white)
            'P' ->
            piece = Piece(pawn, white)
            'r' ->
            piece = Piece(rook, black)
            'k' ->
            piece = Piece(knight, black)
            'b' ->
            piece = Piece(bishop, black)
            'q' ->
            piece = Piece(queen, black)
            'g' ->
            piece = Piece(king, black)
            'p' ->
            piece = Piece(pawn, black)
            else ->
            piece = null
        }

        return piece
    }

    fun indexOfCharacter(character: Char): Int {

        var index: Int?

        artString.indexOf(character).let { idx ->
            index = idx
        }

        if (BuildConfig.DEBUG && index == null) {
            error("Unable to find index of character: $character")
        }

        return index!!
    }

    fun locationOfCharacter(character: Char): BoardLocation {

        val index = indexOfCharacter(character)
        return BoardLocation(index)
    }

    fun indexesWithCharacter(character: Char): ArrayList<Int> {

        var indexes = ArrayList<Int>()

        (0..64-1).forEach {
            val aCharacter = artString[it]

            if (character == aCharacter) {
                indexes.add(it)
            }
        }

        return indexes
    }

    fun locationsWithCharacter(character: Char): ArrayList<BoardLocation> {

        val indexes = indexesWithCharacter(character)

        var locations = ArrayList<BoardLocation>()

        indexes.forEach {
            val location = BoardLocation(it)
            locations.add(location)
        }

        return locations
    }

    fun String.distance(lhs : CharSequence, rhs : CharSequence) : Int {
        if(lhs == rhs) { return 0 }
        if(lhs.isEmpty()) { return rhs.length }
        if(rhs.isEmpty()) { return lhs.length }

        val lhsLength = lhs.length + 1
        val rhsLength = rhs.length + 1

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1..rhsLength-1) {
            newCost[0] = i

            for (j in 1..lhsLength-1) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }

}