package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.one.sandbox.bap.Default

data class SearchRequestDto @Default constructor(
  val context: ClientContext,
  val message: SearchRequestMessageDto,
)

data class SearchRequestMessageDto @Default constructor(
  val criteria: SearchCriteria
)

data class SearchCriteria @Default constructor(
  val searchString: String? = null,
  val location: String? = null,
  val providerId: String? = null,
  val categoryId: String? = null,
)
