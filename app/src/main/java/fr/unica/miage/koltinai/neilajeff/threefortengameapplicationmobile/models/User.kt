package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

data class User(
    val id: String,
    val username: String,
    var password: String?
) {
    init {
        if (password == null)
            password = username
    }
}
