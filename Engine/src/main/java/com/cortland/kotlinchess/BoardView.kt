package com.cortland.kotlinchess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class BoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var boardViewListener: BoardViewListener? = null

    private val COLS = 8
    private val ROWS = 8

    private var mBoardLocations: Array<Array<BoardLocation>>

    var x0 = 0
    var y0 = 0
    var squareSize = 0

    /** 'true' if black is facing player.  */
    var flipped = false

    init {
        this.mBoardLocations = Array(COLS, {
            Array(ROWS, {
                BoardLocation(0, 0)
            })
        })

        setFocusable(true)
        buildTiles()
    }

    fun buildTiles() {
        for (c in 0 until COLS) {
            for (r in 0 until ROWS) {
                mBoardLocations[c][r] =
                    BoardLocation(c, r)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val width = getWidth()
        val height = getHeight()

        this.squareSize = Math.min(getSquareSizeWidth(width), getSquareSizeHeight(height))

        computeOrigins(width, height)

        for (c in 0 until COLS) {
            for (r in 0 until ROWS) {
                val xCoord = getXCoord(c)
                val yCoord = getYCoord(r)
                val tileRect = Rect(
                    xCoord,  // left
                    yCoord,  // top
                    xCoord + squareSize,  // right
                    yCoord + squareSize // bottom
                )
                mBoardLocations[c][r].setBoardLocationRect(tileRect)
                mBoardLocations[c][r].draw(canvas!!)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var boardLocation: BoardLocation
        for (c in 0 until COLS) {
            for (r in 0 until ROWS) {
                boardLocation = mBoardLocations[c][r]
                if (boardLocation.isTouched(x, y)) {
                    println("index: ${boardLocation.index}")
                    boardViewListener?.onLocationTouched(this, boardLocation)
                }
            }
        }

        return true
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

}

