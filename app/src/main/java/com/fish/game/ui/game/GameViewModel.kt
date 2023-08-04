package com.fish.game.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fish.game.core.library.random
import com.fish.game.domain.game.Fish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private var gameScope = CoroutineScope(Dispatchers.Default)
    var gameState = true

    private val _rodPosition = MutableLiveData(0f to 0f)
    val rodPosition: LiveData<Pair<Float, Float>> = _rodPosition

    private val _fishList = MutableLiveData<List<Fish>>(emptyList())
    val fishList: LiveData<List<Fish>> = _fishList

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    private val _boom = MutableLiveData(false)
    val boom: LiveData<Boolean> = _boom

    fun start(y: Float, x: Float, fishSize: Int) {
        gameScope = CoroutineScope(Dispatchers.Default)
        generateFish(y, x, fishSize)
        letFishMove(x)
    }

    fun stop() {
        gameScope.cancel()
    }

    private fun generateFish(y: Float, x: Float, fishSize: Int) {
        gameScope.launch {
            while (true) {
                delay(1000)
                val currentList = _fishList.value!!.toMutableList()
                val isMovingLeft = Random.nextBoolean()
                val randomY =
                    ((y - y / 2 + fishSize).toInt()..(y - fishSize).toInt()).random().toFloat()
                val randomLife = (1 random 50) == 8
                currentList.add(
                    Fish(
                        if (randomLife) 8 else 1 random 7,
                        (if (isMovingLeft) x - fishSize else 0f) to randomY,
                        isMovingLeft
                    )
                )
                _fishList.postValue(currentList)
            }
        }
    }

    private fun letFishMove(x: Float) {
        gameScope.launch {
            while (true) {
                delay(16)
                val currentList = _fishList.value!!.toMutableList()
                val newList = mutableListOf<Fish>()
                currentList.forEach { fish ->
                    if (!(fish.position.first < 0 && fish.isMovingLeft) || !(fish.position.first > x && !fish.isMovingLeft)) {
                        if (fish.isMovingLeft) {
                            fish.position = fish.position.first - 5 to fish.position.second
                        } else {
                            fish.position = fish.position.first + 5 to fish.position.second
                        }
                        newList.add(fish)
                    }
                }
                _fishList.postValue(newList)
            }
        }
    }

    fun catch(fishSize: Int, hookSize: Int, rodHeight: Int, rodWidth: Int) {
        val currentList = _fishList.value!!.toMutableList()
        val newList = mutableListOf<Fish>()
        currentList.forEach { fish ->
            val fishX = (fish.position.first).toInt()..(fish.position.first + fishSize).toInt()
            val fishY = (fish.position.second).toInt()..(fish.position.second + fishSize).toInt()

            val hookX =
                (getHookPosition(hookSize, rodHeight, rodWidth).first).toInt()..(getHookPosition(
                    hookSize,
                    rodHeight,
                    rodWidth
                ).first + hookSize).toInt()
            val hookY =
                (getHookPosition(hookSize, rodHeight, rodWidth).second).toInt()..(getHookPosition(
                    hookSize,
                    rodHeight,
                    rodWidth
                ).second + hookSize).toInt()
            if (fishX.any { it in hookX } && fishY.any { it in hookY }) {
                checkFish(fish)
            } else {
                newList.add(fish)
            }
        }
        _fishList.postValue(newList)
    }

    private fun checkFish(fish: Fish) {
        when (fish.value) {
            6 -> {
                if (_lives.value!! - 1 >= 0) {
                    _lives.postValue(_lives.value!! - 1)
                    _scores.postValue(0)
                    viewModelScope.launch {
                        _boom.postValue(true)
                        delay(500)
                        _boom.postValue(false)
                    }
                }
            }

            in 1..2 -> {
                _scores.postValue(_scores.value!! - 10)
            }

            8 -> {
                if (_lives.value!! + 1 <= 3) {
                    _lives.postValue(_lives.value!! + 1)
                }
            }

            else -> {
                _scores.postValue(_scores.value!! + 5)
            }
        }
    }

    private fun getHookPosition(hookSize: Int, rodHeight: Int, rodWidth: Int): Pair<Float, Float> {
        val rodPosition = _rodPosition.value!!
        val hookX = rodPosition.first + rodWidth - hookSize
        val hookY = rodPosition.second + rodHeight - hookSize
        return hookX to hookY
    }

    fun playerMoveUp(limit: Float) {
        if (_rodPosition.value!!.second - 4f > limit) {
            _rodPosition.postValue(_rodPosition.value!!.first to _rodPosition.value!!.second - 4f)
        }
    }

    fun playerMoveDown(limit: Float) {
        if (_rodPosition.value!!.second + 4f < limit) {
            _rodPosition.postValue(_rodPosition.value!!.first to _rodPosition.value!!.second + 4f)
        }
    }

    fun playerMoveLeft(limit: Float) {
        if (_rodPosition.value!!.first - 4f > limit) {
            _rodPosition.postValue(_rodPosition.value!!.first - 4 to _rodPosition.value!!.second)
        }
    }

    fun playerMoveRight(limit: Float) {
        if (_rodPosition.value!!.first + 4f < limit) {
            _rodPosition.postValue(_rodPosition.value!!.first + 4 to _rodPosition.value!!.second)
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameScope.cancel()
    }
}