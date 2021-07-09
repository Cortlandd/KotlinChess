package com.cortland.kotlinchess

import org.junit.Test
import org.junit.Assert.*
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.Color.*

class BoardTests {
    @Test
    fun newEmptyBoardContainsNoPieces() {
        val board = Board(Board.InitialState.empty)

        (0 until 63).forEach {
            val piece = board.getPiece(BoardLocation(it))
            assertNull("Expected piece at index ${it} to be null", piece)
        }
    }

    @Test
    fun newGameBoardContainsCorrectGamePieces() {

        val expectedPieces: Array<Triple<Int, Piece.PieceType, Color>> = arrayOf(

            // white back row
            Triple(0, rook, white),
            Triple(1, knight, white),
            Triple(2, bishop, white),
            Triple(3, queen, white),
            Triple(4, king, white),
            Triple(5, bishop, white),
            Triple(6, knight, white),
            Triple(7, rook, white),

            // white pawn row
            Triple(8, pawn, white),
            Triple(9, pawn, white),
            Triple(10, pawn, white),
            Triple(11, pawn, white),
            Triple(12, pawn, white),
            Triple(13, pawn, white),
            Triple(14, pawn, white),
            Triple(15, pawn, white),

            // black back row
            Triple(56, rook, black),
            Triple(57, knight, black),
            Triple(58, bishop, black),
            Triple(59, queen, black),
            Triple(60, king, black),
            Triple(61, bishop, black),
            Triple(62, knight, black),
            Triple(63, rook, black),

            // black pawn row
            Triple(48, pawn, black),
            Triple(49, pawn, black),
            Triple(50, pawn, black),
            Triple(51, pawn, black),
            Triple(52, pawn, black),
            Triple(53, pawn, black),
            Triple(54, pawn, black),
            Triple(55, pawn, black)
        )

        val board = Board(Board.InitialState.newGame)

        for (expectedPiece in expectedPieces) {

            val piece = board.getPiece(BoardLocation(expectedPiece.first))

            if (piece == null) {
                fail("Expected piece to exist at index: ${expectedPiece.first}")
                return
            }

            assertTrue("Expected idx ${expectedPiece.first} to be type ${expectedPiece.second}, but was ${piece.type}", piece.type == expectedPiece.second)
            assertTrue("Expected idx ${expectedPiece.first} to be color ${expectedPiece.third}, but was ${piece.color}", piece.color == expectedPiece.third)
        }
    }

    @Test
    fun setAndGetPiece() {

        var board = Board(Board.InitialState.empty)

        val piece = Piece(king, black)
        val location = BoardLocation(5)

        board.setPiece(piece, location)

        val returnedPiece = board.getPiece(location)

        if (returnedPiece == null) {
            print("Expected piece to exist at location")
            return
        }

        assertTrue("Expected pieces to be the same", returnedPiece.isSameTypeAndColor(piece))

    }

    @Test
    fun getKingLocationReturnsCorrectLocation() {

        var board = Board(Board.InitialState.empty)

        val whiteLocation = BoardLocation(5)
        val blackLocation = BoardLocation(10)

        board.setPiece( Piece(king, color = white), whiteLocation)
        board.setPiece( Piece(king, color = black), blackLocation)

        assertTrue("Expected white king to be at location $whiteLocation", board.getKingLocation(color = white) == whiteLocation)

        assertTrue("Expected black king to be at location $blackLocation", board.getKingLocation(color = black) == blackLocation)

    }

    @Test
    fun getKingReturnsKing() {

        val whiteKing = Piece(king, white)
        val blackKing = Piece(king, black)

        var board = Board(Board.InitialState.empty)
        board.setPiece(whiteKing, BoardLocation(0))
        board.setPiece(blackKing, BoardLocation(1))

        assertTrue("Unable to find white king", board.getKing(white).isSameTypeAndColor(whiteKing))
        assertTrue("Unable to find black king", board.getKing(black).isSameTypeAndColor(blackKing))

    }

}