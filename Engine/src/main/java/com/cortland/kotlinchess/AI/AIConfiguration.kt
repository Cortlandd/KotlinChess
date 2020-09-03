package com.cortland.kotlinchess.AI

class AIConfiguration {

    public enum class Difficulty {
        easy,
        medium,
        hard
    }

    data class ConfigurationValue(val easyValue: Double, val difficultValue: Double, val multiplier: Double) {

        var value: Double = 0.toDouble()
            get() {
                return easyValue + ((difficultValue - easyValue) * multiplier)
            }
    }

    val difficulty: Difficulty
    internal var suicideMultipler: ConfigurationValue
    internal var boardRaterCountPiecesWeighting: ConfigurationValue
    internal var boardRaterBoardDominanceWeighting: ConfigurationValue
    internal var boardRaterCenterOwnershipWeighting: ConfigurationValue
    internal var boardRaterCenterDominanceWeighting: ConfigurationValue
    internal var boardRaterThreatenedPiecesWeighting: ConfigurationValue
    internal var boardRaterPawnProgressionWeighting: ConfigurationValue
    internal var boardRaterKingSurroundingPossessionWeighting: ConfigurationValue
    internal var boardRaterCheckMateOpportunityWeighting: ConfigurationValue
    internal var boardRaterCenterFourOccupationWeighting: ConfigurationValue

    constructor(difficulty: Difficulty) {

        this.difficulty = difficulty

        val multiplier: Double

        when(difficulty) {
            Difficulty.easy -> multiplier = 0.toDouble()
            Difficulty.medium -> multiplier = 0.5.toDouble()
            Difficulty.hard -> multiplier = 1.toDouble()
        }

        fun makeValue(easyValue: Double, hardValue: Double): ConfigurationValue {
            return ConfigurationValue(easyValue, hardValue, multiplier)
        }

        suicideMultipler = makeValue(0.toDouble(), 0.toDouble())
        boardRaterCountPiecesWeighting = makeValue(3.toDouble(), 3.toDouble())
        boardRaterBoardDominanceWeighting = makeValue(0.toDouble(), 0.1.toDouble())
        boardRaterCenterOwnershipWeighting = makeValue(0.1.toDouble(), 0.3.toDouble())
        boardRaterCenterDominanceWeighting = makeValue(0.toDouble(), 0.3.toDouble())
        boardRaterThreatenedPiecesWeighting = makeValue(0.toDouble(), 1.5.toDouble())
        boardRaterPawnProgressionWeighting = makeValue(0.1.toDouble(), 1.toDouble())
        boardRaterKingSurroundingPossessionWeighting = makeValue(0.toDouble(), 0.3.toDouble())
        boardRaterCheckMateOpportunityWeighting = makeValue(0.toDouble(), 2.toDouble())
        boardRaterCenterFourOccupationWeighting = makeValue(0.1.toDouble(), 0.3.toDouble())

    }

    internal constructor() {
        val zeroValue = ConfigurationValue(0.toDouble(), 0.toDouble(), 0.toDouble())

        difficulty = Difficulty.easy
        suicideMultipler = zeroValue
        boardRaterCountPiecesWeighting = zeroValue
        boardRaterBoardDominanceWeighting = zeroValue
        boardRaterCenterOwnershipWeighting = zeroValue
        boardRaterCenterDominanceWeighting = zeroValue
        boardRaterThreatenedPiecesWeighting = zeroValue
        boardRaterPawnProgressionWeighting = zeroValue
        boardRaterKingSurroundingPossessionWeighting = zeroValue
        boardRaterCheckMateOpportunityWeighting = zeroValue
        boardRaterCenterFourOccupationWeighting = zeroValue
    }

}

