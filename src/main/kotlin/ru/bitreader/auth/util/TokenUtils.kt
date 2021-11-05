package ru.bitreader.auth.util

import io.smallrye.jwt.build.Jwt
import ru.bitreader.auth.models.Role
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import kotlin.collections.HashSet


object TokenUtils {
	@Throws(Exception::class)
	fun generateToken(username: String?, roles: Set<Role>, duration: Long, issuer: String?): String {
		val privateKeyLocation = "/privatekey.pem"
		val privateKey = readPrivateKey(privateKeyLocation)
		val claimsBuilder = Jwt.claims()
		val currentTimeInSecs = currentTimeInSecs().toLong()
		val groups: MutableSet<String> = HashSet()
		for (role in roles) groups.add(role.toString())
		claimsBuilder.issuer(issuer)
		claimsBuilder.subject(username)
		claimsBuilder.issuedAt(currentTimeInSecs)
		claimsBuilder.expiresAt(currentTimeInSecs + duration)
		claimsBuilder.groups(groups)
		return claimsBuilder.jws().signatureKeyId(privateKeyLocation).sign(privateKey)
	}

	@Throws(Exception::class)
	fun readPrivateKey(pemResName: String?): PrivateKey {
		TokenUtils::class.java.getResourceAsStream(pemResName).use { contentIS ->
			val tmp = ByteArray(4096)
			val length = contentIS.read(tmp)
			return decodePrivateKey(String(tmp, 0, length, Charsets.UTF_8))
		}
	}

	@Throws(Exception::class)
	fun decodePrivateKey(pemEncoded: String): PrivateKey {
		val encodedBytes = toEncodedBytes(pemEncoded)
		val keySpec = PKCS8EncodedKeySpec(encodedBytes)
		val kf = KeyFactory.getInstance("RSA")
		return kf.generatePrivate(keySpec)
	}

	fun toEncodedBytes(pemEncoded: String): ByteArray {
		val normalizedPem = removeBeginEnd(pemEncoded)
		return Base64.getDecoder().decode(normalizedPem)
	}

	fun removeBeginEnd(pem: String): String {
		var pem = pem
		pem = pem.replace("-----BEGIN (.*)-----".toRegex(), "")
		pem = pem.replace("-----END (.*)----".toRegex(), "")
		pem = pem.replace("\r\n".toRegex(), "")
		pem = pem.replace("\n".toRegex(), "")
		return pem.trim { it <= ' ' }
	}

	fun currentTimeInSecs(): Int {
		val currentTimeMS = System.currentTimeMillis()
		return (currentTimeMS / 1000).toInt()
	}
}