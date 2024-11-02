package com.example.cheapgpt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cheapgpt.databinding.RecylerRowBinding
import com.example.cheapgpt.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(private val postList:ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(val binding:RecylerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecylerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
       return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recylerEmailText.text=postList[position].email
        holder.binding.recylerCommentText.text=postList[position].comment
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recylerimageView)

    }
}