import Model.Game
import Model.GameReport
import Model.Player
import com.google.gson.Gson
import java.io.File

fun main(args: Array<String>) {
    val quakeLogs = QuakeLogs()
    val reportList = if (args.isEmpty()) {
        quakeLogs.loadLog("/logs/qgames.log")
    } else {
        quakeLogs.loadLog(args[0], fromResource = false)
    }
    println(reportList)
}

class QuakeLogs {
    private val gameList = mutableListOf<Game>()
    private val gameReportList = mutableListOf<GameReport>()
    private var currentGame = Game(id = 0)

    fun loadLog(filename: String, fromResource: Boolean = true): String {
        val lines = if (fromResource) {
            this::class.java.getResource(filename)?.readText(Charsets.UTF_8)?.lines()
        } else {
            File(filename).readLines()
        }
        if (lines.isNullOrEmpty()) {
            return "Invalid log file"
        }
        readLogLines(lines)
        val gson = Gson()
        return gson.toJson(gameReportList)
    }

    fun checkInitGame(line: String) = line.contains(Constants.INIT_GAME)
    fun checkShutDownGame(line: String) = line.contains(Constants.SHUTDOWN_GAME)
    fun checkClientConnected(line: String) = line.contains(Constants.CLIENT_CONNECT)
    fun checkClientBegin(line: String) = line.contains(Constants.CLIENT_BEGIN)
    fun checkClientDisconnect(line: String) = line.contains(Constants.CLIENT_DISCONNECT)
    fun checkClientUserInfoChanged(line: String) = line.contains(Constants.CLIENT_USER_INFO_CHANGED)
    fun checkKill(line: String) = line.contains(Constants.KILL)

    fun readLogLines(lines: List<String>) {
        lines.forEach { line ->
            if (checkInitGame(line)) {
                startNewGame()
            }
            if (checkShutDownGame(line)) {
                closeCurrentGame()
            }
            if (checkClientUserInfoChanged(line)) {
                updatePlayerInfo(line)
            }
            if (checkKill(line)) {
                updateKillCount(line)
            }
            if (checkClientDisconnect(line)) {
                disconectPlayer(line)
            }
        }
    }

    fun startNewGame() {
        if (currentGame.gameRunning) {
            closeCurrentGame()
        }
        currentGame = Game(id = gameList.size)
        currentGame.gameRunning = true
    }

    fun closeCurrentGame() {
        currentGame.gameRunning = false
        updateGameKillData()
        gameList.add(currentGame)
        generateReport(currentGame)
    }

    fun disconnectPlayers(players: List<Player>) {
        players.forEach { it.isConnected = false }
    }

    fun disconectPlayer(line: String) {
        val splitedLine = line.split(":")
        val playerId = splitedLine.last().trim().toInt()
        currentGame.players.first { it.id == playerId }.isConnected = false
    }

    fun updatePlayerInfo(line: String) {
        val splitedLine = line.split(Constants.CLIENT_USER_INFO_CHANGED)[1].split("\\")
        val id = splitedLine[0].removeSuffix("n").trim().toInt()
        val name = splitedLine[1]

        val players = currentGame.players.filter { it.id == id }
        disconnectPlayers(players)

        val player = currentGame.players.firstOrNull { it.name == name }
        if (player == null) {
            currentGame.players.add(Player(id = id, name = name, isConnected = true))
        } else {
            player.id = id
            player.isConnected = true
        }
    }

    fun updateKillCount(line: String) {
        val splitedLine = line.split(Constants.KILL)[1].trim().split(":")[0].split(" ")
        val killerId = splitedLine[0].toInt()
        val deadId = splitedLine[1].toInt()
        val weaponId = splitedLine[2].toInt()
        val deathType = DeathTypes.values()[weaponId]

        currentGame.totalKills++
        if (killerId == Constants.WORLD) {
            currentGame.players.first { it.id == deadId && it.isConnected }.suicideCount++
            currentGame.players.first { it.id == deadId && it.isConnected }.deaths++
        } else {
            currentGame.players.first { it.id == killerId && it.isConnected }.killCount++
            currentGame.players.first { it.id == deadId && it.isConnected }.deaths++
        }
        currentGame.killsByTypes[deathType] = currentGame.killsByTypes[deathType]?.plus(1) ?: 1
    }

    fun updateGameKillData() {
        currentGame.players.forEach { player ->
            currentGame.kills[player.name] = player.getKillTotal()
        }
    }

    fun generateReport(game: Game) {
        val playersList = mutableListOf<String>()
        val kills = mutableMapOf<String, Int>()
        game.players.forEach {
            playersList.add(it.name)
            kills[it.name] = it.getKillTotal()
        }

        val gameReport = GameReport(
            id = game.id,
            totalKills = game.totalKills,
            players = playersList,
            kills = kills.toList().sortedByDescending { (_, value) -> value }.toMap(),
            killsByMeans = game.killsByTypes.toList().sortedByDescending { (_, value) -> value }.toMap()
        )
        gameReportList.add(gameReport)
    }
}