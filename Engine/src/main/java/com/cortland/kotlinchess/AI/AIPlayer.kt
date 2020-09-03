package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import kotlin.collections.ArrayList

data class Move(val type: MoveType, val rating: Double) {

    sealed class MoveType {
        data class SinglePiece(val fromLocation: BoardLocation, val toLocation: BoardLocation): MoveType()
        data class Castle(val color: Color, val side: CastleSide): MoveType()
    }

}

open class AIPlayer: Player {

    var boardRaters = ArrayList<BoardRater>()
    val configuration: AIConfiguration
    var openingMoves = ArrayList<OpeningMove>()


    constructor(color: Color, configuration: AIConfiguration) {
        this.configuration = configuration

        this.boardRaters = arrayListOf(
            BoardRaterCountPieces(configuration),
            BoardRaterCenterOwnership(configuration),
            BoardRaterBoardDominance(configuration),
            BoardRaterCenterDominance(configuration),
            BoardRaterThreatenedPieces(configuration),
            BoardRaterPawnProgression(configuration),
            BoardRaterKingSurroundingPossession(configuration),
            BoardRaterCheckMateOpportunity(configuration),
            BoardRaterCenterFourOccupation(configuration)
        )

        openingMoves = Opening.allOpeningMoves(color)

        this.color = color
    }

    fun makeMoveAsync() {

        // Swift
//        DispatchQueue.global(qos: .background).async {
//          this.makeMoveSync()
//        }

        // Kotlin
        GlobalScope.async {
            makeMoveSync()
        }

    }

    public fun makeMoveSync() {

        //print("\n\n****** Make Move ******");

        // Check that the game is in progress
        if (!(game.state == Game.State.InProgress)) {
            return
        }

        val board = game.board
        var move: Move

        // Get an opening move
        val openingMove = openingMove(board)
        if (openingMove != null) {
            move = openingMove
        } else { // Or, get the Highest rated move
            move = highestRatedMove(board)
        }

        // Make move
        var operations = ArrayList<BoardOperation>()

        // TODO: figure out
        when(move.type) {
            is Move.MoveType.SinglePiece -> game.board.movePiece((move.type as Move.MoveType.SinglePiece).fromLocation, (move.type as Move.MoveType.SinglePiece).toLocation)
            is Move.MoveType.Castle -> game.board.performCastle((move.type as Move.MoveType.Castle).color, (move.type as Move.MoveType.Castle).side)
        }

        /*
        switch move.type {
            case .singlePiece(let sourceLocation, let targetLocation):
            operations = game.board.movePiece(from: sourceLocation, to: targetLocation)
            case .castle(let color, let side):
            operations = game.board.performCastle(color: color, side: side)
        }
        */

        // Promote pawns
        val pawnsToPromoteLocations = game.board.getLocationsOfPromotablePawns(color)

        assert(pawnsToPromoteLocations.count() < 2) {"There should only ever be one pawn to promote at any time" }

        if (pawnsToPromoteLocations.count() > 0) {
            game.board = promotePawns(game.board)

            val location = pawnsToPromoteLocations.first()
            val transformOperation = BoardOperation(BoardOperation.OperationType.transformPiece, game.board.getPiece(location)!!, location)
            operations.add(transformOperation)
        }

        val strongGame = this.game
        GlobalScope.async {
            strongGame.playerDidMakeMove(player = this@AIPlayer, boardOperations = operations)
        }
//        DispatchQueue.main.async {
//            strongGame.playerDidMakeMove(this, operations)
//        }
    }

    fun openingMove(board: Board): Move? {

        val possibleMoves = openingMoves.filter { it.board == board }

        if (!(possibleMoves.count() > 0)) {
            return null
        }

        val index = Random().nextInt(possibleMoves.count())
        val openingMove = possibleMoves[index]

        return Move(Move.MoveType.SinglePiece(openingMove.fromLocation, openingMove.toLocation), 0.toDouble())
    }

    fun highestRatedMove(board: Board): Move {

        var possibleMoves = ArrayList<Move>()

        for (sourceLocation in BoardLocation.all()) {

            val piece = board.getPiece(sourceLocation)
            if (piece == null) {
                continue
            }

            if (piece.color != color) {
                continue
            }

            for (targetLocation in BoardLocation.all()) {

                if (!canAIMovePiece(sourceLocation, targetLocation)) {
                    continue
                }

                // Make move
                var resultBoard = board
                resultBoard.movePiece(sourceLocation, targetLocation)

                // Promote pawns
                val pawnsToPromoteLocations = resultBoard.getLocationsOfPromotablePawns(color)
                assert(pawnsToPromoteLocations.count() < 2) { "There should only ever be one pawn to promote at any time" }
                if (pawnsToPromoteLocations.count() > 0) {
                    resultBoard = promotePawns(resultBoard)
                }

                // Rate
                var rating = ratingForBoard(resultBoard)

                // reduce rating if suicide
                if (resultBoard.canColorMoveAnyPieceToLocation(color.opposite(), targetLocation)) {
                    rating -= (Math.abs(rating) * configuration.suicideMultipler.value)
                }

                val move = Move(Move.MoveType.SinglePiece(sourceLocation, targetLocation), rating)
                possibleMoves.add(move)
                // print("Rating: \(rating)")
            }
        }

        // Add castling moves
        val castleSides = arrayListOf(CastleSide.kingSide, CastleSide.queenSide)
        for (side in castleSides) {

            if (!(game.board.canColorCastle(color, side))) {
                continue
            }

            // Perform the castling move
            var resultBoard = board
            resultBoard.performCastle(color, side)

            // Rate
            val rating = ratingForBoard(resultBoard)
            val move = Move(Move.MoveType.Castle(color, side), rating)
            possibleMoves.add(move)
        }

        //print("Found \(possibleMoves.count) possible moves")

        // If there are no possible moves, we must be in stale mate
        if (possibleMoves.count() == 0) {
            print("There are no possible moves!!!!")
        }

        // Choose move with highest rating
        var highestRating = possibleMoves.first().rating
        var highestRatedMove = possibleMoves.first()

        for (move in possibleMoves) {

            if (move.rating > highestRating) {
                highestRating = move.rating
                highestRatedMove = move
            }

            //print("rating: \(move.rating)")
        }

        return highestRatedMove
    }

    fun canAIMovePiece(fromLocation: BoardLocation, toLocation: BoardLocation): Boolean {

        // This is a stricter version of the canMove function, used by the AI, that returns false for errors
        return try {
            canMovePiece(fromLocation, toLocation)
        } catch (e: Exception) {
            false
        }

    }

    fun ratingForBoard(board: Board): Double {

        var rating: Double = 0.0

        for (boardRater in boardRaters) {

            val result = boardRater.ratingFor(board, color)

            //let className = "\(boardRater)"
            //print("\t\(className): \(result)")
            rating += result
        }

        // If opponent is in check mate, set the maximum rating
        if (board.isColorInCheckMate(color.opposite())) {
            rating = Double.MAX_VALUE
        }

        return rating
    }

    fun promotePawns(board: Board): Board {

        val pawnsToPromoteLocations = board.getLocationsOfPromotablePawns(color)

        if (!(pawnsToPromoteLocations.count() > 0)) {
            return board
        }

        assert(pawnsToPromoteLocations.count() < 2) { "There should only ever be one pawn to promote at any time" }

        val location = pawnsToPromoteLocations.first()

        if (board.getPiece(location = location) == null) {
            return board
        }

        // Get the ratings
        var highestRating = Double.MIN_VALUE
        var promotedBoard: Board? = null

        for (pieceType in Piece.PieceType.possiblePawnPromotionResultingTypes()) {

            val newBoard = board
            if (newBoard.getPiece(location) == null) {
                return board
            }

            val newPiece = newBoard.getPiece(location)?.byChangingType(pieceType)
            if (newPiece != null) {
                newBoard.setPiece(newPiece, location)
            }

            val rating = ratingForBoard(newBoard)

            if (rating > highestRating) {
                highestRating = rating
                promotedBoard = newBoard
            }

        }

        return promotedBoard!!
    }

//    fun makeMove() {
//
//        val board = game.board
//
//        // Build list of possible moves with ratings
//
//        var possibleMoves = ArrayList<Move>()
//
//        var boardLocations = BoardLocation.all()
//        for (sourceLocation in boardLocations) {
//
//            val piece = board.getPiece(sourceLocation) ?: continue
//
//            if (piece.color != color) {
//                continue
//            }
//
//            for (targetLocation in boardLocations) {
//
//                if (!canMovePiece(sourceLocation, targetLocation)) {
//                    continue
//                }
//
//                var resultBoard = board
//                resultBoard.movePiece(sourceLocation, targetLocation)
//                val rating = ratingForBoard(resultBoard)
//                val move = Move(sourceLocation, targetLocation, rating)
//                possibleMoves.add(move)
//            }
//            println("finished here")
//        }
//
//        print("Found ${possibleMoves.count()} possible moves")
//
//        // If there are no possible moves
//        if (possibleMoves.count() == 0) {
//            print("There are no possible moves!!!!");
//        }
//
//        // Choose move with highest rating
//        var highestRating = possibleMoves.first().rating
//        var highestRatedMove = possibleMoves.first()
//
//        for (move in possibleMoves) {
//
//            if (move.rating > highestRating) {
//                highestRating = move.rating
//                highestRatedMove = move
//            }
//
//            print("rating: ${move.rating}")
//        }
//
//        print("HIGHEST MOVE RATING: $highestRating")
//
//        // Make move
//        val operations = game.board.movePiece(highestRatedMove.sourceLocation, highestRatedMove.targetLocation)
//
//        this.game.playerDidMakeMove(this, operations)
//
//    }

}

