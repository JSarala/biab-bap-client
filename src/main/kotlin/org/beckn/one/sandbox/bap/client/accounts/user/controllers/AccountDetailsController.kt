package org.beckn.one.sandbox.bap.client.accounts.user.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.accounts.user.services.AccountDetailsServices
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.message.entities.AccountDetailsDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.util.*

@RestController
class AccountDetailsController @Autowired constructor(
  private val accountDetailService: AccountDetailsServices,
  private val responseStorageService: ResponseStorageService<AccountDetailsResponse,AccountDetailsDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/account_details")
  @ResponseBody
  fun updateAccountDetails(@RequestBody request: AccountDetailsDao): ResponseEntity<AccountDetailsResponse> {
  val user = SecurityUtil.getSecuredUserDetail()
  request.userId = user?.uid
    if(user?.uid != null){
      return  responseStorageService
        .updateOneById(request)
        .fold(
          {
            log.error("Error when saving address response by user id. Error: {}", it)
            ResponseEntity
              .status(it.status())
              .body(AccountDetailsResponse(userId = null,context = null,error = ProtocolError(code = it.status().name, message = it.message().toString())))
          },
          {
            log.info("Updated address details of user {}")
            ResponseEntity.ok(it)
          }
        )
    }else{
    return  ResponseEntity
        .status(HttpStatus.UNAUTHORIZED.value())
        .body(AccountDetailsResponse(userId = null,context = null,error = ProtocolError(code = "401", message = "Unauthorized access of protected resource, invalid credentials")))
    }
  }
}