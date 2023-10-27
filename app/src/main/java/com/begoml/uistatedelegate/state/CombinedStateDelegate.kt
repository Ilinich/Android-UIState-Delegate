package com.begoml.uistatedelegate.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.uistatedelegate.common.collectIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface CombinedStateDelegate<UiState, State, Event> :
    UiStateDelegate<UiState, Event>,
    InternalStateDelegate<State> {

    /**
     * Transforms UI state using the specified transformation.
     *
     * @param transform  - function to transform UI state.
     */
    suspend fun CombinedStateDelegate<UiState, State, Event>.updateUiState(
        transform: (uiState: UiState, state: State) -> UiState
    )

    fun CombinedStateDelegate<UiState, State, Event>.collectUpdateUiState(
        coroutineScope: CoroutineScope,
        transform: (state: State, uiState: UiState) -> UiState,
    ): Job

    fun <T> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow: Flow<T>,
        transform: suspend (state: State, uiState: UiState, value: T) -> UiState,
    ): Job

    fun <T1, T2> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2) -> UiState,
    ): Job

    fun <T1, T2, T3> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2, value3: T3) -> UiState,
    ): Job

    fun <T1, T2, T3, T4> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2, value3: T3, value4: T4) -> UiState,
    ): Job
}

/**
 * Implementation of a delegate to manage state.
 * This delegate stores and manages two types of state: UI state and internal state.
 *
 * @param mutexState - mutex for synchronizing state access.
 * @param initialUiState - initial UI state.
 * @param initialState - initial internal state.
 * @param singleLiveEventCapacity - channel capacity for SingleLiveEvent.
 */
class CombinedStateDelegateImpl<UiState, State, Event>(
    private val mutexState: Mutex = Mutex(),
    initialUiState: UiState,
    initialState: State,
    singleLiveEventCapacity: Int = Channel.BUFFERED,
) : CombinedStateDelegate<UiState, State, Event>,
    UiStateDelegate<UiState, Event> by UiStateDelegateImpl(
        mutexState = mutexState,
        initialUiState = initialUiState,
        singleLiveEventCapacity = singleLiveEventCapacity,
    ),
    InternalStateDelegate<State> by InternalStateDelegateImpl(
        mutexState = mutexState,
        initialState = initialState,
    ) {

    override suspend fun CombinedStateDelegate<UiState, State, Event>.updateUiState(
        transform: (uiState: UiState, state: State) -> UiState
    ) = updateUiState { uiState -> transform(uiState, internalState) }

    /**
     * Subscription to changes in internal state with transformation
     * of UI state depending on changes in internal state.
     */
    override fun CombinedStateDelegate<UiState, State, Event>.collectUpdateUiState(
        coroutineScope: CoroutineScope,
        transform: (state: State, uiState: UiState) -> UiState,
    ): Job {
        return internalStateFlow.onEach { state ->
            updateUiState { uiState -> transform(state, uiState) }
        }.launchIn(coroutineScope)
    }

    override fun <T> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow: Flow<T>,
        transform: suspend (state: State, uiState: UiState, value: T) -> UiState
    ): Job {
        return internalStateFlow.combine(flow) { state, value -> transform(state, uiState, value) }
            .onEach { newState -> updateUiState { _ -> newState } }
            .launchIn(coroutineScope)
    }

    override fun <T1, T2> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2) -> UiState
    ): Job {
        return combine(internalStateFlow, flow1, flow2) { state, value1, value2 ->
            transform(
                state,
                uiState,
                value1,
                value2
            )
        }.onEach { newState -> updateUiState { _ -> newState } }
            .launchIn(coroutineScope)
    }

    override fun <T1, T2, T3> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2, value3: T3) -> UiState
    ): Job {
        return combine(
            internalStateFlow,
            flow1,
            flow2,
            flow3
        ) { state, value1, value2, value3 ->
            transform(
                state,
                uiState,
                value1,
                value2,
                value3
            )
        }.onEach { newState -> updateUiState { _ -> newState } }
            .launchIn(coroutineScope)
    }

    override fun <T1, T2, T3, T4> CombinedStateDelegate<UiState, State, Event>.combineCollectUpdateUiState(
        coroutineScope: CoroutineScope,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        transform: suspend (state: State, uiState: UiState, value1: T1, value2: T2, value3: T3, value4: T4) -> UiState
    ): Job {
        return combine(
            internalStateFlow,
            flow1,
            flow2,
            flow3,
            flow4
        ) { state, value1, value2, value3, value4 ->
            transform(
                state,
                uiState,
                value1,
                value2,
                value3,
                value4
            )
        }.onEach { newState -> updateUiState { _ -> newState } }
            .launchIn(coroutineScope)
    }
}

inline fun <UiState, State, Event> CombinedStateDelegate<UiState, State, Event>.collectInternalState(
    coroutineScope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline collector: suspend (State) -> Unit
): Job = coroutineScope.launch(
    context = context, start = start
) {
    internalStateFlow.collectIn(
        coroutineScope = coroutineScope,
        start = start,
        collector = collector,
    )
}

context(ViewModel)
fun <UiState, State, Event> CombinedStateDelegate<UiState, State, Event>.asyncUpdateInternalState(
    transform: (state: State) -> State
): Job {
    return asyncUpdateInternalState(
        coroutineScope = viewModelScope,
        transform = transform
    )
}

