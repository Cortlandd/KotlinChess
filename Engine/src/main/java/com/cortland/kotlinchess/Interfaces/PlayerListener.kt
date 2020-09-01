package com.cortland.kotlinchess.Interfaces

import com.cortland.kotlinchess.Player

interface PlayerListener {
    fun playerDidMakeMove(player: Player)
}