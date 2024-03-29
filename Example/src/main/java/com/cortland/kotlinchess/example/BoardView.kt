package com.cortland.kotlinchess.example

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cortland.kotlinchess.*
import com.cortland.kotlinchess.AI.AIPlayer
import com.cortland.kotlinchess.Interfaces.GameListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

interface BoardViewListener {
    fun onLocationTouched(boardView: BoardView, location: BoardLocation)
}
class BoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs), GameListener {

    var boardViewListener: BoardViewListener? = null
    var game: Game

    var mPieceViews = ArrayList<PieceView>()
    var mBoardLocations = ArrayList<BoardLocation>()
    var selectedIndex: Int? = null
        set(value) {
            field = value
            updatePieceViewSelectedStates()
        }

    var x0 = 0
    var y0 = 0
    var squareSize = 0

    // Helper to know where to set location for new piece location
    // Figure a way to get rid of this
    var lastTappedRect: Rect? = null

    /** 'true' if black is facing player.  */
    var flipped = false

    init {
        val whitePlayer = Human(Color.white)
        //val blackPlayer = AIPlayer(Color.black, configuration = AIConfiguration(AIConfiguration.Difficulty.hard))
        val blackPlayer = Human(Color.black)
        game = Game(whitePlayer, blackPlayer)
        game.gameListener = this
        isFocusable = true
        buildBoardLocations()
    }

    fun buildBoardLocations() {

        for (location in BoardLocation.all()) {
            mBoardLocations.add(location)

            val piece = game.board.getPiece(location) ?: continue

            addPiece(location.x, location.y, piece)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        drawBoard(canvas)

        //drawPieces(canvas)

    }

    fun boardIndexForLocation(event: MotionEvent): Int {
        var location = event
        var new_y = this.height - location.y

        var gridx = (8.0 * location.x / this.width).toInt()
        var gridy = (8.0 * new_y / this.height).toInt()

        gridx = min(7, gridx)
        gridy = min(7, gridy)

        return gridx + (gridy * 8)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            //val location = BoardLocation(boardIndexForLocation(event))
            //boardViewListener?.onLocationTouched(this, location)

            var boardIndex = boardIndexForLocation(event)
            println("Board Index: $boardIndex")
            tappedPiece(boardIndex)
        }

        return super.onTouchEvent(event)
    }

    fun tappedPiece(index: Int) {

        val player = (this.game.currentPlayer as? Human) ?: return

        val location = BoardLocation(index)

        if (this.selectedIndex != null) {
            if (location == BoardLocation(selectedIndex!!)) {
                this.selectedIndex = null
                return
            }
        }

        if (player.occupiesSquareAt(location)) {
            this.selectedIndex = index
        }

        try {
            player.movePiece(fromLocation = BoardLocation(selectedIndex!!), toLocation = location)
        } catch (e: Player.PieceMoveErrorException) {
            println(e.message)
        } catch (e: Exception) {
            println("something went wrong")
        }

    }

    // MARK: - Private

    fun drawBoard(canvas: Canvas?) {

        val width = getWidth()
        val height = getHeight()

        this.squareSize = Math.min(getSquareSizeWidth(width), getSquareSizeHeight(height))

        computeOrigins(width, height)

        for (i in 0..64-1) {
            val c = i % 8
            val r = i / 8
            val index = c + (r * 8)

            val color = if ((c + r) % 2 == 0) android.graphics.Color.LTGRAY else android.graphics.Color.BLACK

            val fill = Paint()
            fill.color = color

            val xCoord = getXCoord(c)
            val yCoord = getYCoord(r)
            val tileRect = Rect(
                xCoord,  // left
                yCoord,  // top
                xCoord + squareSize,  // right
                yCoord + squareSize // bottom
            )

            // Rect
            //val squareSize = Size(getBounds(this)!!.width()/8, getBounds(this)!!.height()/8)
            //val rect = Rect((gridX) * squareSize.width,(gridY) * squareSize.height, squareSize.width, squareSize.height)

            canvas!!.drawRect(tileRect, fill)

            try {
                val pieceView = mPieceViews.first { it.location.index == index }
                drawSpecificPiece(pieceView, tileRect, canvas)
//                if (!pieceView.hasBeenUpdated) {
//                }
            } catch (e: Exception) {
                // Prints collection error
                println("Error: $e")
            }

        }

    }

    fun drawSpecificPiece(pieceView: PieceView, rect: Rect, canvas: Canvas?) {
        pieceView.clipBounds = rect
        pieceView.draw(canvas)
    }

    fun drawPieces(canvas: Canvas?) {

        for (pieceView in mPieceViews) {

//            val rect = pieceView.location.getBoardLocationRect()
//            pieceView.clipBounds = Rect(rect.left, rect.top, rect.right, rect.bottom)
            pieceView.draw(canvas)

        }

    }

    fun getAlgebraicPosition(index: Int): String {
        val gridX = index % 8
        val gridY = index / 8
        val boardAlpha = ('a'..'h').toList()

        return "${boardAlpha[gridX]}${gridY + 1}"
    }

    fun addPiece(x: Int, y: Int, piece: Piece) {
        val location = BoardLocation(x, y)
        val pieceView = PieceView(
            piece,
            location,
            this.context
        )
        mPieceViews.add(pieceView)
    }

    fun removePiece(tag: Int) {
        pieceViewWithTag(tag)?.also {
            removePieceView(it)
        }
    }

    fun removePieceView(pieceView: PieceView) {

        mPieceViews.indexOf(pieceView).also { index ->
            mPieceViews.removeAt(index)
        }

        invalidate()

    }

    fun pieceViewWithTag(tag: Int): PieceView? {

        for (pieceView in mPieceViews) {
            if (pieceView.piece.tag == tag) {
                return pieceView
            }
        }

        return null
    }

    fun updatePieceViewSelectedStates() {

        for (pieceView in mPieceViews) {
            pieceView.pieceSelected = (pieceView.location.index == selectedIndex)
        }
        invalidate()
    }

    fun getSquareSizeWidth(width: Int): Int {
        return width / 8
    }

    fun getSquareSizeHeight(height: Int): Int {
        return height / 8
    }

    fun getXCoord(x: Int): Int {
        return x0 + squareSize * if (flipped) 7 - x else x
    }

    fun getYCoord(y: Int): Int {
        return y0 + squareSize * if (flipped) y else 7 - y
    }

    fun computeOrigins(width: Int, height: Int) {
        this.x0 = (width - squareSize * 8) / 2
        this.y0 = (height - squareSize * 8) / 2
    }

    fun tellAIToTakeGo() {
        println("AI MAKING MOVE")
        (game.currentPlayer as? AIPlayer)?.also { player -> player.makeMoveAsync() }
    }

    // GAME Listener

    override fun gameDidChangeCurrentPlayer(game: Game) {
        println(javaClass.enclosingMethod?.name)
        this.selectedIndex = null

        println("game changed player")

        (game.currentPlayer as? AIPlayer)?.let {
            Timer().schedule(object: TimerTask() {
                override fun run() {
                    tellAIToTakeGo()
                }
            }, 2000)
        }
    }

    override fun gameWonByPlayer(game: Game, player: Player) {

    }

    override fun gameEndedInStaleMate(game: Game) {

    }

    override fun gameWillBeginUpdates(game: Game) {

    }

    override fun gameDidAddPiece(game: Game) {

    }

    override fun gameDidRemovePiece(game: Game, piece: Piece, location: BoardLocation) {
        val pieceView = pieceViewWithTag(piece.tag) ?: return

        //pieceView.mPieceImage!!.alpha = 0
        this.removePiece(piece.tag)

        println("pieceviews size after remove ${mPieceViews.count()}")
    }

    override fun gameDidMovePiece(game: Game, piece: Piece, toLocation: BoardLocation) {
        val pieceView = pieceViewWithTag(piece.tag) ?: return

        pieceView.location = toLocation
        lastTappedRect?.let { pieceView.clipBounds = it }
        pieceView.hasBeenUpdated = true
        lastTappedRect = null

        invalidate()
    }

    override fun gameDidTransformPiece(game: Game, piece: Piece, toLocation: BoardLocation) {
        println(javaClass.enclosingMethod?.name)
        val pieceView = pieceViewWithTag(piece.tag) ?: return

        pieceView.piece = piece

    }

    override fun gameDidEndUpdates(game: Game) {
        println(javaClass.enclosingMethod?.name)
    }

    override fun promotedTypeForPawn(
        location: BoardLocation,
        player: Human,
        possiblePromotions: ArrayList<Piece.PieceType>,
        callback: (Piece.PieceType) -> Unit
    ) {

        println(javaClass.enclosingMethod?.name)

    }

}

