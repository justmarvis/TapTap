package com.amba.taptap.model

class Message {

    // Properties
    var message: String? = null
    var senderId: String? = null
    var timestamp: Long = 0

    // Default constructor
    constructor() {}

    // Parameterized constructor
    constructor(message: String?, senderId: String?, timestamp: Long) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }
}