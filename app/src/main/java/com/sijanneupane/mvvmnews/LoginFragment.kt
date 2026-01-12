package com.sijanneupane.mvvmnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sijanneupane.mvvmnews.R
import com.sijanneupane.mvvmnews.ui.MainActivity // Nhớ import Activity này
import com.sijanneupane.mvvmnews.ui.NewsViewModel
import com.sijanneupane.mvvmnews.utils.Resource // Kiểm tra lại package util hay utils trong dự án của bạn

class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- SỬA LỖI 1: KHAI BÁO VIEW (findViewById) ---
        // Phải tìm các view này từ giao diện XML trước khi dùng
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val etLoginUsername = view.findViewById<EditText>(R.id.etLoginUsername)
        val etLoginPassword = view.findViewById<EditText>(R.id.etLoginPassword)
        val tvGoToRegister = view.findViewById<TextView>(R.id.tvGoToRegister)


        // --- SỬA LỖI 2: ÉP KIỂU SAI ---
        // Bạn phải ép kiểu activity thành NewsActivity, KHÔNG PHẢI NewsApplication
        viewModel = (activity as MainActivity).viewModel


        // 2. Xử lý nút Đăng nhập
        btnLogin.setOnClickListener {
            val username = etLoginUsername.text.toString()
            val password = etLoginPassword.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(username, password)
            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Xử lý nút chuyển sang Đăng ký
        tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // 4. Lắng nghe kết quả Đăng nhập
        viewModel.userLoginStatus.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    // Chuyển vào màn hình chính (Tin tức)
                    findNavController().navigate(R.id.action_loginFragment_to_breakingNewsFragment)
                }
                is Resource.Error -> {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Có thể hiện ProgressBar ở đây
                }
            }
        })
    }
}