package com.example.hype_up

data class User(
    val name: String,
    val imageUrl: String,
    val deviceToken: String,
    val status: String,
    val online: Boolean,
    val uid: String
) {
    constructor() : this("", "",  "", "Hey There, I am using whatsapp", false, "")

    constructor(name: String, imageUrl: String,  uid: String) :
            this(name, imageUrl,  "", uid = uid, status = "Hey There, I am using whatsapp", online = false)

}
