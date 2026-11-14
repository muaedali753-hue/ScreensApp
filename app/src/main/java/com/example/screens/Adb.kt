package com.example.screens

import kotlinx.coroutines.delay

interface AdbController {
    suspend fun connect(): Boolean
    suspend fun shell(command: String): String
}

class FakeAdb : AdbController {
    override suspend fun connect(): Boolean { delay(400); return true }
    override suspend fun shell(command: String): String { delay(300); return "" }
}
