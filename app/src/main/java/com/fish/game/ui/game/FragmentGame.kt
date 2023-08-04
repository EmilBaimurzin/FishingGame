package com.fish.game.ui.game

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fish.game.R
import com.fish.game.databinding.FragmentGameBinding
import com.fish.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentGame : ViewBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private var moveScope = CoroutineScope(Dispatchers.Default)
    private val viewModel: GameViewModel by viewModels()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMovement()

        binding.restartButton.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentGame)
        }

        binding.buttonHome.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        viewModel.rodPosition.observe(viewLifecycleOwner) {
            binding.fishingRod.apply {
                x = it.first
                y = it.second
            }
        }

        binding.catchButton.setOnClickListener {
            viewModel.catch(
                dpToPx(50),
                dpToPx(20),
                binding.fishingRod.height,
                binding.fishingRod.width
            )
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val heartView = ImageView(requireContext())
                heartView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                heartView.setImageResource(R.drawable.life)
                binding.livesLayout.addView(heartView)
            }
            if (it == 0 && viewModel.gameState) {
                end()
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        viewModel.boom.observe(viewLifecycleOwner) {
            binding.boom.isVisible = it
        }

        viewModel.fishList.observe(viewLifecycleOwner) {
            binding.fishLayout.removeAllViews()
            it.forEach { fish ->
                val fishView = ImageView(requireContext())
                fishView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(50))
                if (fish.value == 8) {
                    fishView.scaleX = 0.4f
                    fishView.scaleY = 0.4f
                }
                val fishImg = when (fish.value) {
                    1 -> R.drawable.symbol01
                    2 -> R.drawable.symbol02
                    3 -> R.drawable.symbol03
                    4 -> R.drawable.symbol04
                    5 -> R.drawable.symbol05
                    6 -> R.drawable.bomb
                    8 -> R.drawable.life
                    else -> R.drawable.symbol06
                }
                fishView.rotationY = if (fish.isMovingLeft) 0f else 180f
                fishView.setImageResource(fishImg)
                fishView.x = fish.position.first
                fishView.y = fish.position.second
                binding.fishLayout.addView(fishView)
            }
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.gameState) {
                viewModel.start(xy.second.toFloat(), xy.first.toFloat(), dpToPx(50))
            }
        }
    }

    private fun end() {
        viewModel.stop()
        viewModel.gameState = false
        findNavController().navigate(R.id.action_fragmentGame_to_dialogEnd)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMovement() {
        binding.topButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveUp(0f)
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveUp(0f)
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.leftButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveLeft(0f - binding.fishingRod.width + dpToPx(20))
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveLeft(0f - binding.fishingRod.width + dpToPx(20))
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.rightButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveRight((xy.first - binding.fishingRod.width).toFloat())
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveRight(
                                (xy.first - binding.fishingRod.width).toFloat()
                            )
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.bottomButton.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveDown((xy.second - binding.fishingRod.height).toFloat())
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveDown((xy.second - binding.fishingRod.height).toFloat())
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
        moveScope.cancel()
    }
}