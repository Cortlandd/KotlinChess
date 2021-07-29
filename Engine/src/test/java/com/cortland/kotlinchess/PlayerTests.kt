package com.cortland.kotlinchess
import org.junit.Test
import com.cortland.kotlinchess.Color.*
import com.cortland.kotlinchess.Player.PieceMoveError
import junit.framework.TestCase

class PlayerTests: TestCase() {

    lateinit var game: Game

    override fun setUp() {
        super.setUp()

        val firstPlayer = Human(white)
        val secondPlayer = Human(black)
        game = Game(firstPlayer, secondPlayer)
    }

    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testOccupiesSquareAtLocationReturnsTrueWhenOccupiedByPlayerPiece() {
        val location = BoardLocation(index = 0)
        assertTrue("Expected square to be occupied by player color", game.whitePlayer.occupiesSquareAt(location))
    }

    @Test
    fun testOccupiesSquareAtLocationReturnsFalseWhenSquareEmpty() {
        val location = BoardLocation(x = 0, y = 2) // <-- should be empty
        assertFalse("Expected square to not be occupied by player color",
            game.whitePlayer.occupiesSquareAt(location)
        )
    }

    @Test
    fun testOccupiesSquareAtLocationReturnsFalseWhenOccupiedByOppositeColor() {

        val location = BoardLocation(x = 0, y = 7) // <-- should be occupied by black
        assertTrue("Expected square to not be occupied by player color",
            !game.whitePlayer.occupiesSquareAt(location)
        )
    }

    // Piece move tests

    @Test
    fun testPlayerCannotMovePieceToSameLocation() {
        val location = BoardLocation(index = 0)

        try {
            val canMove = game.whitePlayer.canMovePiece(location, location)
            assertNull(canMove)
        } catch (e: Player.PieceMoveErrorException) {
            assertTrue(e.message == PieceMoveError.movingToSameLocation.description)
        }
    }

    // MARK: - Move Errors
    fun gameForTestingCallbacks(board: Board, color: Color): Game {

        val whitePlayer = Human(color = white)
        val blackPlayer = Human(color = black)

        return Game(firstPlayer = whitePlayer, secondPlayer = blackPlayer, board = board, colorToMove = color)
    }

    @Test
    fun testMoveInToCheckErrorIsThrownByMovingQueen() {

        val board = ASCIIBoard(pieces =
                    "- - - - * - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "G - - - Q - - r" +
                    "- - - - - - - -" )

        val queenLocation = board.locationOfCharacter('Q')
        val targetLocation = board.locationOfCharacter('*')

        val game = gameForTestingCallbacks(board = board.board(), color = white)

        val player = game.currentPlayer as Human? ?: error("Something went wrong in test: testMoveInToCheckErrorIsThrownByMovingQueen")

        // Assert that the correct error is thrown
        try {
            val canMove = player.canMovePiece(queenLocation, targetLocation)
            assertNull(canMove)
        } catch (e: Player.PieceMoveErrorException) {
            println(e)
            assertTrue(e.message == PieceMoveError.cannotMoveInToCheck.description)
        }
    }
}