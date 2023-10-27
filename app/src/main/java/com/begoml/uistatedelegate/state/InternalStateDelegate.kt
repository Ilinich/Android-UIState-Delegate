package com.begoml.uistatedelegate.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface InternalStateDelegate<State> {

    /**
     * Get the internal state as data flow.
     */
    val InternalStateDelegate<State>.internalStateFlow: Flow<State>

    /**
     * Get the current internal state.
     */
    val InternalStateDelegate<State>.internalState: State

    /**
     * Transforms internal state using the specified transformation.
     *
     * @param transform  - function to transform internal state.
     */
    suspend fun InternalStateDelegate<State>.updateInternalState(
        transform: (state: State) -> State,
    )

    /**
     * Changing the state without blocking the coroutine.
     */
    fun InternalStateDelegate<State>.asyncUpdateInternalState(
        coroutineScope: CoroutineScope, transform: (state: State) -> State
    ): Job
}

/**
 * Implementation of a delegate to manage state.
 * This delegate stores and manages internal state.
 *
 * @param mutexState - mutex for synchronizing state access.
 * @param initialState - initial internal state.
 */
class InternalStateDelegateImpl<State>(
    private val mutexState: Mutex = Mutex(),
    initialState: State,
) : InternalStateDelegate<State> {

    private val internalMutableState = MutableStateFlow(initialState)

    override val InternalStateDelegate<State>.internalStateFlow: Flow<State>
        get() = internalMutableState

    override val InternalStateDelegate<State>.internalState: State
        get() = internalMutableState.value

    override suspend fun InternalStateDelegate<State>.updateInternalState(
        transform: (state: State) -> State,
    ) {
        mutexState.withLock { internalMutableState.update(transform) }
    }

    override fun InternalStateDelegate<State>.asyncUpdateInternalState(
        coroutineScope: CoroutineScope, transform: (state: State) -> State
    ): Job {
        return coroutineScope.launch {
            updateInternalState(transform)
        }
    }
}