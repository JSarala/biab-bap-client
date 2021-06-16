package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.constants.ProtocolVersion
import org.beckn.one.sandbox.bap.dtos.Action
import org.beckn.one.sandbox.bap.dtos.Context
import java.time.Clock

class ContextFactory {
  companion object {
    fun getDefaultContext(clock: Clock = Clock.systemUTC()) = Context(
      domain = Domain.LocalRetail.value,
      country = Country.India.value,
      city = City.Bengaluru.value,
      action = Action.search,
      coreVersion = ProtocolVersion.V0_9_1.value,
      bapId = "beckn_in_a_box_bap",
      bapUri = "beckn_in_a_box_bap.com",
      clock = clock
    )
  }
}