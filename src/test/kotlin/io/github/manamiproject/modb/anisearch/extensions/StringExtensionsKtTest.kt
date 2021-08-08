package io.github.manamiproject.modb.anisearch.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class StringExtensionsKtTest {

    @Nested
    inner class IsIntTests {

        @ParameterizedTest
        @ValueSource(strings = ["0", "1", "10", "100", "1000", "43252446346"])
        fun `return true, because the the given value represents an Int`(value: String) {
            // when
            val result = value.isInt()

            // then
            assertThat(result).isTrue()
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "  ", " 4", "5 ", "123,3", "123.5", "2021-07-31"])
        fun `returns false, because the given value is not a representation of an Int`(value: String) {
            // when
            val result = value.isInt()

            // then
            assertThat(result).isFalse()
        }
    }
}