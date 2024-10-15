package org.smartregister.fct.json.tree

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import org.smartregister.fct.json.node.Node
import org.smartregister.fct.json.node.TreeApplier
import org.smartregister.fct.json.tree.extension.ExpandableTree
import org.smartregister.fct.json.tree.extension.ExpandableTreeHandler
import org.smartregister.fct.json.tree.extension.SelectableTree
import org.smartregister.fct.json.tree.extension.SelectableTreeHandler

@DslMarker
private annotation class TreeMarker

@Immutable
@TreeMarker
data class TreeScope internal constructor(
    val depth: Int,
    internal val isExpanded: Boolean = false,
    internal val expandMaxDepth: Int = 0
)

@Stable
class Tree<T> internal constructor(
    val nodes: List<Node<T>>
) : ExpandableTree<T> by ExpandableTreeHandler(nodes),
    SelectableTree<T> by SelectableTreeHandler(nodes)

@Composable
internal fun <T> Tree(
    key: Any? = null,
    content: @Composable TreeScope.() -> Unit
): Tree<T> {
    val applier = remember(key) { TreeApplier<T>() }
    val compositionContext = rememberCompositionContext()
    val composition =
        remember(applier, compositionContext) { Composition(applier, compositionContext) }
    composition.setContent { TreeScope(depth = 0).content() }
    return remember(applier) { Tree(applier.children) }
}
