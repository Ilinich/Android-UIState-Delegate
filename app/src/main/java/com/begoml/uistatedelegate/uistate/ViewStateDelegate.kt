package com.begoml.uistatedelegate.uistate

import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * ViewState - must be Data class, immutable
 */
interface ViewStateDelegate<UIState, Event> {

    /**
     * Declarative description of the UI based on the current state.
     */
    val uiState: Flow<UIState>

    val singleEvents: Flow<Event>

    /**
     * State is read-only
     * The only way to change the state is to emit[reduce] an action,
     * an object describing what happened.
     */
    val stateValue: UIState

    /**
     * Reduce are functions that take the current state and an action as arguments,
     * and changed a new state result. In other words, (state: ViewState) => newState.
     */
    @MainThread
    @Throws(IllegalStateException::class)
    suspend fun ViewStateDelegate<UIState, Event>.reduce(action: (state: UIState) -> UIState)

    @MainThread
    @Throws(IllegalStateException::class)
    fun CoroutineScope.asyncReduce(action: (state: UIState) -> UIState)

    suspend fun ViewStateDelegate<UIState, Event>.sendEvent(event: Event)
}

class ViewStateDelegateImpl<UIState, Event>(
    initialViewState: UIState,
    singleLiveEventCapacity: Int = Channel.BUFFERED,
) : ViewStateDelegate<UIState, Event> {

    /**
     * The source of truth that drives our app.
     */
    private val stateFlow = MutableStateFlow(initialViewState)

    override val uiState: Flow<UIState>
        get() = stateFlow.asStateFlow()

    override val stateValue: UIState
        get() = stateFlow.value

    private val singleEventsChannel = Channel<Event>(singleLiveEventCapacity)

    override val singleEvents: Flow<Event>
        get() = singleEventsChannel.receiveAsFlow()

    private val mutex = Mutex()

    override suspend fun ViewStateDelegate<UIState, Event>.reduce(action: (state: UIState) -> UIState) {
        mutex.withLock {
            stateFlow.emit(action(stateValue))
        }
    }

    override suspend fun ViewStateDelegate<UIState, Event>.sendEvent(event: Event) {
        singleEventsChannel.send(event)
    }

    override fun CoroutineScope.asyncReduce(action: (state: UIState) -> UIState) {
        launch {
            reduce(action)
        }
    }
}
