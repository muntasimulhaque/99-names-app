package io.github.muntasimulhaque.names99.data

import android.content.Context
import kotlinx.serialization.json.Json
import java.util.TimeZone

object NamesRepository {

    @Volatile
    private var cache: List<Name>? = null

    private val json = Json { ignoreUnknownKeys = true }

    fun load(context: Context): List<Name> =
        cache ?: synchronized(this) {
            cache ?: json.decodeFromString<List<Name>>(
                context.assets.open("names.json").bufferedReader().use { it.readText() }
            ).also { cache = it }
        }

    fun byNumber(context: Context, number: Int): Name? =
        load(context).getOrNull(number - 1)

    /** Deterministic name of the day, based on the local calendar day. */
    fun dailyName(context: Context): Name {
        val now = System.currentTimeMillis()
        val localDays = (now + TimeZone.getDefault().getOffset(now)) / 86_400_000L
        val index = (localDays % 99L).toInt()
        return load(context)[index]
    }
}
