package org.beckn.one.sandbox.bap.client.shared.security

import java.time.Instant

data class Authorization(
  val keyId: String,
  val algorithm: String = XED25519,
  val created: Long,
  val expires: Long,
  val headers: String = "(created) (expires) digest",
  val signature: String
) {

  fun isNotExpired() = Instant.now().toEpochMilli() / 1000 < expires

  val headerString by lazy {
    """Signature keyId="$keyId" algorithm="$algorithm" created="$created" expires="$expires" headers="$headers" signature="$signature""""
  }

  val parseKey by lazy {
    val keyComponents = keyId.trim().split("|")
    Triple(keyComponents[0], keyComponents[1], keyComponents[2])
  }

  companion object {
    private const val KEY_ID = "keyId"
    private const val ALGORITHM = "algorithm"
    private const val CREATED = "created"
    private const val EXPIRES = "expires"
    private const val HEADERS = "headers"
    private const val SIGNATURE = "signature"

    private const val XED25519 = "xed25519"
    const val HEADER_NAME = "Authorization"

    fun parse(auth: String?): Authorization? {
      return auth?.let { authStr ->
        val authParams = authStr.trim()
          .removePrefix("Signature ")
          .split("\" ")
          .associate {
            val keyValue = it.trim().split("=\"", limit = 2)
            Pair(keyValue[0], keyValue[1].removeSuffix("\""))
          }

        val keyId = authParams[KEY_ID] ?: return null
        val algorithm = authParams[ALGORITHM] ?: return null
        val created = authParams[CREATED]?.toLong() ?: return null
        val expires = authParams[EXPIRES]?.toLong() ?: return null
        val headers = authParams[HEADERS] ?: return null
        val signature = authParams[SIGNATURE] ?: return null

        Authorization(
          keyId = keyId,
          algorithm = algorithm,
          created = created,
          expires = expires,
          headers = headers,
          signature = signature
        )
      }
    }

    fun create(subscriberId: String,
               uniqueKeyId: String,
               signature: String,
               created: Long,
               expires: Long): Authorization {
      return Authorization(
        keyId = "$subscriberId|$uniqueKeyId|$XED25519",
        created = created,
        expires =  expires,
        signature = signature
      )
    }
  }
}