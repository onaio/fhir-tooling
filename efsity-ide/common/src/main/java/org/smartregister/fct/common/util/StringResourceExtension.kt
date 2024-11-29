package org.smartregister.fct.common.util

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun StringResource.asString(vararg formatArgs: Any) = stringResource(this, *formatArgs)


context (CoroutineScope)
suspend fun StringResource.asString() = getString(this@asString)