package com.begoml.uistatedelegate.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.begoml.uistatedelegate.AppApplication
import com.begoml.uistatedelegate.R
import com.begoml.uistatedelegate.databinding.FragmentForgotPasswordBinding
import com.begoml.uistatedelegate.forgotpassword.ForgotPasswordViewModel.UiState
import com.begoml.uistatedelegate.uistate.collectEvent
import com.begoml.uistatedelegate.uistate.render
import com.begoml.uistatedelegate.uistate.uiStateDiffRender

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private var _binding: FragmentForgotPasswordBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by viewModels {
        ForgotPasswordViewModelFactory(
            authRepository = (requireContext().applicationContext as AppApplication).appProvider.provideAuthRepository()
        )
    }


    private val render = uiStateDiffRender {
        UiState::isLoading { isLoading ->
            with(binding) {
                progress.isVisible = isLoading
                button.isEnabled = isLoading.not()
                loginInputFiled.isEnabled = isLoading.not()
            }
        }

        UiState::title { title ->
            binding.title.text = title
        }

        UiState::login { login ->
            binding.loginInputFiled.apply {
                setText(login)
                setSelection(login.length)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgotPasswordBinding.bind(view)

        with(binding) {
            button.setOnClickListener { viewModel.onForgotPasswordClick() }
            loginInputFiled.doAfterTextChanged(viewModel::onLoginTextChanged)
        }

        with(viewModel) {
            render(
                lifecycleOwner = viewLifecycleOwner,
                render = render
            )
            collectEvent(lifecycle) { event ->
                return@collectEvent when (event) {
                    ForgotPasswordViewModel.Event.FinishFlow -> requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
