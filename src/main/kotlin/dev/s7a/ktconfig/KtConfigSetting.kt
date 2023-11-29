package dev.s7a.ktconfig

/**
 * Config conversion settings
 *
 * @property strictListElement If enabled, throws an exception when [List] contains null. By default, it ignores that value and returns the list.
 * @property strictMapElement If enabled, throws an exception when [Map] contains null. By default, it ignores that value and returns the map.
 *
 * @since 1.0.0
 */
data class KtConfigSetting(
    val strictListElement: Boolean = false,
    val strictMapElement: Boolean = false
)
