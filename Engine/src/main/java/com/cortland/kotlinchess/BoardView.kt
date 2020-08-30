package com.cortland.kotlinchess

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class BoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var boardViewListener: BoardViewListener? = null

    var mPieceViews = ArrayList<PieceView>()
    var selectedIndex: Int? = null
        set(value) = updatePieceViewSelectedStates()

    var x0 = 0
    var y0 = 0
    var squareSize = 0

    /** 'true' if black is facing player.  */
    var flipped = false

    init {

        isFocusable = true
        buildBoardLocations()
    }

    fun buildBoardLocations() {

        val board = Board()
        for (location in BoardLocation.all()) {
            val piece = board.getPiece(location) ?: continue

            addPiece(location.x, location.y, piece)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        drawBoard(canvas)

        drawPieces(canvas)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var boardLocation: BoardLocation
        for (i in 0..63) {
            val c = i % 8
            val r = i / 8
            val index = c + (r * 8)

            try {
                boardLocation = mPieceViews.first { it.location.index == i }.location
                if (boardLocation.isTouched(x, y)) {
                    println("alg location ${getAlgebraicPosition(boardLocation.index)}")
                    boardViewListener?.onLocationTouched(this, boardLocation)
                }
            } catch (e: Exception) {
                println(e)
            }
        }

        return true
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

            val color =
                if ((c + r) % 2 == 0) android.graphics.Color.BLUE else android.graphics.Color.BLACK

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

            canvas!!.drawRect(tileRect, fill)

            try {
                val pieceView = mPieceViews.first { it.location.index == i }
                pieceView.location.setBoardLocationRect(tileRect)
            } catch (e: Exception) {
                println(e)
            }

        }

    }

    fun drawPieces(canvas: Canvas?) {

        for (pieceView in mPieceViews) {

            val gridX = pieceView.location.x
            val gridY = 7 - pieceView.location.y

            val width = canvas?.width?.div(8)
            val height = canvas?.height?.div(8)

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
            pieceView.selected = (pieceView.location.index == selectedIndex)
        }
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

    fun boardIndexForLocation(bounds: Rect, x: Int, y: Int): Int {

        var x = x
        var y = y

        // Flip y (0 at bottom)
        y = bounds.height() - y

        // Get Grid coordinates
        var gridX = (8.0 * x / bounds.width()).toInt()
        var gridY = (8.0 * y / bounds.height()).toInt()
        gridX = Math.min(7, gridX)
        gridY = Math.min(7, gridY)

        // Make board index
        return gridX + (gridY*8)
    }

}

