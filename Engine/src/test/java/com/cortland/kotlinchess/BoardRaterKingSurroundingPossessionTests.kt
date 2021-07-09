package com.cortland.kotlinchess

import com.cortland.kotlinchess.AI.AIConfiguration
import com.cortland.kotlinchess.AI.BoardRaterKingSurroundingPossession
import org.junit.Test
import org.junit.Assert.*
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.Color.*
import junit.framework.TestCase

class BoardRaterKingSurroundingPossessionTests: TestCase() {
    lateinit var boardRater: BoardRaterKingSurroundingPossession

    override fun setUp() {
        super.setUp()

        boardRater = BoardRaterKingSurroundingPossession(AIConfiguration(AIConfiguration.Difficulty.hard))
    }

    override fun tearDown() {
        super.tearDown()
    }

    // MARK: - Test obtaining surrounding spaces

    private fun assertSurroundingSpacesAreCorrect(color: Color, board: ASCIIBoard) {

        var indexes = board.indexesWithCharacter('*')
        var surroundingLocations = this.boardRater.locationsSurroundingKing(color, board.board())

        assertEquals("Expected same: (${indexes.count()} indexes ${surroundingLocations.count()} surrounding locations).", indexes.count(), surroundingLocations.count())

        for (index in indexes) {

            var location = BoardLocation(index)

            var foundLocation = surroundingLocations.contains(location)

            assertTrue("Expected location x: ${location.x} y: ${location.y} to be returned", foundLocation)
        }

    }

    @Test fun testWhiteKingSurroundingSpacesReturnsCorrectIndexesInCenter() {

        var board = ASCIIBoard(pieces =
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - * * * - - -" +
                "- - * G * - - -" +
                "- - * * * - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(white, board)
    }

    @Test fun testWhiteKingSurroundingSpacesReturnsCorrectIndexesInBottomLeft() {

        var board = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "* * - - - - - -" +
                "G * - - - - - -" )

        assertSurroundingSpacesAreCorrect(white, board)
    }

    @Test fun testWhiteKingSurroundingSpacesReturnsCorrectIndexesInBottomRight() {

        var board = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - * *" +
                "- - - - - - * G" )

        assertSurroundingSpacesAreCorrect(white, board)
    }

    @Test fun testWhiteKingSurroundingSpacesReturnsCorrectIndexesInTopLeft() {

        var board = ASCIIBoard(pieces = "G * - - - - - -" +
        "* * - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(white, board)
    }

    @Test fun testWhiteKingSurroundingSpacesReturnsCorrectIndexesInTopRight() {

        var board = ASCIIBoard(pieces = "- - - - - - * G" +
        "- - - - - - * *" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(white, board)
    }

    @Test fun testBlackKingSurroundingSpacesReturnsCorrectIndexesInCenter() {

        var board = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - * * * - - -" +
                "- - * g * - - -" +
                "- - * * * - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(black, board)
    }

    @Test fun testBlackKingSurroundingSpacesReturnsCorrectIndexesInBottomLeft() {

        var board = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "* * - - - - - -" +
                "g * - - - - - -" )

        assertSurroundingSpacesAreCorrect(black, board)
    }

    @Test fun testBlackKingSurroundingSpacesReturnsCorrectIndexesInBottomRight() {

        var board = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - * *" +
                "- - - - - - * g" )

        assertSurroundingSpacesAreCorrect(black, board)
    }

    @Test fun testBlackKingSurroundingSpacesReturnsCorrectIndexesInTopLeft() {

        var board = ASCIIBoard(pieces = "g * - - - - - -" +
        "* * - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(black, board)
    }

    @Test fun testBlackKingSurroundingSpacesReturnsCorrectIndexesInTopRight() {

        var board = ASCIIBoard(pieces = "- - - - - - * g" +
        "- - - - - - * *" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" )

        assertSurroundingSpacesAreCorrect(black, board)
    }

    // MARK: - Test Ratings

    @Test fun testThatGreaterOpponentPossessionOfOurKingSurroundingsResultsInLowerRating() {

        var lowRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "r - - - - - - -" +
                "- - - - G - - -" )

        var highRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "r - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var lowRating = boardRater.ratingFor(lowRatingBoard.board(), white)
        var highRating = boardRater.ratingFor(highRatingBoard.board(), white)

        assertTrue(highRating > lowRating)
    }

    @Test fun testThatGreaterPossessionOfOurKingSurroundingsResultsInHigherRating() {

        var lowRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "R - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var highRatingBoard = ASCIIBoard(pieces = "- - - - - - - -" +
        "- - - - g - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "R - - - - - - -" +
                "- - - - G - - -" )

        var lowRating = boardRater.ratingFor(lowRatingBoard.board(), white)
        var highRating = boardRater.ratingFor(highRatingBoard.board(), white)

        assertTrue(highRating > lowRating)
    }

    @Test fun testThatGreaterPossessionOfOpponentKingSurroundingsResultsInHigherRating() {

        var lowRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "R - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var highRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "R - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var lowRating = boardRater.ratingFor(lowRatingBoard.board(), white)
        var highRating = boardRater.ratingFor(highRatingBoard.board(), white)

        assertTrue(highRating > lowRating)
    }

    @Test fun testThatGreaterOpponentPossessionOfOpponentKingSurroundingsResultsInLowerRating() {

        var lowRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "r - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var highRatingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "r - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var lowRating = boardRater.ratingFor(lowRatingBoard.board(), white)
        var highRating = boardRater.ratingFor(highRatingBoard.board(), white)

        assertTrue(highRating > lowRating)
    }

    @Test fun testThatBlackandWhiteRatingsAreTheSame() {

        var board = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "Q - - - - - - b" +
                "R - - - p - - q" +
                "- k - - G - K -" )

        var invertedBoard = ASCIIBoard(pieces = "- - - - G - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "q - - - - - - B" +
                "r - - - P - - Q" +
                "- K - - g - k -" )

        var rating = boardRater.ratingFor(board.board(), white)
        var invertedRating = boardRater.ratingFor(invertedBoard.board(), black)

        assertEquals(rating, invertedRating, 0.01)
    }

    @Test fun testThatPiecesSurroundingOwnKingResultsInPositiveRatingForWhite() {

        var board = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - P P P - -" +
                "- - - P G P - -" )

        var rating = boardRater.ratingFor(board.board(), white)
        assertTrue(rating > 0)
    }

    @Test fun testThatPiecesSurroundingOwnKingResultsInPositiveRatingForBlack() {

        var board = ASCIIBoard(pieces = "- - - p g p - -" +
        "- - - p p p - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var rating = boardRater.ratingFor(board.board(), black)
        assertTrue(rating > 0)
    }

    @Test fun testThatPiecesSurroundingOpponentKingResultsInMoreNegativeRatingForWhite() {

        var openKingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var surroundedKingBoard = ASCIIBoard(pieces = "- - - p g p - -" +
        "- - - p p p - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var openKingRating = boardRater.ratingFor(openKingBoard.board(), white)
        var surroundedKingRating = boardRater.ratingFor(surroundedKingBoard.board(), white)

        assertTrue(surroundedKingRating < openKingRating)
    }

    @Test fun testThatPiecesSurroundingOpponentKingResultsInMoreNegativeRatingForBlack() {

        var openKingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - G - - -" )

        var surroundedKingBoard = ASCIIBoard(pieces = "- - - - g - - -" +
        "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - P P P - -" +
                "- - - P G P - -" )

        var openKingRating = boardRater.ratingFor(openKingBoard.board(), black)
        var surroundedKingRating = boardRater.ratingFor(surroundedKingBoard.board(), black)

        assertTrue(surroundedKingRating < openKingRating)
    }

}
