package com.cortland.kotlinchess

import org.junit.Test
import org.junit.Assert.*
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.Color.*
import com.cortland.kotlinchess.Board.InitialState.*

class PieceMovementTests {

    // MARK: - Board Testing

    private fun testBoard(board: ASCIIBoard, movingPiece: Char, movement: PieceMovement) {

        // Get the index of the moving piece
        val movingIndex = board.indexOfCharacter(movingPiece)

        // Test allowed locations
        val allowedIndexes = board.indexesWithCharacter('*')
        if (allowedIndexes.count() > 0) {

            for (allowedIndex in allowedIndexes) {
                assertTrue(
                    "Allowed index was invalid: $allowedIndex",
                    movement.canPieceMove(
                        BoardLocation(movingIndex),
                        BoardLocation(allowedIndex),
                        board.board()
                    )
                )
            }
        }

        // Test invalid locations
        val invalidIndexes = board.indexesWithCharacter('!')
        if (invalidIndexes.count() > 0) {

            for (invalidIndex in invalidIndexes) {
                assertFalse(
                    "Invalid index was valid: $invalidIndex", movement.canPieceMove(
                        BoardLocation(movingIndex),
                        BoardLocation(invalidIndex), board.board()
                    )
                )
            }
        }

    }

    // MARK: - King Movement

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromCenter() {

        var board = ASCIIBoard(
            colors =
            "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! * * * ! ! !" +
                    "! ! * W * ! ! !" +
                    "! ! * * * ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromTopLeft() {

        var board = ASCIIBoard(
            colors =
            "W * ! ! ! ! ! !" +
                    "* * ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromTopRight() {

        var board = ASCIIBoard(
            colors =
            "! ! ! ! ! ! * W" +
                    "! ! ! ! ! ! * *" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromBottomLeft() {

        var board = ASCIIBoard(
            colors =
            "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "* * ! ! ! ! ! !" +
                    "W * ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToInvalidPositionFromBottomRight() {

        var board = ASCIIBoard(
            colors =
            "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! * *" +
                    "! ! ! ! ! ! * W"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementKing())

    }

    @Test
    fun kingMovementCannotMoveToCurrentPosition() {

        var location = BoardLocation(3, 3)

        var board = Board(Board.InitialState.empty)
        board.setPiece(Piece(Piece.PieceType.king, Color.white), location)

        var movement = PieceMovementKing()

        assertTrue(
            "Expected piece could not move to its current position",
            movement.canPieceMove(location, location, board) == false
        )

    }

    @Test
    fun kingMovementCanTakeOpponent() {

        var board = ASCIIBoard(
            colors =
            "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - B - - -" +
                    "- - - W - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        var whiteIndex = board.indexOfCharacter('W')
        var blackIndex = board.indexOfCharacter('B')

        var movement = PieceMovementKing()
        assertTrue(
            movement.canPieceMove(
                BoardLocation(whiteIndex),
                BoardLocation(blackIndex),
                board.board()
            )
        )
    }

    @Test
    fun kingMovementCannotTakeKing() {

        var board = ASCIIBoard(
            pieces =
            "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - g - - -" +
                    "- - - G - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        var pieceIndex = board.indexOfCharacter('G')
        var kingIndex = board.indexOfCharacter('g')

        var movement = PieceMovementKing()
        assertFalse(
            movement.canPieceMove(
                BoardLocation(pieceIndex),
                BoardLocation(kingIndex),
                board.board()
            )
        )
    }

    // MARK: - Rook Movement
    @Test
    fun testRookCannotMoveToInvalidPositionFromCentre() {

        var board = ASCIIBoard(
            colors = "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "* * * W * * * *" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementRook())

    }

    @Test
    fun testRookCannotMoveToInvalidPositionFromTopLeft() {

        var board = ASCIIBoard(
            colors = "W * * * * * * *" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementRook())

    }

    @Test
    fun testRookCannotMoveToInvalidPositionFromTopRight() {

        var board = ASCIIBoard(
            colors = "* * * * * * * W" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementRook())

    }

    @Test
    fun testRookCannotMoveToInvalidPositionFromBottomLeft() {

        var board = ASCIIBoard(
            colors =
            "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "* ! ! ! ! ! ! !" +
                    "W * * * * * * *"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementRook())

    }

    @Test
    fun testRookCannotMoveToInvalidPositionFromBottomRight() {

        var board = ASCIIBoard(
            colors =
            "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "! ! ! ! ! ! ! *" +
                    "* * * * * * * W"
        )

        testBoard(board = board, movingPiece = 'W', movement = PieceMovementRook())

    }

    @Test
    fun testRookMovementCannotMoveToCurrentPosition() {

        var location = BoardLocation(x = 3, y = 3)

        var board = Board(state = Board.InitialState.empty)
        board.setPiece(Piece(type = rook, color = white), location)

        var movement = PieceMovementRook()

        assert(
            movement.canPieceMove(location, location, board = board) == false
        ) { "Expected piece could not move to its current position" }

    }

    @Test
    fun testRookMovementCanTakeOpponent() {

        var board = ASCIIBoard(
            colors =
            "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "W - - - B - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        var whiteIndex = board.indexOfCharacter('W')
        var blackIndex = board.indexOfCharacter('B')

        var movement = PieceMovementRook()
        assertTrue(
            movement.canPieceMove(
                BoardLocation(whiteIndex),
                BoardLocation(blackIndex),
                board = board.board()
            )
        )
    }

    @Test
    fun testRookMovementCannotTakeKing() {

        var board = ASCIIBoard(
            pieces = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "R - - - g - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        var pieceIndex = board.indexOfCharacter('R')
        var kingIndex = board.indexOfCharacter('g')

        var movement = PieceMovementRook()
        assertFalse(
            movement.canPieceMove(
                BoardLocation(pieceIndex),
                BoardLocation(kingIndex),
                board = board.board()
            )
        )
    }

    private fun canMakeMove(
        board: ASCIIBoard,
        from: Char,
        to: Char,
        movement: PieceMovement
    ): Boolean {

        val movingIndex = board.indexOfCharacter(from)
        val targetIndex = board.indexOfCharacter(to)

        return movement.canPieceMove(
            BoardLocation(movingIndex),
            BoardLocation(targetIndex),
            board.board()
        )
    }

    // MARK: - Straight Line Movement

    @Test
    fun testStraightLineMovementCanMoveUp() {

        val board = ASCIIBoard(
            colors =
            "* - - - - - - -" +
                    "* - - - - - - -" +
                    "* - - - - - - -" +
                    "* - - - - - - -" +
                    "* - - - - - - -" +
                    "* - - - - - - -" +
                    "* - - - - - - -" +
                    "W - - - - - - -"
        )

        testBoard(board, 'W', PieceMovementStraightLine())

    }

    // MARK: - Pawn Movement

    @Test
    fun testWhitePawnCanMoveAheadOneSpace() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - * - - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())

    }

    @Test
    fun testBlackPawnCanMoveAheadOneSpace() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - - * - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())

    }

    @Test
    fun testWhitePawnCanMoveAheadTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - * - - - -" +
                    "- - - - - - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())
    }

    @Test
    fun testBlackPawnCanMoveAheadTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - - - - - - -" +
                    "- - - * - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())

    }

    @Test
    fun testStartingWhitePawnCannotJumpOverPiece() {

        var board = ASCIIBoard(
            pieces = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - ! - - - -" +
                    "- - - K - - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())
    }

    @Test
    fun testStartingBlackPawnCannotJumpOverPiece() {

        var board = ASCIIBoard(
            pieces =
            "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - - k - - - -" +
                    "- - - ! - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())
    }

    @Test
    fun testNonStartingRowWhitePawnCannotMoveAheadTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - ! - - - -" +
                    "- - - - - - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())

    }

    @Test
    fun testNonStartingRowBlackPawnCannotMoveAheadTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - - - - - - -" +
                    "- - - ! - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())

    }

    @Test
    fun testStartingRowWhitePawnCannotMoveToInvalidPosition() {

        var board = ASCIIBoard(
            colors = "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! P ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())

    }

    @Test
    fun testStartingRowBlackPawnCannotMoveToInvalidPosition() {

        var board = ASCIIBoard(
            colors = "! ! ! ! ! ! ! !" +
                    "! ! ! p ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())

    }

    @Test
    fun testPawnMovementCannotMoveToCurrentPosition() {

        var location = BoardLocation(x = 3, y = 3)

        var board = Board(state = Board.InitialState.empty)
        board.setPiece(Piece(type = pawn, color = white), location)

        var movement = PieceMovementPawn()

        assertTrue(
            "Expected piece could not move to its current position",
            movement.canPieceMove(
                fromLocation = location,
                toLocation = location,
                board = board
            ) == false
        )

    }

    @Test
    fun testNonStartingRowWhitePawnCannotMoveToInvalidPosition() {

        var board = ASCIIBoard(
            colors = "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! P ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'P', movement = PieceMovementPawn())

    }

    @Test
    fun testNonStartingRowBlackPawnCannotMoveToInvalidPosition() {

        var board = ASCIIBoard(
            colors = "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! p ! ! ! !" +
                    "! ! ! * ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !" +
                    "! ! ! ! ! ! ! !"
        )

        testBoard(board = board, movingPiece = 'p', movement = PieceMovementPawn())

    }

    @Test
    fun testWhitePawnCannotTakePieceByMovingForwardOneSpace() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- p - - - - - -" +
                    "- P - - - - - -" +
                    "- - - - - - - -"
        )

        assert(
            canMakeMove(
                board = board,
                from = 'P',
                to = 'p',
                movement = PieceMovementPawn()
            ) == false
        )

    }

    @Test
    fun testBlackPawnCannotTakePieceByMovingForwardOneSpace() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- p - - - - - -" +
                    "- P - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        assert(
            canMakeMove(
                board = board,
                from = 'p',
                to = 'P',
                movement = PieceMovementPawn()
            ) == false
        )

    }

    @Test
    fun testWhitePawnCannotTakePieceByMovingForwardTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- p - - - - - -" +
                    "- - - - - - - -" +
                    "- P - - - - - -" +
                    "- - - - - - - -"
        )

        assert(
            canMakeMove(
                board = board,
                from = 'P',
                to = 'p',
                movement = PieceMovementPawn()
            ) == false
        )

    }

    @Test
    fun testBlackPawnCannotTakePieceByMovingForwardTwoSpaces() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- p - - - - - -" +
                    "- - - - - - - -" +
                    "- P - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        assert(
            canMakeMove(
                board = board,
                from = 'p',
                to = 'P',
                movement = PieceMovementPawn()
            ) == false
        )

    }

    @Test
    fun testWhitePawnCanTakePieceDiagonallyToLeft() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - p - - - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -"
        )

        assert(canMakeMove(board = board, from = 'P', to = 'p', movement = PieceMovementPawn()))

    }

    @Test
    fun testWhitePawnCanTakePieceDiagonallyToRight() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - p - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -"
        )

        assert(canMakeMove(board = board, from = 'P', to = 'p', movement = PieceMovementPawn()))

    }

    @Test
    fun testBlackPawnCanTakePieceDiagonallyToLeft() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - P - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        assert(canMakeMove(board = board, from = 'p', to = 'P', movement = PieceMovementPawn()))

    }

    @Test
    fun testBlackPawnCanTakePieceDiagonallyToRight() {

        var board = ASCIIBoard(
            colors = "- - - - - - - -" +
                    "- - - p - - - -" +
                    "- - - - P - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        assert(canMakeMove(board = board, from = 'p', to = 'P', movement = PieceMovementPawn()))

    }

    @Test
    fun testPawnMovementCannotTakeKing() {

        var board = ASCIIBoard(
            pieces = "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - g - - -" +
                    "- - - P - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -"
        )

        var pieceIndex = board.indexOfCharacter('P')
        var kingIndex = board.indexOfCharacter('g')

        var movement = PieceMovementPawn()
        assertFalse(
            movement.canPieceMove(
                fromLocation = BoardLocation(index = pieceIndex),
                toLocation = BoardLocation(kingIndex),
                board = board.board()
            )
        )
    }

    // TODO: Not Working
    // TODO: - Test pawn En Passant

    fun makeGame(board: Board, colorToMove: Color): Game {

        var whitePlayer = Human(color = white)
        var blackPlayer = Human(color = black)

        var game = Game(
            firstPlayer = whitePlayer,
            secondPlayer = blackPlayer,
            board = board,
            colorToMove = colorToMove
        )
        return game
    }

    @Test
    fun testPawnEnPassantFlagIsTrueAfterMoveTwoSpaces() {

        var board = Board(state = newGame)

        var startLocation = BoardLocation(x = 0, y = 1)
        var targetLocation = BoardLocation(x = 0, y = 3)

        var game = makeGame(board = board, colorToMove = white)

        var whitePlayer = game.currentPlayer as? Human
            ?: error("Error occurred testPawnEnPassantFlagIsTrueAfterMoveTwoSpaces()")

        try {
            whitePlayer.movePiece(fromLocation = startLocation, toLocation = targetLocation)
        } catch (e: Exception) {
            error(e)
        }

        var piece = game.board.getPiece(location = targetLocation) ?: error("")

        assertTrue(piece.color == white)
        assertTrue(piece.type == pawn)
        assertTrue(piece.canBeTakenByEnPassant)
    }

    @Test
    fun testPawnEnPassantFlagIsFalseAfterMoveOneSpace() {

        var board = Board(state = newGame)

        var startLocation = BoardLocation(x = 0, y = 1)
        var targetLocation = BoardLocation(x = 0, y = 2)

        var game = makeGame(board = board, colorToMove = white)

        var whitePlayer = game.currentPlayer as? Human
            ?: error("Error occurred testPawnEnPassantFlagIsFalseAfterMoveOneSpace()")

        try {
            whitePlayer.movePiece(fromLocation = startLocation, toLocation = targetLocation)
        } catch (e: Exception) {
            error(e)
        }

        var piece = game.board.getPiece(location = targetLocation) ?: error("")

        assertTrue(piece.color == white)
        assertTrue(piece.type == pawn)
        assertTrue(piece.canBeTakenByEnPassant == false)
    }

    @Test
    fun testPawnEnPassantFlagIsResetAfterSubsequentMove() {

        // White moves pawn
        var board = Board(state = newGame)

        var startLocation = BoardLocation(x = 0, y = 1)
        var targetLocation = BoardLocation(x = 0, y = 2)

        var game = makeGame(board = board, colorToMove = white)

        var whitePlayer = game.currentPlayer as? Human ?: error("error occurred testPawnEnPassantFlagIsResetAfterSubsequentMove()")

        try {
            whitePlayer.movePiece(fromLocation = startLocation, toLocation = targetLocation)
        } catch (e: Exception) {
            error(e)
        }

        // Black moves pawn
        var blackPlayer = game.currentPlayer as? Human ?: error("black error testPawnEnPassantFlagIsResetAfterSubsequentMove()")

        if (blackPlayer.color != black) {
            error("black not correct color")
        }

        try {
            blackPlayer.movePiece(fromLocation = BoardLocation(x = 0, y = 6), toLocation = BoardLocation(x = 0, y = 5))
        } catch (e: Exception) {
            error(e)
        }

        var piece = game.board.getPiece(targetLocation) ?: error("")

        assertTrue(piece.color == white)
        assertTrue(piece.type == pawn)
        assertTrue(piece.canBeTakenByEnPassant == false)
    }

    @Test
    fun testWhitePawnCanTakeOpponentUsingEnPassant() {

        var board = ASCIIBoard(pieces =
                    "- - - - - - - g" +
                    "p - - - - - - -" +
                    "+ - - - - - - -" +
                    "* P - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - -" +
                    "- - - - - - - G"
        )

        var game = makeGame(board = board.board(), colorToMove = black)
        var blackPlayer = game.blackPlayer as Human
        var whitePlayer = game.whitePlayer as Human

        // Black move two spaces
        try {
            blackPlayer.movePiece(
                fromLocation = board.locationOfCharacter('p'),
                toLocation = board.locationOfCharacter('*')
            )
        } catch (e: Exception) {
            error(e)
        }

        // White should be able to take the black pawn using the en passant rule
        var pieceMovement = PieceMovementPawn()
        assertTrue("Expected white to be able to make en passant move",
            pieceMovement.canPieceMove(
                fromLocation = board. locationOfCharacter ('P'), 
                toLocation = board. locationOfCharacter ('+'), 
                board = game.board
            )
        )

        try {
            whitePlayer.movePiece(
                fromLocation = board.locationOfCharacter('P'),
                toLocation = board.locationOfCharacter('+')
            )
        } catch (e: Exception) {
            error("Expected white to be able to execute en passant move")
        }

        assertTrue("Expected black pawn to be removed from board", game.board.getPiece(board.locationOfCharacter ('*')) == null)
 
    }


    @Test fun testBlackPawnCanTakeOpponentUsingEnPassant() {

        var board = ASCIIBoard(pieces =
                "- - - - - - - g" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "* p - - - - - -" +
                "+ - - - - - - -" +
                "P - - - - - - -" +
                "- - - - - - - G" )

        var game = makeGame( board = board.board(), colorToMove = white)
        var whitePlayer = game.whitePlayer as Human
        var blackPlayer = game.blackPlayer as Human

        // White move two spaces
        try {
            whitePlayer.movePiece(
                fromLocation = board.locationOfCharacter('P'),
                toLocation = board.locationOfCharacter('*')
            )
        } catch (e: Exception) {
            error(e)
        }

        // Black should be able to take the white pawn using the en passant rule
        var pieceMovement = PieceMovementPawn()
        assertTrue("Expected black to be able to make en passant move", pieceMovement.canPieceMove(
            fromLocation = board.locationOfCharacter('p'),
            toLocation = board.locationOfCharacter('+'),
            board = game.board)
        )

        try {
            blackPlayer.movePiece(board.locationOfCharacter('p'), board.locationOfCharacter('+'))
        } catch (e: Exception) {
            error(e)
        }

        assertTrue("Expected white pawn to be removed from board", game.board.getPiece(board.locationOfCharacter('*')) == null)

    }

    @Test fun testWhitePawnCannotTakeOpponentUsingEnPassantIfMoveNotMadeImmediately() {

        var board = ASCIIBoard(pieces =
                "- - - - - - - g" +
                "p - - - - - - %" +
                "+ - - - - - - -" +
                "* P - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - -" +
                "- - - - - - - &" +
                "- - - - - - - G" )

        var game = makeGame( board = board.board(), colorToMove = black)
        var whitePlayer = game.whitePlayer as Human
        var blackPlayer = game.blackPlayer as Human

        // Black move two spaces
        try {
            blackPlayer.movePiece(
                fromLocation = board.locationOfCharacter('p'),
                toLocation = board.locationOfCharacter('*')
            )
        } catch (e: Exception) {
            error(e)
        }

        // White moves king
        try {
            whitePlayer.movePiece(board.locationOfCharacter('G'), board.locationOfCharacter('&'))
        } catch (e: Exception) {
            error(e)
        }

        // Black moves king
        try {
            blackPlayer.movePiece(board.locationOfCharacter('g'), board.locationOfCharacter('%'))
        } catch (e: Exception) {
            error(e)
        }

        // White should not be able to take the black pawn using the en passant rule
        var pieceMovement = PieceMovementPawn()
        assertFalse(pieceMovement.canPieceMove(board.locationOfCharacter('P'), board.locationOfCharacter('+'), game.board))

    }


}