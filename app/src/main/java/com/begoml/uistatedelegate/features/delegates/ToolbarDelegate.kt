package com.begoml.uistatedelegate.features.delegates

import androidx.annotation.FloatRange
import com.begoml.core.UserRepository
import com.begoml.uistatedelegate.state.UiStateDelegate
import com.begoml.uistatedelegate.state.UiStateDelegateImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ToolbarDelegate {

    val toolbarUiState: StateFlow<ToolbarUiState>
}

data class ToolbarUiState(
    val title: String = "",

    @FloatRange(from = 0.0, to = 100.0)
    val levelProgress: Float = 0f,

    val levelLabel: String = "",
)

class ToolbarDelegateImpl @Inject constructor(
    coroutineScope: CoroutineScope,
    userRepository: UserRepository,
) : ToolbarDelegate,
    UiStateDelegate<ToolbarUiState, Unit> by UiStateDelegateImpl(ToolbarUiState()) {

    override val toolbarUiState: StateFlow<ToolbarUiState>
        get() = uiStateFlow

    init {
        coroutineScope.launch {
            delay(500L)
            updateUiState { state ->
                state.copy(
                    title = "Title",
                    levelLabel = "Level:",
                )
            }
        }

        userRepository.getUserLevelFlow()
            .flowOn(Dispatchers.IO)
            .onEach { levelProgress ->
                updateUiState { state -> state.copy(levelProgress = levelProgress) }
            }
            .catch { throwable -> throwable.printStackTrace() }
            .launchIn(coroutineScope)
    }

}