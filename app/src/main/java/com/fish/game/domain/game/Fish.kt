package com.fish.game.domain.game

data class Fish(
    val value: Int,
    var position: Pair<Float, Float>,
    var isMovingLeft: Boolean
)