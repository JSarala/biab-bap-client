package org.beckn.one.sandbox.bap.client.accounts.address.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.message.entities.DeliveryAddressDao
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.newId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AddressServices @Autowired constructor(
  private val addressRepository: GenericRepository<AddDeliveryAddressDao>,
  private val responseStorageService: ResponseStorageService<DeliveryAddressResponse, AddDeliveryAddressDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun save(address: DeliveryAddressRequestDto): Either<HttpError, AddDeliveryAddressDao> {
    val user = SecurityUtil.getSecuredUserDetail()

    return Either
      .catch { addressRepository.insertOne(AddDeliveryAddressDao(
        userId = user?.uid,
        id =  newId<String>().toString(),
        descriptor = address.descriptor,
        gps = address.gps,
        default = address.default,
        address = address.address)) }
      .mapLeft { e ->
        log.error("Error when saving message to DB", e)
        DatabaseError.OnWrite
      }.map {
        it
      }

  }

  fun findAddressesForCurrentUser(
    userId: String
  ): ResponseEntity<List<DeliveryAddressResponse>> = responseStorageService
    .findManyByUserId(userId,0,0)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        mapToErrorResponse(it)
      },
      {
        log.info("Found responses for address {}", userId)
        ResponseEntity.ok(it)
      }
    )

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      listOf(DeliveryAddressResponse(
        id = null,
        userId = null,
        context = null,
        error = it.error()
      )
    ))
}
