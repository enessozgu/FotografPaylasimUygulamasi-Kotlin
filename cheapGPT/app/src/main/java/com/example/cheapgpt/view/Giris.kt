package com.example.cheapgpt.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.cheapgpt.R
import com.example.cheapgpt.databinding.FragmentGirisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Giris : Fragment() {

    private var _binding: FragmentGirisBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGirisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view


    }


    fun girisYap(view: View){
        val email=binding.EmailAddress.text.toString()
        val password=binding.editTextTextPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val action=GirisDirections.actionGirisToRecylePage()
                Navigation.findNavController(requireView()).navigate(action)
            }.addOnFailureListener { exception->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }


    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.giris2.setOnClickListener{girisYap(it)}

        binding.kayitol.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_giris_to_kayit)
        }

        val guncelKullanici = auth.currentUser
        if (guncelKullanici !=null){
            val action=GirisDirections.actionGirisToRecylePage()
            Navigation.findNavController(requireView()).navigate(action)
        }

    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }








}