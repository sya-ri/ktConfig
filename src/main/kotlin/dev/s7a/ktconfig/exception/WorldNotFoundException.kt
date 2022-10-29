package dev.s7a.ktconfig.exception

class WorldNotFoundException(world: String) : IllegalArgumentException("Not found world: $world")
