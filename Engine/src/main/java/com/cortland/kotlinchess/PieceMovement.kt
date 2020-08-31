package com.cortland.kotlinchess

// MARK - PieceMovement (Base Class)

open class PieceMovement {

    companion object {

        fun pieceMovement(pieceType: PieceType): PieceMovement {
            return when(pieceType) {
                PieceType.pawn -> PieceMovementPawn()
                PieceType.rook -> PieceMovementRook()
                PieceType.knight -> PieceMovementKnight()
                PieceType.bishop -> PieceMovementBishop()
                PieceType.queen -> PieceMovementQueen()
                PieceType.king -> PieceMovementKing()
            }
        }
    }

    open fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        return false
    }

    fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board, stride: BoardStride): Boolean {

        // Get the moving piece
        val movingPiece = board.getPiece(fromLocation)

        if (movingPiece == null) {
            print("Cannot from an index that does not contain a piece")
            return false
        }

        // Increment by Stride
        if (!fromLocation.canIncrementBy(stride)) {
            return false
        }

        var testLocation = fromLocation.incrementedBy(stride)

        while (testLocation.isInBounds()) {

            // If there is a piece on the square
            board.getPiece(testLocation).let { piece ->
                if (piece!!.color == movingPiece.color) {
                    return false
                }

                if (piece.color == movingPiece.color.opposite() && testLocation == toLocation) {
                    return true
                }

                if (piece.color == movingPiece.color.opposite() && testLocation != toLocation) {
                    return false
                }
            }


            // if the square is empty
            if (testLocation == toLocation) {
                return true
            }

            // Increment by stride
            if (!testLocation.canIncrementBy(stride)) {
                return false
            }
            testLocation = testLocation.incrementedBy(stride)
        }

        return false
    }

    fun canPieceOccupySquare(pieceLocation: BoardLocation, xOffset: Int, yOffset: Int, board: Board): Boolean {

        val targetLocation = pieceLocation.incrementedBy(xOffset, yOffset)

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
            print("Cannot from an index that does not contain a piece")
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

class PieceMovementStraightLine: PieceMovement() {

    override fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {

        val strides = mutableListOf(
            BoardStride( 0, -1 ), // Down
            BoardStride(0, 1 ), // Up
            BoardStride(-1, 0 ), // Left
            BoardStride(1, 0 )  // Right
        )

        for (stride in strides) if (canPieceMove(fromLocation, toLocation, board, stride)) return true

        return false
    }

}

// MARK: - PieceMovementDiagonal

open class PieceMovementDiagonal: PieceMovement() {

    override open fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {


        val strides = mutableListOf(
            BoardStride(1, -1 ), // South East
            BoardStride(-1, -1 ), // South West
            BoardStride(1, 1 ), // North East
            BoardStride(-1, 1 )  // North West
        )

        for (stride in strides) if (canPieceMove(fromLocation, toLocation, board, stride)) return true

        return false
    }

}

// MARK: - PieceMovementQueen

open class PieceMovementQueen: PieceMovement() {

    var movements = mutableListOf(PieceMovementStraightLine(), PieceMovementDiagonal())

    open override fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        for (pieceMovement in movements) if (pieceMovement.canPieceMove(fromLocation, toLocation, board)) return true
        return false
    }
}

// MARK: - PieceMovementRook

open class PieceMovementRook: PieceMovement() {

    val straightLineMovement = PieceMovementStraightLine()

    fun isMovementPossible(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {
        return straightLineMovement.canPieceMove(fromLocation, toLocation, board)
    }
}

// MARK: - PieceMovementBishop

open class PieceMovementBishop: PieceMovement() {

    val diagonalMovement = PieceMovementDiagonal()

    override open fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {

        return diagonalMovement.canPieceMove(fromLocation, toLocation, board)

        //return false

    }
}

// MARK - PieceMovementKnight

class PieceMovementKnight: PieceMovement() {

    override fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {

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

        for (offset in offsets) {
            val offsetLocation = fromLocation.incrementedBy(offset.first, offset.second)
            if (toLocation == offsetLocation && canPieceOccupySquare(fromLocation, offset.first, offset.second, board)) return true
        }

        return false
    }

}

// MARK - PieceMovementPawn
class PieceMovementPawn: PieceMovement() {

    override fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {

        val movingPiece = board.getPiece(fromLocation)

        if (movingPiece == null) {
            print("Cannot from an index that does not contain a piece")
            return false
        }

        val color = movingPiece.color

        var forwardStrides = ArrayList<BoardStride>()

        // Add one ahead offset
        if (color == Color.white) {
            forwardStrides.add(BoardStride(0, 1))
        }
        else {
            forwardStrides.add(BoardStride(0, -1))
        }

        // Add the two ahead offset
        if (color == Color.white && fromLocation.y == 1) {
            forwardStrides.add(BoardStride(0, 2))
        }
        else if (color == Color.black && fromLocation.y == 6) {
            forwardStrides.add(BoardStride(0, -2))
        }

        for (stride in forwardStrides) {

            if (!fromLocation.canIncrementBy(stride)) {
                continue
            }

            val location = fromLocation.incrementedBy(stride)

            var piece = board.getPiece(location) ?: continue

            if (location == toLocation) {
                return true
            }
        }

        // ****** Test Diagonal locations ******
        var diagonalStrides = ArrayList<BoardStride>()

        if (color == Color.white) {
            diagonalStrides.add(BoardStride(-1, 1))
            diagonalStrides.add(BoardStride(1, 1))
        } else {
            diagonalStrides.add(BoardStride(-1, -1))
            diagonalStrides.add(BoardStride(1, -1))
        }

        for (stride in diagonalStrides) {

            if (!fromLocation.canIncrementBy(stride)) { continue }

            val location = fromLocation.incrementedBy(stride)

            if (location != toLocation) {
                continue
            }

            board.getPiece(location)?.also {
                if (it.color == color.opposite()) {
                    return true
                }
            }

        }

        return false
    }
}

// MARK - PieceMovementKing
class PieceMovementKing: PieceMovement() {

    open override fun canPieceMove(fromLocation: BoardLocation, toLocation: BoardLocation, board: Board): Boolean {


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

        for (offset in offsets) {

            val offsetLocation = fromLocation.incrementedBy(offset.first, offset.second)

            if (toLocation == offsetLocation && canPieceOccupySquare(fromLocation, offset.first, offset.second, board)) {
                return true
            }
        }

        return false
    }
}


