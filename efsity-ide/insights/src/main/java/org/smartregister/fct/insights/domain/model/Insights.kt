package org.smartregister.fct.insights.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Insights(
    val resourceTypeCount: Map<String, Int>,
    val unSyncedResources: List<Pair<String, Int>>,
    val userInfo: UserInfo?,
    val userName: String?,
    val organization: String?,
    val careTeam: String?,
    val location: String?,
    val appVersionCode: String,
    val appVersion: String,
    val buildDate: String,
) {

    fun hasEnoughResourceTypeCount(): Boolean {
        return resourceTypeCount.isNotEmpty() && resourceTypeCount.toList().maxByOrNull {
            it.second
        }!!.second > 9
    }

    fun getMaxResourceTypeCount(): Int {
        return resourceTypeCount.toList().maxByOrNull { it.second }!!.second
    }
}