package com.sc7258.springshopflyway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpringProfileResolverTest {

    @Test
    fun `command-line args에서 profile을 우선 적용한다`() {
        val activeProfile = resolveActiveProfile(
            args = arrayOf("--spring.profiles.active=qa"),
            systemProperty = "dev",
            environmentVariable = "prod"
        )

        assertEquals("qa", activeProfile)
    }

    @Test
    fun `공백 분리형 command-line args도 profile로 인식한다`() {
        val activeProfile = resolveActiveProfile(
            args = arrayOf("--spring.profiles.active", "prod"),
            systemProperty = null,
            environmentVariable = null
        )

        assertEquals("prod", activeProfile)
    }

    @Test
    fun `system property는 environment variable보다 우선한다`() {
        val activeProfile = resolveActiveProfile(
            args = emptyArray(),
            systemProperty = "qa",
            environmentVariable = "dev"
        )

        assertEquals("qa", activeProfile)
    }

    @Test
    fun `profile이 없으면 dev를 기본값으로 사용한다`() {
        val activeProfile = resolveActiveProfile(
            args = emptyArray(),
            systemProperty = null,
            environmentVariable = null
        )

        assertEquals("dev", activeProfile)
    }

    @Test
    fun `env 파일은 지원하는 profile에만 매핑한다`() {
        assertEquals(".env.dev", resolveEnvFileName("dev"))
        assertEquals(".env.qa", resolveEnvFileName("qa"))
        assertEquals(".env.prod", resolveEnvFileName("prod"))
        assertNull(resolveEnvFileName("test"))
    }
}
