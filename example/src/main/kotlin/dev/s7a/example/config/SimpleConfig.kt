package dev.s7a.example.config

import dev.s7a.ktconfig.ForKtConfig

@ForKtConfig
data class SimpleConfig(
    val serverName: String = "My Server",
    val description: String = "A configuration example",
    val maxPlayers: Int = 20,
    val tickRate: Double = 20.0,
    val isEnabled: Boolean = true,
    val debugMode: Boolean = false,
    val allowedWorlds: List<String> = listOf("world", "world_nether", "world_the_end"),
    val serverState: ServerState = ServerState.RUNNING,
    val version: Float = 1.0f,
) {
    enum class ServerState {
        STARTING,
        RUNNING,
        STOPPING,
        MAINTENANCE,
    }
}
