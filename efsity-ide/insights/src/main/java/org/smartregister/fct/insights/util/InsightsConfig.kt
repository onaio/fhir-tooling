package org.smartregister.fct.insights.util

import org.smartregister.fct.insights.domain.model.Insights

internal object InsightsConfig {
    var activeInsights: Insights? = null
    var activeDeviceId: String? = null
}