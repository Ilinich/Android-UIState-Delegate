package com.begoml.uistatedelegate.uistate

/**
 * UiStateDiffRender for efficient ui updates.
 */
class UiStateDiffRender<T> private constructor(
    private val renders: List<Render<T, Any>>
) {

    private var lastUiState: T? = null

    fun render(newState: T) {
        lastUiState.let { oldUiState ->
            renders.forEach { render ->
                val property = render.property
                val newProperty = property(newState)
                if (oldUiState == null || property(oldUiState) != newProperty) {
                    render.callback(newProperty)
                }
            }
        }

        lastUiState = newState
    }

    private class Render<T, R>(
        val property: (T) -> R,
        val callback: (R) -> Unit,
    )

    /**
     * it's obligatory to clear render in onDestroyView
     */
    fun clear() {
        lastUiState = null
    }

    class Builder<T> @PublishedApi internal constructor() {

        private val renders = mutableListOf<Render<T, Any>>()

        operator fun <R> ((T) -> R).invoke(callback: (R) -> Unit) {
            renders += Render(
                property = this,
                callback = callback,
            ) as Render<T, Any>
        }

        fun build(): UiStateDiffRender<T> = UiStateDiffRender(renders)
    }
}
