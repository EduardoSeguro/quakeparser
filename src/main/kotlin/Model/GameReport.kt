package Model

import DeathTypes
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GameReport(
    @SerializedName("game_id") val id: Int,
    @SerializedName("total_kills") val totalKills: Int,
    @SerializedName("players") val players: MutableList<String>,
    @SerializedName("kills") val kills: Map<String, Int>,
    @SerializedName("kills_by_means") val killsByMeans: Map<DeathTypes, Int>
) : Serializable