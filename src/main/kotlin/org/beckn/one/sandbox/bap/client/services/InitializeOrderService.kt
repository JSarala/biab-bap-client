package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.DeliveryInfoDto
import org.beckn.one.sandbox.bap.client.dtos.OrderDto
import org.beckn.one.sandbox.bap.client.dtos.OrderItemDto
import org.beckn.one.sandbox.bap.client.errors.validation.MultipleProviderError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.schemas.ProtocolBilling
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectMessageSelectedProviderLocations
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InitializeOrderService @Autowired constructor(
  private val messageService: MessageService,
  private val bppService: BppService,//todo: might require a mapper
  private val registryService: RegistryService,
  private val log: Logger = LoggerFactory.getLogger(InitializeOrderService::class.java)
) {
  fun initOrder(
    context: ProtocolContext,
    order: OrderDto,
    deliveryInfo: DeliveryInfoDto,
    billingInfo: ProtocolBilling
  ): Either<HttpError, MessageDao?> {
    log.info("Got initialize order request. Context: {}, Order: {}, Delivery Info: {}", context, order, deliveryInfo)
    if (order.items.isNullOrEmpty()) {
      log.info("Empty order received, no op. Order: {}", order)
      return Either.Right(null)
    }

    if (areMultipleBppItemsSelected(order.items)) {
      log.info("Order contains items from more than one BPP, returning error. Order: {}", order)
      return Either.Left(MultipleProviderError.MultipleBpps)
    }

    if (areMultipleProviderItemsSelected(order.items)) {
      log.info("Order contains items from more than one provider, returning error. Cart: {}", order)
      return Either.Left(MultipleProviderError.MultipleProviders)
    }

    return registryService.lookupBppById(order.items.first().bppId)
      .flatMap {
        bppService.init(
          context,
          bppUri = it.first().subscriber_id,
          providerId = order.items.first().provider.id,
          billingInfo = billingInfo,
          items = order.items,
          providerLocation = ProtocolSelectMessageSelectedProviderLocations(id = order.items.first().provider.locations!!.first()), //todo: is this for sure non nullable?
          deliveryInfo = deliveryInfo
        )
      }
      .flatMap {
        messageService.save(MessageDao(id = context.messageId, type = MessageDao.Type.Init))
      }

  }

  private fun areMultipleProviderItemsSelected(items: List<OrderItemDto>): Boolean =
    items.distinctBy { it.provider.id }.size > 1

  private fun areMultipleBppItemsSelected(items: List<OrderItemDto>): Boolean =
    items.distinctBy { it.bppId }.size > 1

}