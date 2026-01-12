package com.sijanneupane.mvvmnews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sijanneupane.mvvmnews.databinding.ItemArticlePreviewBinding
import com.sijanneupane.mvvmnews.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return (oldItem.url ?: "") == (newItem.url ?: "")
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        val binding = holder.binding

        Glide.with(binding.root)
            .load(article.image ?: article.urlToImage)   // ✅ ăn cả GNews + NewsAPI
            .into(binding.ivArticleImage)

        binding.tvSource.text = article.source?.name ?: ""
        binding.tvTitle.text = article.title ?: ""
        binding.tvDescription.text = article.description ?: ""
        binding.tvPublishedAt.text = article.publishedAt ?: ""

        binding.root.setOnClickListener {
            onItemClickListener?.invoke(article)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
