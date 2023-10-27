package com.begoml.uistatedelegate.uistate

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.begoml.uistatedelegate.state.UiStateDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.FlowCollector
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
fun <State, Event> UiStateDelegate<State, Event>.render(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    render: UiStateDiffRender<State>
): Job = lifecycleOwner.lifecycleScope.launch {
    uiStateFlow.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collectLatest(render::render)
}

/**
 * render [State] with [AppCompatActivity]
 * The UI re-renders based on the new state
 **/
fun <State, Event> UiStateDelegate<State, Event>.render(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    render: UiStateDiffRender<State>
): Job = lifecycle.coroutineScope.launch {
    uiStateFlow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collectLatest(render::render)
}

/**
 * send [Event] with [lifecycleState]
 * The UI re-renders based on the new event
 **/
fun <State, Event> UiStateDelegate<State, Event>.collectEvent(
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
fun <State, Event> UiStateDelegate<State, Event>.collectEvent(
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

@Composable
fun <State, Event> UiStateDelegate<State, Event>.CollectEventEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    vararg keys: Any?,
    collector: FlowCollector<Event>,
) = LaunchedEffect(Unit, *keys) {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect(collector)
}