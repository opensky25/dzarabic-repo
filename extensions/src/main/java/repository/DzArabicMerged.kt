package repository

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

// ==================== 1. FaselHDS Provider ====================
class FaselHDSProvider : MainAPI() {
    override var mainUrl = "https://www.faselhds.care"
    override var name = "FaselHDS"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search/$query").document
        return doc.select(".movie-item").mapNotNull {
            MovieSearchResponse(
                name = it.selectFirst("h2")?.text() ?: return@mapNotNull null,
                url = it.attr("href"),
                apiName = this.name,
                type = TvType.Movie,
                posterUrl = it.selectFirst("img")?.attr("src")
            )
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        return MovieLoadResponse(
            name = doc.selectFirst("h1.title")?.text() ?: "No Title",
            url = url,
            apiName = this.name,
            type = TvType.Movie,
            posterUrl = doc.selectFirst(".poster img")?.attr("src"),
            year = doc.selectFirst(".year")?.text()?.toIntOrNull(),
            plot = doc.selectFirst(".description")?.text(),
            episodes = doc.select(".episode-list a").map {
                Episode(
                    it.text(),
                    it.attr("href"),
                    it.text().filter { c -> c.isDigit() }.toIntOrNull() ?: 1
                )
            }
        )
    }
}

// ==================== 2. Wecima Provider ====================
class WecimaProvider : MainAPI() {
    override var mainUrl = "https://wecima.video"
    override var name = "Wecima"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    private fun String.ensureUrl() = if (startsWith("http")) this else "$mainUrl$this"

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search/$query").document
        return doc.select(".movie").mapNotNull {
            MovieSearchResponse(
                name = it.selectFirst("h3")?.text() ?: return@mapNotNull null,
                url = it.attr("href").ensureUrl(),
                apiName = this.name,
                type = TvType.Movie,
                posterUrl = it.selectFirst("img")?.attr("data-src")
            )
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        return MovieLoadResponse(
            name = doc.selectFirst("h1.title")?.text() ?: "No Title",
            url = url,
            apiName = this.name,
            type = TvType.Movie,
            posterUrl = doc.selectFirst(".poster img")?.attr("src"),
            year = doc.selectFirst(".year")?.text()?.toIntOrNull(),
            plot = doc.selectFirst(".desc")?.text(),
            episodes = doc.select(".episode-list a").map {
                Episode(
                    it.text(),
                    it.attr("href").ensureUrl(),
                    it.text().filter { c -> c.isDigit() }.toIntOrNull() ?: 1
                )
            }
        )
    }
}

// ==================== 3. Cima4u Provider ====================
class Cima4uProvider : MainAPI() {
    override var mainUrl = "https://cima4u.actor"
    override var name = "Cima4u"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search/$query").document
        return doc.select(".movie").mapNotNull {
            MovieSearchResponse(
                name = it.selectFirst("h3")?.text() ?: return@mapNotNull null,
                url = it.attr("href"),
                apiName = this.name,
                type = TvType.Movie,
                posterUrl = it.selectFirst("img")?.attr("data-src")
            )
        }
    }
}

// ==================== 4. EgyDead Provider ====================
class EgyDeadProvider : MainAPI() {
    override var mainUrl = "https://egydead.fyi"
    override var name = "EgyDead"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries, TvType.Live)

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        if (url.contains("/live/")) {
            return LiveLoadResponse(
                name = doc.selectFirst("h1")?.text() ?: "Live Channel",
                url = url,
                apiName = this.name,
                posterUrl = doc.selectFirst("img.poster")?.attr("src")
            )
        }
        return null
    }
}

// [...] (Similar implementations for remaining 11 providers)

// ==================== 15. Shaeid4u Provider ====================
class Shaeid4uProvider : MainAPI() {
    override var mainUrl = "https://shaeid4u.net"
    override var name = "Shaeid4u"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search?q=$query").document
        return doc.select(".film-card").mapNotNull {
            MovieSearchResponse(
                name = it.selectFirst(".film-name")?.text() ?: return@mapNotNull null,
                url = it.attr("href"),
                apiName = this.name,
                type = TvType.Movie,
                posterUrl = it.selectFirst("img")?.attr("data-src")
            )
        }
    }
}