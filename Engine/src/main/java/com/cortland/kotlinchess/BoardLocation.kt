package com.cortland.kotlinchess

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class BoardLocation {

    enum class GridPosition {
        a1, b1, c1, d1, e1, f1, g1, h1,
        a2, b2, c2, d2, e2, f2, g2, h2,
        a3, b3, c3, d3, e3, f3, g3, h3,
        a4, b4, c4, d4, e4, f4, g4, h4,
        a5, b5, c5, d5, e5, f5, g5, h5,
        a6, b6, c6, d6, e6, f6, g6, h6,
        a7, b7, c7, d7, e7, f7, g7, h7,
        a8, b8, c8, d8, e8, f8, g8, h8
    }

    var index: Int
        set(value) {
            field = value
            x = value % 8
            y = value / 8
        }

    var x: Int = 0
        get() = index % 8

    var y: Int = 0
        get() = index / 8

    var squareColor = Paint()
    private var boardLocationRect: Rect = Rect()

    companion object {
        private val TAG = BoardLocation::class.java.simpleName

        private var allLocationsBacking: ArrayList<BoardLocation>? = null

        fun all(): ArrayList<BoardLocation> {
            return if (allLocationsBacking != null) {
                allLocationsBacking!!
            } else {
                val locations = ArrayList<BoardLocation>()
                (0..63).forEach {
                    locations.add(BoardLocation(index = it))
                }

                allLocationsBacking = locations
                allLocationsBacking!!
            }
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(boardLocationRect, squareColor)
    }

    val columnString: String?
        get() = when (x) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            4 -> "E"
            5 -> "F"
            6 -> "G"
            7 -> "H"
            else -> null
        }

    // To get the actual row, add 1 since 'row' is 0 indexed.
    val rowString: String
        get() =// To get the actual row, add 1 since 'row' is 0 indexed.
            (y + 1).toString()

    val isDark: Boolean
        get() = (index + y) % 2 == 0

    fun isTouched(x: Int, y: Int): Boolean {
        return boardLocationRect.contains(x, y)
    }

    fun setBoardLocationRect(boardLocationRect: Rect) {
        this.boardLocationRect = boardLocationRect
    }

    fun getBoardLocationRect(): Rect {
        return this.boardLocationRect
    }

    fun isInBounds(): Boolean {
        return (index < 64 && index >= 0)
    }

    override fun toString(): String {
        val column = columnString
        val row = rowString
        return "<BoardLocation $column$row>"
    }

    constructor(index: Int) {
        this.index = index

        setPaint(x, y)
    }

    constructor(x: Int, y: Int) {
        this.index = x + (y * 8)

        setPaint(x, y)
    }

    constructor(gridPosition: GridPosition) {
        this.index = gridPosition.ordinal

        setPaint(x, y)
    }

    // TODO: Do this better
    private fun setPaint(col: Int, row: Int) {
        squareColor.setColor(if ((col + row) % 2 == 0) Color.BLACK else Color.WHITE)
    }
}