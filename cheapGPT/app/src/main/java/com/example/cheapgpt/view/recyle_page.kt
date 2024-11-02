package com.example.cheapgpt.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cheapgpt.model.Post
import com.example.cheapgpt.R
import com.example.cheapgpt.adapter.PostAdapter
import com.example.cheapgpt.databinding.FragmentRecylePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class recyle_page : Fragment(),PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentRecylePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup:PopupMenu
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    val postList:ArrayList<Post> = arrayListOf()
    private var adapter:PostAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db=Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecylePageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener{floatinButtonSelect(it)}

        popup=PopupMenu(requireContext(),binding.floatingActionButton)
        val inflater=popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)



        getDatabase()

        adapter= PostAdapter(postList)
        binding.recylerr.layoutManager=LinearLayoutManager(requireContext())
        binding.recylerr.adapter=adapter


    }

    fun floatinButtonSelect(view: View){

        popup.show()

    }



    private fun getDatabase(){
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error !=null){

                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()

            }else{
                if (value !=null){
                    if (!value.isEmpty){
                        postList.clear()

                        val documents=value.documents
                        for (document in documents){
                            val comment=document.get("comment") as String
                            val email=document.get("email") as String
                            val downloadUrl=document.get("downloadUrl") as String

                            val post= Post(comment,email,downloadUrl)
                            postList.add(post)

                        }
                        adapter?.notifyDataSetChanged()
                    }

                }


            }
        }

    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.uploadOptions) {

            val action=recyle_pageDirections.actionRecylePageToUploadFragment()
           Navigation.findNavController(requireView()).navigate(action)

        }else if(item?.itemId == R.id.logoutOptions){
            auth.signOut()
            val action=recyle_pageDirections.actionRecylePageToGiris()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return false
    }



}