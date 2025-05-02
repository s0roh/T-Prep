package com.example.preferences.tooltip

interface TooltipPreferences {

    fun isLocalTooltipEnabled(): Boolean
    fun isPublicTooltipEnabled(): Boolean
    fun isPublicDecksTooltipEnabled(): Boolean

    fun setLocalTooltipShown()
    fun setPublicTooltipShown()
    fun setPublicDecksTooltipShown()
}