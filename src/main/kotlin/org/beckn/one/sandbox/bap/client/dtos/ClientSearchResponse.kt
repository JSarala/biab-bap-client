package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.schemas.Error
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog

interface ClientResponse {
  val context: Context
  val error: Error?
}

data class ClientSearchResponse(
  override val context: Context,
  val message: ClientSearchResponseMessage? = null,
  override val error: Error? = null,
) : ClientResponse

data class ClientSearchResponseMessage(
  val catalogs: List<ProtocolCatalog>? = null,
)