package com.begoml.uistatedelegate.features.user

import androidx.lifecycle.ViewModel
import com.begoml.uistatedelegate.features.delegates.ToolbarDelegate

class UserViewModel(
    private val toolbarDelegate: ToolbarDelegate,
) : ToolbarDelegate by toolbarDelegate, ViewModel()