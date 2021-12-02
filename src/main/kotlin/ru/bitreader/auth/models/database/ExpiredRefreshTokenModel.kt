package ru.bitreader.auth.models.database

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "expired_refresh_token")
class ExpiredRefreshTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
    var tokenId: String? = null
    var addAt: Date? = null
}