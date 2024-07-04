package net.rsprox.proxy.config

import java.net.URL

public data class JavConfig(
    public val text: String,
) {
    public constructor(url: URL) : this(parseText(url))

    public fun getRevision(): Int {
        return getParam(REVISION_PARAM)
            ?.toIntOrNull()
            ?: throw IllegalStateException("Jav config does not support revision.")
    }

    public fun getWorldListUrl(): String {
        return getParam(WORLD_LIST_PARAM)
            ?: throw IllegalStateException("Jav config does not support world list.")
    }

    public fun replaceWorldListUrl(replacement: String): JavConfig {
        return toBuilder()
            .replaceParam(WORLD_LIST_PARAM, replacement)
            .build()
    }

    private fun toBuilder(): JavConfigBuilder {
        return JavConfigBuilder(this)
    }

    private fun getParam(id: Int): String? {
        val prefix = "param=$id="
        val line =
            text
                .lineSequence()
                .firstOrNull { line -> line.startsWith(prefix) }
                ?: return null
        return line.substring(prefix.length)
    }

    private companion object {
        private const val REVISION_PARAM: Int = 25
        private const val WORLD_LIST_PARAM: Int = 17

        private fun parseText(url: URL): String {
            return url.readText(Charsets.UTF_8)
        }
    }
}
