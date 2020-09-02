package com.cortland.kotlinchess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cortland.kotlinchess.AI.AIPlayer
import com.cortland.kotlinchess.Interfaces.BoardViewListener
import com.cortland.kotlinchess.Interfaces.GameListener
import java.util.*
import kotlin.collections.ArrayList

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
    var lastTappedRect: Rect? = null

    /** 'true' if black is facing player.  */
    var flipped = false

    init {
        val whitePlayer = Human(Color.white)
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

        drawPieces(canvas)

        println("pieceviews size after ondraw ${mPieceViews.count()}")

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {

            val x = event.x.toInt()
            val y = event.y.toInt()

            val location = mBoardLocations.find {
                it.getBoardLocationRect().contains(x, y)
            }

            lastTappedRect = location?.getBoardLocationRect()

            location?.index?.let { tappedPiece(it) }

        }

        return super.onTouchEvent(event)
    }

    fun tappedPiece(index: Int) {

        println("TAPPED ${getAlgebraicPosition(index)}")

        val player = (this.game.currentPlayer as Human) ?: return

        val location = BoardLocation(index)

        try {
            this.selectedIndex?.also { idx ->
                if (location == BoardLocation(idx)) {
                    this.selectedIndex = null
                    return
                }
            }
        } catch (e: Exception) {
            print(e)
        }

        if (this.game.currentPlayer.occupiesSquareAt(location)) {
            this.selectedIndex = index
        }

        try {
            player.movePiece(fromLocation = BoardLocation(selectedIndex!!), toLocation = location)
        } catch (e: Player.PieceMoveErrorException) {
            println(e.message)
        } catch (e: Exception) {
            print(e.message)
        }

    }

    // MARK: - Private

    fun drawBoard(canvas: Canvas?) {

        val width = getWidth()
        val height = getHeight()

        this.squareSize = Math.min(getSquareSizeWidth(width), getSquareSizeHeight(height))

        computeOrigins(width, height)

        for (i in 0..63) {
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

            mBoardLocations[i].setBoardLocationRect(tileRect)

            canvas!!.drawRect(tileRect, fill)

            try {
                val pieceView = mPieceViews.first { it.location.index == i }
                if (!pieceView.hasBeenUpdated) {
                    pieceView.location.setBoardLocationRect(tileRect)
                }
            } catch (e: Exception) {
                // Prints collection error
            }

        }

    }

    fun drawPieces(canvas: Canvas?) {

        for (pieceView in mPieceViews) {

            val rect = pieceView.location.getBoardLocationRect()
            pieceView.clipBounds = Rect(rect.left, rect.top, rect.right, rect.bottom)
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

        val pieceView = PieceView(piece, location, this.context)
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

    fun boardIndexForLocation(xcoord: Int, ycoord: Int): Int {

        val x = xcoord
        var y = ycoord

        // Flip y (0 at bottom)
        y = height - y

        // Get Grid coordinates
        var gridX = (8.0 * x / width).toInt()
        var gridY = (8.0 * y / height).toInt()
        gridX = Math.min(7, gridX)
        gridY = Math.min(7, gridY)

        // Make board index
        return gridX + (gridY*8)
    }

    fun tellAIToTakeGo() {
        println("AI MAKING MOVE")
        (game.currentPlayer as? AIPlayer)?.also { player -> player.makeMove() }
    }

    // GAME Listener

    override fun gameDidChangeCurrentPlayer(game: Game) {

        this.selectedIndex = null

        println("game changed player")

        (game.currentPlayer as? AIPlayer)?.let {
            Timer().schedule(object: TimerTask() {
                override fun run() {
                    tellAIToTakeGo()
                }
            }, 3000)
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
        lastTappedRect?.let { pieceView.location.setBoardLocationRect(it) }
        pieceView.hasBeenUpdated = true
        lastTappedRect = null

        invalidate()
    }

    override fun gameDidTransformPiece(game: Game) {

    }

    override fun gameDidEndUpdates(game: Game) {

    }

}

