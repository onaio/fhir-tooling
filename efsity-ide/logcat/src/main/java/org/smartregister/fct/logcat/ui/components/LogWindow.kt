package org.smartregister.fct.logcat.ui.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.logger.model.Log
import org.smartregister.fct.logger.model.LogFilter
import org.smartregister.fct.logger.model.LogLevel

@Composable
internal fun LogWindow(
    wrapText: State<Boolean>,
    stickScrollToBottom: MutableState<Boolean>,
    logLevelFilter: State<LogLevel?>
) {

    val logs = remember { mutableStateListOf<Log>() }
    val state = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        logs.addAll(FCTLogger.getAllLogs())
        delay(100)
        if (logs.isNotEmpty()) {
            state.animateScrollToItem(logs.size - 1)
        }
        FCTLogger.listen().collectLatest {
            it?.let(logs::add)
            if (stickScrollToBottom.value) {
                state.animateScrollToItem(logs.size - 1)
            }
        }
    }

    LaunchedEffect(Unit) {
        FCTLogger.addFilter("logcat", object: LogFilter{
            override fun onClear() {
                logs.clear()
            }
        })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SelectionContainer(
            modifier = Modifier.pointerHoverIcon(PointerIcon.Text)
        ) {

            val lazyModifier = if (!wrapText.value) {
                Modifier.fillMaxSize().horizontalScroll(horizontalScrollState)
            } else Modifier.fillMaxSize()

            LazyColumn(
                modifier = lazyModifier,
                contentPadding = PaddingValues(8.dp),
                state = state
            ) {

                items(logs.filter {
                    if (logLevelFilter.value != null && logLevelFilter.value != LogLevel.VERBOSE) {
                        it.priority == logLevelFilter.value
                    } else true
                }) { log ->
                    LogView(log, wrapText.value)
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )

        if (!wrapText.value) {
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth(),
                adapter = rememberScrollbarAdapter(
                    scrollState = horizontalScrollState
                )
            )
        }
    }
}