package Model

class Player(
    var id: Int,
    var name: String = "",
    var killCount: Int = 0,
    var suicideCount: Int = 0,
    var deaths: Int = 0,
    var isConnected: Boolean = true
) {
    fun getKillTotal(): Int {
        return killCount - suicideCount
    }
}