package com.cortland.kotlinchess

import android.util.Log
import com.cortland.kotlinchess.Piece.PieceType
import com.cortland.kotlinchess.Piece.PieceType.*
import kotlin.math.max
import kotlin.math.min

class Square {

    var piece: Piece? = null

}

class Board(state: InitialState) {

    val TAG = Board::class.java.simpleName

    var squares: MutableList<Square> = mutableListOf<Square>()
        private set

    private var lastAssignedPieceTag = 0

    enum class InitialState {
        empty, newGame
    }

    init {
        // Setup squares
        (0..63).forEach {
            squares.add(Square())
        }

        // Setup for new game?
        if (state == InitialState.newGame) {
            setupForNewGame()
        }
    }

    fun setupForNewGame() {

        val pieces = listOf(rook, knight, bishop, queen, king, bishop, knight, rook)

        fun makePiece(type: Piece.PieceType, color: Color): Piece {
            lastAssignedPieceTag += 1
            return Piece(type, color, lastAssignedPieceTag)
        }

        // Setup white bottom row
        for (i in 0..7) {
            setPiece(makePiece(pieces[i], Color.white), BoardLocation(i))
        }

        // Setup white pawn row
        for (i in 8..15) {
            setPiece(makePiece(pawn, Color.white), BoardLocation(i))
        }

        // Setup black bottom row
        for (i in 56..63) {
            setPiece(makePiece(pieces[i.minus(56)], Color.black), BoardLocation(i))
        }

        // Setup black pawn row
        for (i in 48..55) {
            setPiece(makePiece(pawn, Color.black), BoardLocation(i))
        }
    }

    // Pieces

    fun setPiece(piece: Piece, location: BoardLocation) {
        squares[location.index].piece = piece
        squares[location.index].piece?.location = location
    }

    fun getPiece(location: BoardLocation): Piece? {
        return squares[location.index].piece
    }

    fun removePiece(location: BoardLocation) {
        squares[location.index].piece = null
    }

    internal fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation): ArrayList<BoardOperation> {

        if (toLocation == fromLocation) {
            return ArrayList(0)
        }

        var operations = ArrayList<BoardOperation>()

        val movingPiece = getPiece(fromLocation) ?: throw Exception("There is no piece on at (${fromLocation.x}, ${fromLocation.y})")

        val operation = BoardOperation(BoardOperation.OperationType.movePiece, movingPiece, toLocation)
        operations.add(operation)
        
        val targetPiece = getPiece(toLocation)
        if (targetPiece != null) {
            val op = BoardOperation(BoardOperation.OperationType.removePiece, targetPiece, toLocation)
            operations.add(op)
        }

        squares[toLocation.index].piece = this.squares[fromLocation.index].piece
        squares[toLocation.index].piece?.location = toLocation
        squares[toLocation.index].piece?.hasMoved = true
        squares[fromLocation.index].piece = null

        // If the moving piece is a pawn, check whether it just made an en passent move, and remove the passed piece
        (movingPiece.type == pawn).also {
            val stride = fromLocation.strideTo(toLocation)
            val enPassentStride = BoardStride(stride.x, 0)
            val enPassentLocation = fromLocation.incrementedBy(enPassentStride)

            val enPassentPiece = getPiece(enPassentLocation) ?: return@also

            if (enPassentPiece.canBeTakenByEnPassant && enPassentPiece.color == movingPiece.color.opposite) {
                squares[enPassentLocation.index].piece = null
                val op = BoardOperation(BoardOperation.OperationType.removePiece, enPassentPiece, enPassentLocation)
                operations.add(op)
            }
        }


        // Reset en passant flags
        resetEnPassantFlags()

        // If pawn has moved two squares, then need to update the en passant flag
        if (movingPiece.type == pawn) {

            val startingRow = if (movingPiece.color == Color.white) 1 else 6
            val twoAheadRow = if (movingPiece.color == Color.white) 3 else 4

            if (fromLocation.y == startingRow && toLocation.y == twoAheadRow) {
                squares[toLocation.index].piece?.canBeTakenByEnPassant = true
            }
        }

        return operations

    }

    fun resetEnPassantFlags() {

        for (i in 0 until squares.count()) {
            squares[i].piece?.canBeTakenByEnPassant = false
        }
    }

    // MARK: - Get Specific pieces

    fun getKing(color: Color): Piece {

        var king: Piece? = null

        for (square in squares) {

            val piece = square.piece ?: continue

            if (piece.isSameTypeAndColor(Piece(PieceType.king, color))) {
                king = piece
                break
            }
        }

        // We'll implitly unwrap this, because there should always be a king for each color on the board. If there isn't, it's an error
        return king!!
    }

    fun getKingLocation(color: Color): BoardLocation {

        for ((index, square) in squares.withIndex()) {

            val piece = square.piece ?: continue

            if (piece.color == color && piece.type == king) {
                return BoardLocation(index)
            }
        }

        throw Exception("Couldn't find $color king. Kings should always exist")

    }

    fun getLocationsOfColor(color: Color): ArrayList<BoardLocation> {

        val locations = ArrayList<BoardLocation>()

        for ((index, square) in squares.withIndex()) {

            val piece = square.piece ?: continue

            if (piece.color == color) {
                locations.add(BoardLocation(index))
            }
        }

        return locations
    }

    fun getPieces(color: Color): ArrayList<Piece> {

        var pieces = ArrayList<Piece>()

        for (square in squares) {

            val piece = square.piece ?: continue

            if (piece.color == color) {
                pieces.add(piece)
            }
        }

        return pieces

    }

    // MARK: - Possession

    fun canColorMoveAnyPieceToLocation(color: Color, location: BoardLocation): Boolean {

        for ((index, square) in squares.withIndex()) {

            val piece = square.piece ?: continue
            if (piece.color != color) { continue }

            if (piece.movement.canPieceMove(BoardLocation(index), location, this)) {
                return true
            }
        }

        return false
    }

    fun doesColorOccupyLocation(color: Color, location: BoardLocation): Boolean {
        val piece = getPiece(location) ?: return false
        return (if (piece.color == color) true else false)
    }

    fun possibleMoveLocationsForPiece(location: BoardLocation): ArrayList<BoardLocation> {

        val piece = squares[location.index].piece ?: return arrayListOf()

        var locations = ArrayList<BoardLocation>()

        BoardLocation.all().forEach {
            if (piece.movement.canPieceMove(location, it, this)) {
                locations.add(it)
            }
        }

        return locations
    }

    fun isColorInStalemate(color: Color): Boolean {
        return !isColorAbleToMove(color) && !isColorInCheckMate(color)
    }

    fun isColorAbleToMove(color: Color): Boolean {

        for (pieceLocation in getLocationsOfColor(color)) {

            val piece = getPiece(pieceLocation) ?: continue

            val boardLocations = BoardLocation.all()
            for (targetLocation in boardLocations) {

                val canMove = piece.movement.canPieceMove(pieceLocation, targetLocation, this)

                if (!canMove) {
                    continue
                }

                var resultBoard = this
                resultBoard.movePiece(pieceLocation, targetLocation)
                if (!resultBoard.isColorInCheck(color)) {
                    return true
                }
            }
        }

        return false
    }

    // MARK: - Check / Check mate state

    fun isColorInCheck(color: Color): Boolean {

        // Get the king location
        var kingLocation: BoardLocation? = null

        for (location in BoardLocation.all()) {

            val piece = getPiece(location) ?: continue

            if (piece.color == color && piece.type == king) {
                kingLocation = location
                break
            }
        }

        // If there is no king, then return false (some tests will be run without a king)
        if (kingLocation == null) {
            return false
        }

        // Work out if we're in check
        val oppositionLocations = getLocations(color.opposite)

        // Pieces will not move to take the king, so change it for a pawn of the same color
        var noKingBoard = this
        noKingBoard.squares[kingLocation.index].piece = Piece(pawn, color)

        for (location in oppositionLocations) {

            val piece = getPiece(location) ?: continue

            if (piece.movement.canPieceMove(location, kingLocation, noKingBoard)) {
                return true
            }
        }

        return false

    }

    fun isColorInCheckMate(color: Color): Boolean {

        if (!isColorInCheck(color)) {
            return false
        }

        for (pieceLocation in getLocationsOfColor(color)) {

            val piece = getPiece(pieceLocation) ?: continue

            for (targetLocation in BoardLocation.all()) {

                val canMove = piece.movement.canPieceMove(pieceLocation, targetLocation, this)

                if (canMove) {
                    val resultBoard = this
                    resultBoard.movePiece(pieceLocation, targetLocation)
                    if (resultBoard.isColorInCheck(color) == false) {
                        return false
                    }
                }
            }
        }

        return true
    }

    fun getLocations(color: Color): ArrayList<BoardLocation> {

        var locations = ArrayList<BoardLocation>()

        for ((index, square) in squares.withIndex()) {

            val piece = square.piece ?: continue

            if (piece.color == color) {
                locations.add(BoardLocation(index))
            }
        }

        return locations
    }

    fun canColorCastle(color: Color, side: CastleSide): Boolean {

        // Get the correct castle move
        val castleMove = CastleMove(color, side)

        // Get the pieces
        val kingPiece = getPiece(castleMove.kingStartLocation()) ?: return false
        val rookPiece = getPiece(castleMove.rookStartLocation()) ?: return false

        // Check that the pieces are of the correct types
        if (kingPiece.type != king) {
            return false
        }

        if (rookPiece.type != rook) {
            return false
        }

        // Check that neither of the pieces have moved yet
        if (kingPiece.hasMoved == true || rookPiece.hasMoved == true) {
            return false
        }

        // Check that there are no pieces between the king and the rook
        val rStart = min(castleMove.kingStartXPos, castleMove.rookStartXPos)
        val rEnd = max(castleMove.kingStartXPos, castleMove.rookStartXPos)

        // TODO: come back and look at
        // original: for xPos in rStart..<rEnd
        for (xPos in rStart until rEnd) {

            if (xPos == castleMove.kingStartXPos || xPos == castleMove.rookStartXPos) {
                continue
            }

            val location = BoardLocation(xPos, castleMove.yPos)

            if (getPiece(location) != null) {
                return false
            }

        }

        // Check that king is not currently in check
        if (isColorInCheck(color)) {
            return false
        }

        // Check that the king will not end up in, or move through check
        val kStart = min(castleMove.kingEndXPos, castleMove.kingStartXPos)
        val kEnd = max(castleMove.kingEndXPos, castleMove.kingStartXPos)
        for (xPos in kStart..kEnd) {

            if (xPos == castleMove.kingStartXPos) {
                continue
            }

            var newBoard = this
            val newLocation = BoardLocation(xPos, castleMove.yPos)
            newBoard.movePiece(castleMove.kingStartLocation(), newLocation)
            if (newBoard.isColorInCheck(color)) {
                return false
            }
        }

        return true
    }

    // TODO: Original: @discardableResult internal mutating func performCastle(color: Color, side: CastleSide) -> [BoardOperation] {
    internal fun performCastle(color: Color, side: CastleSide): List<BoardOperation> {

        assert(canColorCastle(color, side) == true, {
            "$color is unable to castle on side $side. Call canColorCastle(color: side:) first"
        })

        val castleMove = CastleMove(color, side)

        val moveKingOperations = this.movePiece(castleMove.kingStartLocation(), castleMove.kingEndLocation())
        val moveRookOperations = this.movePiece(castleMove.rookStartLocation(), castleMove.rookEndLocation())

        return moveKingOperations + moveRookOperations
    }

    fun getLocationsOfPromotablePawns(color: Color): ArrayList<BoardLocation> {

        var promotablePawnLocations = ArrayList<BoardLocation>()

        val y: Int = if (color == Color.white) 7 else 0

        for (x in 0..7) {

            val location = BoardLocation(x, y)

            val piece = this.getPiece(location) ?: continue

            if (piece.color == color && piece.type == pawn) {
                promotablePawnLocations.add(location)
            }
        }

        return promotablePawnLocations
    }

    fun printBoardColors() {

        printBoard { square ->
            square.piece?.let { piece ->
                if (piece.color == Color.white) 'W' else 'B'
            }
        }

    }

    fun printBoardPieces() {

        printBoard { square ->
            var character: Char? = null

            val piece = square.piece

            when (piece?.type) {
                pawn -> character = 'P'
                rook -> character = 'R'
                knight -> character = 'N'
                bishop -> character = 'B'
                queen -> character = 'Q'
                king -> character = 'K'
            }

            character
        }
    }

    fun printBoardState() {

        printBoard { square ->
            var character: Char? = null

            var piece = square.piece

            when(piece?.type) {
                pawn -> {
                    character = if (piece.color == Color.white) 'P' else 'p'
                }
                rook -> {
                    character = if (piece.color == Color.white) 'R' else 'r'
                }
                knight -> {
                    character = if (piece.color == Color.white) 'N' else 'n'
                }
                bishop -> {
                    character = if (piece.color == Color.white) 'B' else 'b'
                }
                queen -> {
                    character = if (piece.color == Color.white) 'Q' else 'q'
                }
                king -> {
                    character = if (piece.color == Color.white) 'K' else 'k'
                }
                null -> {
                    character = null
                }
            }

            character
        }

    }

    fun printBoard(squarePrinter: (Square) -> Char?) {

        var printString = StringBuilder()

        for (y in (0..7).reversed()) {
            for (x in 0..7) {
                val index = y * 8 + x
                val character = squarePrinter(squares[index])
                printString.append(character ?: "-")
            }

            printString.append("\n")
        }

        println(printString)
    }

}