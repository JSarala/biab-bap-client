package org.beckn.one.sandbox.bap.client.fulfillment.track.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.order.quote.services.QuoteService
import org.beckn.one.sandbox.bap.client.shared.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.TrackError
import org.beckn.one.sandbox.bap.client.shared.services.BppService
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TrackService @Autowired constructor(
  private val registryService: RegistryService,
  private val messageService: MessageService,
  private val bppService: BppService,
) {
  private val log: Logger = LoggerFactory.getLogger(QuoteService::class.java)

  fun track(context: ProtocolContext, request: TrackRequestDto): Either<HttpError, MessageDao?> {
    log.info("Got track request. Request: {}", request)

    return validate(request)
      .flatMap { registryService.lookupBppById(request.context.bppId!!) }
      .flatMap { Either.Right(it.first()) }
      .flatMap { bppService.track(it.subscriber_url, context, request) }
      .flatMap { messageService.save(MessageDao(id = context.messageId, type = MessageDao.Type.Track)) }
  }

  private fun validate(request: TrackRequestDto): Either<TrackError, Nothing?> =
    when (request.context.bppId) {
      null -> Either.Left(TrackError.BppIdNotPresent)
      else -> Either.Right(null)
    }
}