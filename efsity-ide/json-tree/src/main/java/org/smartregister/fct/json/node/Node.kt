package org.smartregister.fct.json.node

import androidx.compose.runtime.Composable
import org.smartregister.fct.json.JsonScope
import org.smartregister.fct.json.node.extension.ExpandableNode
import org.smartregister.fct.json.node.extension.ExpandableNodeHandler
import org.smartregister.fct.json.node.extension.SelectableNode
import org.smartregister.fct.json.node.extension.SelectableNodeHandler
import org.smartregister.fct.json.util.randomUUID

typealias NodeComponent<T> = @Composable JsonScope<T>.(Node<T>) -> Unit

sealed interface Node<T> {

    val key: String

    val content: T

    val name: String

    val depth: Int

    val isSelected: Boolean

    val iconComponent: NodeComponent<T>

    val nameComponent: NodeComponent<T>
}

class LeafNode<T> internal constructor(
    override val content: T,
    override val depth: Int,
    override val key: String = randomUUID,
    override val name: String = content.toString(),
    override val iconComponent: NodeComponent<T> = { DefaultNodeIcon(it) },
    override val nameComponent: NodeComponent<T> = { DefaultNodeName(it) }
) : Node<T>,
    SelectableNode by SelectableNodeHandler()

internal class BranchNode<T> internal constructor(
    override val content: T,
    override val depth: Int,
    override val key: String = randomUUID,
    override val name: String = content.toString(),
    override val iconComponent: NodeComponent<T> = { DefaultNodeIcon(it) },
    override val nameComponent: NodeComponent<T> = { DefaultNodeName(it) }
) : Node<T>,
    SelectableNode by SelectableNodeHandler(),
    ExpandableNode by ExpandableNodeHandler()
