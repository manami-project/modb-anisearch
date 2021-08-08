package io.github.manamiproject.modb.anisearch.extensions

/**
 * Determines whether a given [String] is a representation of an [Int] or not.
 * This function only checks the syntax. It doesn't check if the value can be safely parsed. So there is no check
 * if the value is within the boundaries of [Int.MIN_VALUE] and [Int.MAX_VALUE].
 * @since 5.2.0
 * @return `true` if the given value offers the general syntax of an [Int]
 */
internal fun String.isInt(): Boolean = Regex("[0-9]+").matches(this)