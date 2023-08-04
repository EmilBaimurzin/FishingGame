package com.fish.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fish.game.R
import com.fish.game.core.library.ViewBindingDialog
import com.fish.game.databinding.DialogExitBinding

class DialogExit: ViewBindingDialog<DialogExitBinding>(DialogExitBinding::inflate) {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.buttonYes.setOnClickListener {
            requireActivity().finish()
        }
        binding.buttonNo.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
    }
}