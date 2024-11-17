package com.example.photoeditor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    lateinit var _binding: FragmentSignupBinding
    val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }
        binding.buttonBack.setOnClickListener {
            back()
        }
    }

    private fun signUp() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun back() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.card, SighinFragment())
            .addToBackStack(null)
            .commit()
    }
}