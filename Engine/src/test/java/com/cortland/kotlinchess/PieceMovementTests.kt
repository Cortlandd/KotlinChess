package com.cortland.kotlinchess

import org.junit.Test
import org.junit.Assert.*
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.Color.*

class PieceMovementTests {

    // MARK: - Board Testing

    private fun testBoard(board: ASCIIBoard, movingPiece: Char, movement: PieceMovement) {

        // Get the index of the moving piece
        val movingIndex = board.indexOfCharacter(movingPiece)

        // Test allowed locations
        val allowedIndexes = board.indexesWithCharacter('*')
        if (allowedIndexes.count() > 0) {

            for (allowedIndex in allowedIndexes) {
                assertTrue("Allowed index was invalid: $allowedIndex", movement.canPieceMove(BoardLocation(movingIndex), BoardLocation(allowedIndex), board.board()))
            }
        }

        // Test invalid locations
        val invalidIndexes = board.indexesWithCharacter('!')
        if (invalidIndexes.count() > 0) {

            for (invalidIndex in invalidIndexes) {
                assertFalse("Invalid index was valid: $invalidIndex", movement.canPieceMove(BoardLocation(movingIndex),
                    BoardLocation(invalidIndex), board.board()))
            }
        }

    }

    // MARK: - King Movement

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromCenter() {

        var board = ASCIIBoard(colors =
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! * * * ! ! !" +
                "! ! * W * ! ! !" +
                "! ! * * * ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" )

        testBoard(board, 'W', PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromTopLeft() {

        var board = ASCIIBoard(colors =
                "W * ! ! ! ! ! !" +
                "* * ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromTopRight() {

        var board = ASCIIBoard(colors =
                "! ! ! ! ! ! * W" +
                "! ! ! ! ! ! * *" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromBottomLeft() {

        var board = ASCIIBoard(colors =
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "* * ! ! ! ! ! !" +
                "W * ! ! ! ! ! !" )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromBottomRight() {

        var board = ASCIIBoard(colors =
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! ! !" +
                "! ! ! ! ! ! * *" +
                "! ! ! ! ! ! * W" )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToCurrentPosition() {

        var location = BoardLocation(3, 3)

        var board = Board(Board.InitialState.empty)
        board.setPiece(Piece(Piece.PieceType.king, Color.white), location)

        var movement = PieceMovementKing()

        assertTrue("Expected piece could not move to its current position", movement.canPieceMove(location, location, board) == false)

    }

    @Test
    fun kingMovementCanTakeOpponent() {

        var board = ASCIIBoard(colors =
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - B - - -" +
                "- - - W - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        var whiteIndex = board.indexOfCharacter('W')
        var blackIndex = board.indexOfCharacter('B')

        var movement = PieceMovementKing()
        assertTrue(movement.canPieceMove(BoardLocation(whiteIndex), BoardLocation(blackIndex), board.board()))
    }

    @Test
    fun kingMovementCannotTakeKing() {

        var board = ASCIIBoard(pieces =
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - g - - -" +
                "- - - G - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        var pieceIndex = board.indexOfCharacter('G')
        var kingIndex = board.indexOfCharacter('g')

        var movement = PieceMovementKing()
        assertFalse(movement.canPieceMove(BoardLocation(pieceIndex), BoardLocation(kingIndex), board.board()))
    }

    private fun canMakeMove(board: ASCIIBoard, from: Char, to: Char, movement: PieceMovement): Boolean {

        val movingIndex = board.indexOfCharacter(from)
        val targetIndex = board.indexOfCharacter(to)

        return movement.canPieceMove(BoardLocation(movingIndex), BoardLocation(targetIndex), board.board())
    }



    // MARK: - Straight Line Movement

    @Test
    fun testStraightLineMovementCanMoveUp() {

        val board = ASCIIBoard(colors =
                "* - - - - - - -" +
                "* - - - - - - -" +
                "* - - - - - - -" +
                "* - - - - - - -" +
                "* - - - - - - -" +
                "* - - - - - - -" +
                "* - - - - - - -" +
                "W - - - - - - -")

        testBoard(board, 'W', PieceMovementStraightLine())

    }

}