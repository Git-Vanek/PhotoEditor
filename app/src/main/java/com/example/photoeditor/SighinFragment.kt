package com.example.photoeditor

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photoeditor.databinding.FragmentSighinBinding

class SighinFragment : Fragment() {
    lateinit var _binding: FragmentSighinBinding
    val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSighinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignIn.setOnClickListener {
            signIn()
        }
        binding.buttonSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun signIn() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.card, SignupFragment())
            .addToBackStack(null)
            .commit()
    }
}