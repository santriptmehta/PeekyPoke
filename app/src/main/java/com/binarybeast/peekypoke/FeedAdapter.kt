package com.binarybeast.peekypoke

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context): FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewHolder>(options) {

    class FeedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postImage : ImageView = itemView.findViewById(R.id.post_image)
        val postText : TextView = itemView.findViewById(R.id.post_text)
        val authorText : TextView = itemView.findViewById(R.id.post_author)
        val timeText : TextView = itemView.findViewById(R.id.post_time)
        val likeIcon : ImageView = itemView.findViewById(R.id.like_icon)
        val likeCount : TextView = itemView.findViewById(R.id.like_count)
        val commentIcon : ImageView = itemView.findViewById(R.id.comment_icon)
        val commentCount : TextView = itemView.findViewById(R.id.comment_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent,false)
        return FeedViewHolder(view)

    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, model: Post) {

        holder.postText.text = model.text
        holder.authorText.text = model.user.name

        Glide.with(context)
            .load(model.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.postImage)

        holder.likeCount.text = model.likeList.size.toString()


        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val postDocument = firestore.collection("Posts").document(
            snapshots.getSnapshot(position).id)

        postDocument.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val post = it.result?.toObject(Post::class.java)

                post?.likeList?.let { list ->
                    if (list.contains(userId)) {
                        holder.likeIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.like_icon_filled
                            )
                        )
                    } else {
                        holder.likeIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.like_icon_outline
                            )
                        )
                    }

                    fun invertLike() {
                        if (list.contains(userId)) {
                            list.remove(userId)
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.like_icon_outline
                                )
                            )
                        } else {
                            userId?.let { userId ->
                                list.add(userId)
                            }
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.like_icon_filled
                                ))
                        }
                    }
                    holder.likeIcon.setOnClickListener {
                        invertLike()

                        postDocument.set(post).addOnCompleteListener { updatePostTask ->
                            if (!updatePostTask.isSuccessful) {
                                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                                invertLike()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()

            }
        }


    }
}