package com.begoml.uistatedelegate.uistate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <T> Fragment.uiStateDiffRender(
    init: UiStateDiffRender.Builder<T>.() -> Unit
): UiStateDiffRender<T> {

    var render: UiStateDiffRender<T>? = null

    lifecycle.addObserver(object : DefaultLifecycleObserver {
        val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
            val viewLifecycleOwner = it ?: return@Observer

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    render?.clear()
                }
            })
        }

        override fun onCreate(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            render = null
        }
    })

    return UiStateDiffRender.Builder<T>()
        .apply(init)
        .build().apply {
            render = this
        }
}

inline fun <T> AppCompatActivity.uiStateDiffRender(
    init: UiStateDiffRender.Builder<T>.() -> Unit
): UiStateDiffRender<T> {

    var render: UiStateDiffRender<T>? = null

    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            render?.clear()
            render = null
        }
    })

    return UiStateDiffRender.Builder<T>()
        .apply(init)
        .build().apply {
            render = this
        }
}

/**
 * render [State] with [lifecycleState]
 * The UI re-renders based on the new state
 **/
fun <State, Event> ViewStateDelegate<State, Event>.render(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    render: UiStateDiffRender<State>
): Job = lifecycleOwner.lifecycleScope.launch {
    uiState.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collectLatest(render::render)
}

/**
 * render [State] with [AppCompatActivity]
 * The UI re-renders based on the new state
 **/
fun <State, Event> ViewStateDelegate<State, Event>.render(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    render: UiStateDiffRender<State>
): Job = lifecycle.coroutineScope.launch {
    uiState.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collectLatest(render::render)
}

/**
 * send [Event] with [lifecycleState]
 * The UI re-renders based on the new event
 **/
fun <State, Event> ViewStateDelegate<State, Event>.collectEvent(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect { event ->
        block.invoke(event)
    }
}

/**
 * send [Event] with [AppCompatActivity]
 * The UI re-renders based on the new event
 **/
fun <State, Event> ViewStateDelegate<State, Event>.collectEvent(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycle.coroutineScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collect {
        block(it)
    }
}
