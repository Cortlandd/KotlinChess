package com.cortland.kotlinchess

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

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

    /** 'true' if black is facing player.  */
    var flipped = false

    init {
        game = Game()
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

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {

            val x = event.x.toInt()
            val y = event.y.toInt()

            val location = mBoardLocations.find {
                it.getBoardLocationRect().contains(x, y)
            }

            location?.index?.let { tappedPiece(it) }

        }

        return super.onTouchEvent(event)
    }

    fun tappedPiece(index: Int) {

        println("TAPPED ${getAlgebraicPosition(index)}")

        val location = BoardLocation(index)

        try {
            this.selectedIndex.let { idx ->
                if (location == BoardLocation(idx!!)) {
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

        this.selectedIndex.let { selectedIndex ->
            try {
                this.game.currentPlayer.movePiece(BoardLocation(selectedIndex!!), location)
                println("MOVE COMPLETED")
            } catch (e: Player.PieceMoveErrorException) {
                println(e.message)
            } catch (e: Exception) {
                println("something went wrong")
            }
        }

        println("SelectedIndex: ${this.selectedIndex}")
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
                pieceView.location.setBoardLocationRect(tileRect)
            } catch (e: Exception) {

            }

        }

    }

    fun drawPieces(canvas: Canvas?) {

        for (pieceView in mPieceViews) {

            val image = pieceView.mPieceImage!!

            val rect = pieceView.location.getBoardLocationRect()
            image.bounds = Rect(rect.left, rect.top, rect.right, rect.bottom)

            image.draw(canvas!!)

            invalidateDrawable(image)

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

    override fun gameDidChangeCurrentPlayer(game: Game) {
        this.selectedIndex = null
    }

}

