package com.amba.taptap.model

class User {
    var imageUrl: String? = null
    var email: String? = null
    var username: String? = null
    var fullname: String? = null
    var password: String? = null
    var uid: String? = null
    var country: String? = null
    var bio: String? = null

    constructor(){}

    constructor(imageUrl: String?, email: String?,country: String?, fullname: String?, username: String?, password: String?, uid: String?, bio: String?) {
        this.imageUrl = imageUrl
        this.email = email
        this.username = username
        this.fullname = fullname
        this.password = password
        this.uid = uid
        this.country = country
        this.bio = bio
    }

}