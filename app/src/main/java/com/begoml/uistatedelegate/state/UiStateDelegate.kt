package com.begoml.uistatedelegate.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * UiState - must be Data class, immutable
 */
interface UiStateDelegate<UiState, Event> {

    /**
     * Declarative description of the UI based on the current state.
     */
    val uiStateFlow: StateFlow<UiState>

    val singleEvents: Flow<Event>

    /**
     * State is read-only
     * The only way to change the state is to emit[reduce] an action,
     * an object describing what happened.
     */
    val UiStateDelegate<UiState, Event>.uiState: UiState

    /**
     * Transforms UI state using the specified transformation.
     *
     * @param transform  - function to transform UI state.
     */
    suspend fun UiStateDelegate<UiState, Event>.updateUiState(
        transform: (uiState: UiState) -> UiState,
    )

    /**
     * Changing the state without blocking the coroutine.
     */
    fun UiStateDelegate<UiState, Event>.asyncUpdateUiState(
        coroutineScope: CoroutineScope,
        transform: (state: UiState) -> UiState,
    ): Job

    suspend fun UiStateDelegate<UiState, Event>.sendEvent(event: Event)
}

/**
 * Implementation of a delegate to manage state.
 * This delegate stores and manages UI state.
 *
 * @param mutexState A mutex for synchronizing state access.
 * @param initialUiState Initial UI state.
 * @param singleLiveEventCapacity Channel capacity for SingleLiveEvent.
 */
class UiStateDelegateImpl<UiState, Event>(
    initialUiState: UiState,
    singleLiveEventCapacity: Int = Channel.BUFFERED,
    private val mutexState: Mutex = Mutex()
) : UiStateDelegate<UiState, Event> {

    /**
     * The source of truth that drives our app.
     */
    private val uiMutableStateFlow = MutableStateFlow(initialUiState)

    override val uiStateFlow: StateFlow<UiState>
        get() = uiMutableStateFlow.asStateFlow()

    override val UiStateDelegate<UiState, Event>.uiState: UiState
        get() = uiMutableStateFlow.value

    private val singleEventsChannel = Channel<Event>(singleLiveEventCapacity)

    override val singleEvents: Flow<Event>
        get() = singleEventsChannel.receiveAsFlow()

    override suspend fun UiStateDelegate<UiState, Event>.updateUiState(
        transform: (uiState: UiState) -> UiState,
    ) {
        mutexState.withLock {
            uiMutableStateFlow.emit(transform(uiState))
        }
    }

    override suspend fun UiStateDelegate<UiState, Event>.sendEvent(event: Event) {
        singleEventsChannel.send(event)
    }

    override fun UiStateDelegate<UiState, Event>.asyncUpdateUiState(
        coroutineScope: CoroutineScope,
        transform: (state: UiState) -> UiState,
    ): Job {
        return coroutineScope.launch {
            updateUiState { state -> transform(state) }
        }
    }
}
