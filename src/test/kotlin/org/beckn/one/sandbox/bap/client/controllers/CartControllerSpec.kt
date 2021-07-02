package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.dtos.Cart
import org.beckn.one.sandbox.bap.client.dtos.CartItem
import org.beckn.one.sandbox.bap.client.dtos.CartItemProvider
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
  init {

    describe("Cart") {
      it("should return acknowledgement response when create is called") {
        val cartId = "cart 1"
        val getCartResponse = mockMvc
          .perform(
            get("/client/v0/cart")
              .param("id", cartId)
          )
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val cart = objectMapper.readValue(getCartResponse, Cart::class.java)
        cart shouldBe Cart(
          id = cartId, items = listOf(
            CartItem(
              bppId = "paisool",
              provider = CartItemProvider(
                id = "venugopala stores",
                providerLocations = listOf("13.001581,77.5703686")
              ),
              itemId = "cothas-coffee-1",
              quantity = 2,
              measure = ProtocolScalar(
                value = BigDecimal.valueOf(500),
                unit = "gm"
              )
            ),
            CartItem(
              bppId = "paisool",
              provider = CartItemProvider(
                id = "maruthi-stores",
                providerLocations = listOf("12.9995218,77.5704439")
              ),
              itemId = "malgudi-coffee-500-gms",
              quantity = 1,
              measure = ProtocolScalar(
                value = BigDecimal.valueOf(1),
                unit = "kg"
              )
            )
          )
        )
      }
    }
  }
}