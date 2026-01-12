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
import com.sijanneupane.mvvmnews.db.User
import com.sijanneupane.mvvmnews.ui.MainActivity
import com.sijanneupane.mvvmnews.ui.NewsViewModel
import com.sijanneupane.mvvmnews.utils.Resource

class RegisterFragment : Fragment(R.layout.fragment_register) {

    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Khai báo các View (Sửa lỗi đỏ)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val etRegisterUsername = view.findViewById<EditText>(R.id.etRegisterUsername)
        val etRegisterPassword = view.findViewById<EditText>(R.id.etRegisterPassword)
        val tvGoToLogin = view.findViewById<TextView>(R.id.tvGoToLogin)

        // 2. Lấy ViewModel từ MainActivity (Sửa lỗi ép kiểu)
        viewModel = (activity as MainActivity).viewModel

        // 3. Xử lý nút Đăng ký
        btnRegister.setOnClickListener {
            val username = etRegisterUsername.text.toString()
            val password = etRegisterPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = User(username = username, password = password)
                viewModel.register(user)
            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Nút quay lại đăng nhập
        tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // 5. Lắng nghe kết quả Đăng ký
        viewModel.userRegisterStatus.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    Toast.makeText(context, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show()
                    // Quay lại màn hình đăng nhập
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Resource.Error -> {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Có thể hiển thị loading nếu muốn
                }
            }
        })
    }
}