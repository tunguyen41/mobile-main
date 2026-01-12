package com.sijanneupane.mvvmnews.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sijanneupane.mvvmnews.R
import com.sijanneupane.mvvmnews.databinding.FragmentArticleBinding
import com.sijanneupane.mvvmnews.ui.MainActivity
import com.sijanneupane.mvvmnews.ui.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleBinding.bind(view)

        viewModel = (requireActivity() as MainActivity).viewModel
        val article = args.article

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(binding.root, "Article saved!", Snackbar.LENGTH_SHORT).show()
        }
        binding.fab.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url.orEmpty()))
            startActivity(intent)
            true
        }

        if (!hasInternetConnection(requireContext())) {
            showOfflineContent(article)
            return
        }

        showOnlineContent(article)
    }

    private fun showOfflineContent(article: com.sijanneupane.mvvmnews.models.Article) {
        binding.webView.visibility = View.GONE
        binding.offlineContainer.visibility = View.VISIBLE

        Glide.with(binding.root)
            .load(article.image ?: article.urlToImage)
            .into(binding.ivOfflineImage)

        binding.tvOfflineTitle.text = article.title ?: "(No title)"
        val sourceName = article.source?.name ?: ""
        val time = article.publishedAt ?: ""
        binding.tvOfflineMeta.text = listOf(sourceName, time).filter { it.isNotBlank() }.joinToString(" • ")

        val offlineText = when {
            !article.content.isNullOrBlank() -> article.content
            !article.description.isNullOrBlank() -> article.description
            else -> "Bạn đang offline. Bài này chỉ lưu tiêu đề/mô tả."
        }
        binding.tvOfflineContent.text = offlineText

        Snackbar.make(binding.root, "Offline: hiển thị nội dung đã lưu", Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showOnlineContent(article: com.sijanneupane.mvvmnews.models.Article) {
        binding.offlineContainer.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE

        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {}
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    Snackbar.make(binding.root, "Web load error: ${error?.description}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        val url = article.url
        if (!url.isNullOrBlank()) {
            binding.webView.loadUrl(url)
        } else {
            Snackbar.make(binding.root, "Invalid article URL", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun hasInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onPause() {
        super.onPause()
        _binding?.webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        _binding?.webView?.onResume()
    }

    override fun onDestroyView() {
        _binding?.webView?.apply {
            stopLoading()
            loadUrl("about:blank")
            clearHistory()
            destroy()
        }
        super.onDestroyView()
        _binding = null
    }
}
