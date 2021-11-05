package ru.bitreader.auth.util

import org.eclipse.microprofile.config.inject.ConfigProperty
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.RequestScoped
import javax.inject.Inject


@ApplicationScoped
class PBKDF2Encoder {
	@ConfigProperty(name = "ru.bitreader.auth.password.secret")
	private lateinit var secret: String

	@ConfigProperty(name = "ru.bitreader.auth.password.iteration")
	private lateinit var iteration: String

	@ConfigProperty(name = "ru.bitreader.auth.password.keylength")
	private lateinit var keylength: String

	/**
	 * More info (https://www.owasp.org/index.php/Hashing_Java) 404 :(
	 * @param cs password
	 * @return encoded password
	 */
	fun encode(cs: CharSequence): String {
		return try {
			val result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
				.generateSecret(
					PBEKeySpec(
						cs.toString().toCharArray(),
						secret.toByteArray(),
						iteration.toInt(),
						keylength.toInt()
					)
				)
				.encoded
			Base64.getEncoder().encodeToString(result)
		} catch (ex: NoSuchAlgorithmException) {
			throw RuntimeException(ex)
		} catch (ex: InvalidKeySpecException) {
			throw RuntimeException(ex)
		}
	}
}