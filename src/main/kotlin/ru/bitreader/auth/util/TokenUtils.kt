package ru.bitreader.auth.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.lang.Classes.getResourceAsStream
import io.jsonwebtoken.security.SignatureException
import io.smallrye.jwt.build.Jwt
import org.jose4j.json.internal.json_simple.JSONObject
import org.jose4j.json.internal.json_simple.parser.JSONParser
import ru.bitreader.auth.models.database.Role
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


object TokenUtils {
	private const val publicKeyLocation = "/publickey.pem"
	private val publicKey: PublicKey = readPublicKey()
	private const val privateKeyLocation = "/privatekey.pem"
	private val privateKey = readPrivateKey()

	/**
	 * Generate token
	 * See https://quarkus.io/guides/security-jwt
	 * **/
	@Throws(Exception::class)
	fun generateToken(
		userId: Long,
		role: Role,
		duration: Long,
		issuer: String,
		accessTokenId: String?
	): String {
		val claimsBuilder = Jwt.claims()
		val currentTimeInSecs = currentTimeInSecs()
		claimsBuilder.issuer(issuer)
		claimsBuilder.subject(userId.toString())
		claimsBuilder.groups(role.toString())
		claimsBuilder.issuedAt(currentTimeInSecs)
		claimsBuilder.expiresAt(currentTimeInSecs + duration)
		claimsBuilder.claim("role", role)
		if (accessTokenId != null) claimsBuilder.claim("accessTokenId", accessTokenId)
		return claimsBuilder.jws().keyId(privateKeyLocation).sign(privateKey)
	}

	fun isValidToken(token: String): Boolean {
		return try {
			Jwts.parserBuilder().setSigningKey(publicKey).build().parse(token)
			true
		} catch (e: SignatureException) {
			false
		}
	}



	private fun readPublicKey(): PublicKey {
		getResourceAsStream(publicKeyLocation).use { contentIS ->
			val tmp = ByteArray(4096)
			val length = contentIS.read(tmp)
			return decodePublicKey(String(tmp, 0, length, Charsets.UTF_8))
		}
	}

	fun getPublicKeyByString(): String {
		return TokenUtils::class.java.getResourceAsStream(publicKeyLocation)
			?.bufferedReader(Charsets.UTF_8)!!.readText()
	}

	@Throws(Exception::class)
	private fun readPrivateKey(): PrivateKey {
		TokenUtils::class.java.getResourceAsStream(privateKeyLocation).use { contentIS ->
			val tmp = ByteArray(4096)
			val length = contentIS.read(tmp)
			return decodePrivateKey(String(tmp, 0, length, Charsets.UTF_8))
		}
	}

	@Throws(Exception::class)
	private fun decodePrivateKey(pemEncoded: String): PrivateKey {
		val encodedBytes = toEncodedBytes(pemEncoded)
		val keySpec = PKCS8EncodedKeySpec(encodedBytes)
		val kf = KeyFactory.getInstance("RSA")
		return kf.generatePrivate(keySpec)
	}

	private fun decodePublicKey(pemEncoded: String): PublicKey {
		val encodedBytes = toEncodedBytes(pemEncoded)
		val keySpec = X509EncodedKeySpec(encodedBytes)
		val kf = KeyFactory.getInstance("RSA")
		return kf.generatePublic(keySpec)
	}

	private fun toEncodedBytes(pemEncoded: String): ByteArray {
		val normalizedPem = removeBeginEnd(pemEncoded)
		return Base64.getDecoder().decode(normalizedPem)
	}

	private fun removeBeginEnd(pem: String): String {
		var cleanPem = pem
		cleanPem = cleanPem.replace("-----BEGIN (.*)-----".toRegex(), "")
		cleanPem = cleanPem.replace("-----END (.*)----".toRegex(), "")
		cleanPem = cleanPem.replace("\r\n".toRegex(), "")
		cleanPem = cleanPem.replace("\n".toRegex(), "")
		return cleanPem.trim { it <= ' ' }
	}

	fun currentTimeInSecs(): Long {
		TimeZone.setDefault(TimeZone.getTimeZone("MSK"));
		return  Date().toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()*1000
	}

	fun decodeTokenPayload(token: String): JSONObject {
		val rawtokenpayload = token.split(".")[1].replace(".", "")
		val tokenpayload = Base64.getDecoder().decode(rawtokenpayload)
		return JSONParser().parse(String(tokenpayload)) as JSONObject
	}

	fun getUserId(token: String): Long {
		return decodeTokenPayload(token)["sub"].toString().toLong()
	}
}