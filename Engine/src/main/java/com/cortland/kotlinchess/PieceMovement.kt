package com.cortland.kotlinchess

import com.cortland.kotlinchess.Piece.*

// MARK - PieceMovement (Base Class)

val pawnMovement = PieceMovementPawn()
val rookMovement = PieceMovementRook()
val knightMovement = PieceMovementKnight()
val bishopMovement = PieceMovementBishop()
val queenMovement = PieceMovementQueen()
val kingMovement = PieceMovementKing()

open class PieceMovement {

    companion object {

        fun pieceMovement(pieceType: PieceType): PieceMovement {
            return when(pieceType) {
                PieceType.pawn -> pawnMovement
                PieceType.rook -> rookMovement
                PieceType.knight -> knightMovement
                PieceType.bishop -> bishopMovement
                PieceType.queen -> queenMovement
                PieceType.king -> kingMovement
            }
        }
    }

    // TODO: Losing pieces once here is reached
    fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board, accountForCheckState: Boolean = false): Boolean {
        if (fromLocation == toLocation) {
            return false
        }

        val canMove = isMovementPossible(fromLocation, toLocation, board)

        if (canMove && accountForCheckState) {

            val color = board.getPiece(fromLocation)!!.color

            val boardCopy = board.cloneBoard()
            boardCopy.movePiece(fromLocation, toLocation)
            return !boardCopy.isColorInCheck(color)
        } else {
            return canMove
        }
    }

    open fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        return false
    }

    private enum class Direction {
        increasing, decresing, none
    }
    fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board, stride: BoardStride): Boolean {

        var strideDirectionX = Direction.none
        if (stride.x < 0) { strideDirectionX = Direction.decresing }
        if (stride.x > 0) { strideDirectionX = Direction.increasing }

        var locationDirectionX = Direction.none
        if (toLocation.x - fromLocation.x < 0) { locationDirectionX = Direction.decresing }
        if (toLocation.x - fromLocation.x > 0) { locationDirectionX = Direction.increasing }

        if (strideDirectionX != locationDirectionX) {
            return false
        }

        var strideDirectionY = Direction.none
        if (stride.y < 0) { strideDirectionY = Direction.decresing }
        if (stride.y > 0) { strideDirectionY = Direction.increasing }

        var locationDirectionY = Direction.none
        if (toLocation.y - fromLocation.y < 0) { locationDirectionY = Direction.decresing }
        if (toLocation.y - fromLocation.y > 0) { locationDirectionY = Direction.increasing }

        if (strideDirectionY != locationDirectionY) {
            return false
        }

        // Make sure cannot take king
        val kPiece = board.getPiece(toLocation)
        if (kPiece != null) {
            if (kPiece.type == PieceType.king) {
                return false
            }
        }

        // Get the moving piece
        val movingPiece = board.getPiece(fromLocation)
        if (movingPiece == null) {
            print("Cannot move from an index that does not contain a piece")
            return false
        }

        // Increment by Stride
        if (!fromLocation.canIncrement(stride = stride)) {
            return false
        }

        var testLocation = fromLocation.incrementedBy(stride)
        while (testLocation.isInBounds()) {

            // If there is a piece on the square
            val piece = board.getPiece(testLocation)
            if (piece != null) {
                if (piece.color == movingPiece.color) {
                    return false
                }

                if (piece.color == movingPiece.color.opposite && testLocation == toLocation) {
                    return true
                }

                if (piece.color == movingPiece.color.opposite && testLocation != toLocation) {
                    return false
                }
            }


            // if the square is empty
            if (testLocation == toLocation) {
                return true
            }

            // Increment by stride
            if (!testLocation.canIncrement(stride = stride)) {
                return false
            }
            testLocation = testLocation.incrementedBy(stride)
        }

        return false
    }

    fun canPieceOccupySquare(pieceLocation: BoardLocation, xOffset: Int, yOffset: Int, board: Board): Boolean {

        val targetLocation = pieceLocation.incrementedBy(x = xOffset, y = yOffset)

        // Check if in bounds
        if (!targetLocation.isInBounds()) {
            return false
        }

        // Check if wrapped
        if (targetLocation.x - pieceLocation.x != xOffset || targetLocation.y - pieceLocation.y != yOffset) {
            return false
        }

        // Check if space is occupied
        val movingPiece = board.getPiece(pieceLocation)
        if (movingPiece == null) {
            print("Cannot move from an index that does not contain a piece")
            return false
        }

        val targetPiece = board.getPiece(targetLocation)
        if (targetPiece != null) {
            if (targetPiece.color == movingPiece.color) {
                return false
            }
        }

        return true
    }

}

// MARK - PieceMovementStraightLine

open class PieceMovementStraightLine: PieceMovement() {

    val strides = mutableListOf(
        BoardStride( 0, -1 ), // Down
        BoardStride(0, 1 ), // Up
        BoardStride(-1, 0 ), // Left
        BoardStride(1, 0 )  // Right
    )

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        val sameX = fromLocation.x == toLocation.x
        val sameY = fromLocation.y == toLocation.y

        if (!(sameX || sameY)) {
            return false
        }

        for (stride in strides) {
            if (canPieceMove(fromLocation, toLocation, board, stride)) {
                return true
            }
        }

        return false
    }

}

// MARK: - PieceMovementDiagonal

open class PieceMovementDiagonal: PieceMovement() {

    val strides = mutableListOf(
        BoardStride(1, -1 ), // South East
        BoardStride(-1, -1 ), // South West
        BoardStride(1, 1 ), // North East
        BoardStride(-1, 1 )  // North West
    )

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        if (fromLocation.isDarkSquare != toLocation.isDarkSquare) {
            return false
        }

        for (stride in strides) {
            if (canPieceMove(fromLocation, toLocation, board, stride)) {
                return true
            }
        }

        return false
    }

}

// MARK: - PieceMovementQueen

open class PieceMovementQueen: PieceMovement() {

    var movements = listOf(PieceMovementStraightLine(), PieceMovementDiagonal())

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        for (pieceMovement in movements) {

            if (pieceMovement.canPieceMove(fromLocation = fromLocation, toLocation = toLocation, board = board)) {
                return true
            }
        }

        return false
    }

}

// MARK: - PieceMovementRook

open class PieceMovementRook: PieceMovement() {

    val straightLineMovement = PieceMovementStraightLine()

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        return straightLineMovement.canPieceMove(fromLocation, toLocation, board)
    }
}

// MARK: - PieceMovementBishop

open class PieceMovementBishop: PieceMovement() {

    val diagonalMovement = PieceMovementDiagonal()

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        return diagonalMovement.canPieceMove(fromLocation, toLocation, board)
    }

}

// MARK - PieceMovementKnight

class PieceMovementKnight: PieceMovement() {

    var offsets = listOf(
        Pair(1,2),
        Pair(2,1),
        Pair(2,-1),
        Pair(-2,1),
        Pair(-1,-2),
        Pair(-2,-1),
        Pair(1,-2),
        Pair(-1,2)
    )

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        // Make sure cannot take king
        val piece = board.getPiece(toLocation)
        if (piece != null) {
            if (piece.type == PieceType.king) {
                return false
            }
        }

        for (offset in offsets) {
            val offsetLocation = fromLocation.incrementedBy(offset.first, offset.second)
            if (toLocation == offsetLocation && canPieceOccupySquare(fromLocation, offset.first, offset.second, board)) {
                return true
            }
        }

        return false
    }

}

// MARK - PieceMovementPawn
class PieceMovementPawn: PieceMovement() {

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        // Get the moving piece
        val movingPiece = board.getPiece(fromLocation) ?: return false

        if (movingPiece.color == Color.white && toLocation.y == 0) {
            return false
        }

        if (movingPiece.color == Color.black && toLocation.y == 7) {
            return false
        }

        // Make sure cannot take king
        val kPiece = board.getPiece(toLocation)
        if (kPiece != null) {
            if (kPiece.type == PieceType.king) {
                return false
            }
        }

        val color = movingPiece.color

        // ****** Test forward locations ******

        // Test one ahead offset
        val oneAheadStride = if (color == Color.white) BoardStride(0, 1) else BoardStride(0, -1)
        var canMoveOneAhead = true

        fromLocation.canIncrement(oneAheadStride).also {

            val location = fromLocation.incremented(oneAheadStride)

            if (board.getPiece(location) != null) {
                canMoveOneAhead = false
                return@also
            }

            if (location == toLocation) {
                return true
            }
        }

        // Test two ahead offset
        if (canMoveOneAhead) {

            var twoAheadStride: BoardStride? = null

            if (color == Color.white && fromLocation.y == 1) {
                twoAheadStride = BoardStride(0, 2)
            } else if (color == Color.black && fromLocation.y == 6) {
                twoAheadStride = BoardStride(0, -2)
            }

            twoAheadStride.let {
                if (it != null) {
                    val twoAheadLocation = fromLocation.incremented(it)

                    if (toLocation != twoAheadLocation) {
                        return@let
                    }

                    if (board.getPiece(twoAheadLocation) == null) {
                        return true
                    }
                }
            }

        }

        // ****** Test Diagonal locations ******
        var diagonalStrides = ArrayList<BoardStride>()

        if (color == Color.white) {
            diagonalStrides.add( BoardStride(-1, 1))
            diagonalStrides.add( BoardStride(1, 1))
        } else {
            diagonalStrides.add( BoardStride(-1, -1))
            diagonalStrides.add( BoardStride(1, -1))
        }

        for (stride in diagonalStrides) {

            if (!fromLocation.canIncrement(stride)) {
                continue
            }

            val location = fromLocation.incremented(stride = stride)

            if (location != toLocation) {
                continue
            }

            // If the target square has an opponent piece
            val piece = board.getPiece(location)
            if (piece != null) {
                if (piece.color == color.opposite) {
                    return true
                }
            }

            // If can make en passent move
            val enPassentStride = BoardStride(stride.x, 0)

            if (!fromLocation.canIncrement(enPassentStride)) {
                break
            }

            val enPassentLocation = fromLocation.incremented(enPassentStride)

            val passingPiece = board.getPiece(enPassentLocation)
            if (passingPiece == null) {
                break
            }

            if (passingPiece.canBeTakenByEnPassant && passingPiece.color == color.opposite) {
                return true
            }

        }

        return false
    }

}

// MARK - PieceMovementKing
open class PieceMovementKing: PieceMovement() {

    val offsets = listOf(
        Pair(0,1), // North
        Pair(1,1), // North-East
        Pair(1,0), // East
        Pair(1,-1), // South-East
        Pair(0,-1), // South
        Pair(-1,-1), // South-West
        Pair(-1,0), // West
        Pair(-1,1) // North- West
    )

    override fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {

        // Make sure cannot take king
        val piece = board.getPiece(toLocation)
        if (piece != null) {
            if (piece.type == PieceType.king) {
                return false
            }
        }

        for (offset in offsets) {

            val offsetLocation = fromLocation.incrementedBy(offset.first, offset.second)

            if (toLocation == offsetLocation
                && offsetLocation.isInBounds()
                && canPieceOccupySquare(fromLocation, offset.first, offset.second, board)) {

                return true
            }
        }

        return false
    }
}


