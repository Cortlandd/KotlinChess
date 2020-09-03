package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color
import com.cortland.kotlinchess.Piece

class BoardRaterThreatenedPieces(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {
        val rating = board.getPieces(color).map {
            threatValue(it, board)
        }.sum() * configuration.boardRaterThreatenedPiecesWeighting.value

        return rating
    }

    fun threatValue(piece: Piece, board: Board): Double {

        val threatenedByPieces = getPiecesThreatening(piece, board = board)
        val protectedByPieces = getPiecesProtecting(piece, board = board)
        val isThreatened = threatenedByPieces.count() > 0
        val isProtected = protectedByPieces.count() > 0

        // Threatened but not protected
        if (isThreatened && !isProtected) {
            return piece.value.unaryMinus() * 3
        }

        // Threatened, but protected (only return if the trade is not preferable)
        if (isThreatened && isProtected) {

            val lowestValueThreat = threatenedByPieces.lowestPieceValue()

            if (lowestValueThreat < piece.value) {
                return piece.value.unaryMinus()
            }

            // Here we could bump the value to encourage a good trade?
        }

        val targetPieces = getPiecesThreatenedBy(piece, board = board)
        for (targetPiece in targetPieces) {

            val isTargetProtected = isPieceProtected(targetPiece, board)

            // If it's protected, is it a good trade
            if (isTargetProtected && targetPiece.value < piece.value) {
                return 0.toDouble()
            } else {
                return targetPiece.value
            }
        }

        // Nothing much interesting
        return 0.toDouble()
    }

    // MARK: - Helpers

    fun getPiecesProtecting(piece: Piece, board: Board): List<Piece> {

        var alteredBoard = board
        alteredBoard.setPiece(piece.withOppositeColor(), piece.location)

        return alteredBoard.getPieces(piece.color).filter {
            it.movement.canPieceMove(it.location, piece.location, alteredBoard, true)
        }
    }

    fun getPiecesProtectedBy(piece: Piece, board: Board): List<Piece> {

        return board.getPieces(piece.color).filter {
            piece.movement.canPieceMove(piece.location, it.location, board, true)
        }
    }

    fun isPieceProtected(piece: Piece, board: Board): Boolean {

        var alteredBoard = board
        alteredBoard.setPiece(piece.withOppositeColor(), piece.location)

        for (square in alteredBoard.squares) {

            val squarePiece = square.piece ?: continue

            if (!(squarePiece.color == piece.color)) {
                continue
            }

            if (squarePiece.movement.canPieceMove(squarePiece.location, piece.location, alteredBoard, true)) {
                return true
            }
        }

        return false
    }

    fun isPieceThreatened(piece: Piece, board: Board): Boolean {

        for (square in board.squares) {

            val squarePiece = square.piece ?: continue

            if (squarePiece.color == piece.color.opposite()) {
                continue
            }

            if (!(squarePiece.color == piece.color.opposite())) {
                continue
            }

            if (squarePiece.movement.canPieceMove(squarePiece.location, piece.location, board, true)) {
                return true
            }
        }

        return false
    }

    fun getPiecesThreatening(threatening_piece: Piece, board: Board): List<Piece> {

        return board.getPieces(threatening_piece.color.opposite()).filter {
            it.movement.canPieceMove(it.location, threatening_piece.location, board, true)
        }
    }

    fun getPiecesThreatenedBy(threatenedBy_piece: Piece, board: Board): List<Piece> {

        return board.getPieces(threatenedBy_piece.color.opposite()).filter {
            threatenedBy_piece.movement.canPieceMove(threatenedBy_piece.location, it.location, board, true)
        }
    }

    fun canPieceMoveToSafety(piece: Piece, board: Board): Boolean {

        for (location in BoardLocation.all()) {

            if (piece.movement.canPieceMove(piece.location, location, board, true)) {

                var boardCopy = board
                boardCopy.movePiece(piece.location, location)

                val movedPiece = boardCopy.getPiece(location)
                if (!isPieceThreatened(movedPiece!!, boardCopy)) {
                    return true
                }
            }
        }

        return false
    }

}

fun Collection<Piece>.highestPieceValue(): Double {

    if (this.count() == 0) {
        return 0.toDouble()
    }

    var result = this.first().value

    for (piece in this) {
        val pieceValue = piece.value
        if (pieceValue > result) { result = pieceValue }
    }

    return result
}

fun Collection<Piece>.lowestPieceValue(): Double {
    if (this.count() == 0) {
        return 0.toDouble()
    }

    var result = this.first().value

    for (piece in this) {
        val pieceValue = piece.value
        if (pieceValue < result) { result = pieceValue }
    }

    return result
}