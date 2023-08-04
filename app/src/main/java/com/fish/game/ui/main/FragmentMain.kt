package com.fish.game.ui.main

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fish.game.R
import com.fish.game.databinding.FragmentMainBinding
import com.fish.game.ui.other.ViewBindingFragment

class FragmentMain : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonPlay.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentGame)
        }
        binding.buttonExit.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_dialogExit)
        }
        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}