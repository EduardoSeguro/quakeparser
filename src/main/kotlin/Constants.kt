class Constants {
    companion object {
        const val INIT_GAME = "InitGame:"
        const val SHUTDOWN_GAME = "ShutdownGame:"
        const val KILL = "Kill:"
        const val CLIENT_CONNECT = "ClientConnect:"
        const val CLIENT_USER_INFO_CHANGED = "ClientUserinfoChanged:"
        const val CLIENT_BEGIN = "ClientBegin:"
        const val CLIENT_DISCONNECT = "ClientDisconnect:"
        const val WORLD = 1022 // World player ID
    }
}

enum class DeathTypes {
    MOD_UNKNOWN,
    MOD_SHOTGUN,
    MOD_GAUNTLET,
    MOD_MACHINEGUN,
    MOD_GRENADE,
    MOD_GRENADE_SPLASH,
    MOD_ROCKET,
    MOD_ROCKET_SPLASH,
    MOD_PLASMA,
    MOD_PLASMA_SPLASH,
    MOD_RAILGUN,
    MOD_LIGHTNING,
    MOD_BFG,
    MOD_BFG_SPLASH,
    MOD_WATER,
    MOD_SLIME,
    MOD_LAVA,
    MOD_CRUSH,
    MOD_TELEFRAG,
    MOD_FALLING,
    MOD_SUICIDE,
    MOD_TARGET_LASER,
    MOD_TRIGGER_HURT,
    MOD_NAIL,
    MOD_CHAINGUN,
    MOD_PROXIMITY_MINE,
    MOD_KAMIKAZE,
    MOD_JUICED,
    MOD_GRAPPLE
}