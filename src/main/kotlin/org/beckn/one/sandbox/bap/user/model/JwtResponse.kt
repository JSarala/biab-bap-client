package org.beckn.one.sandbox.bap.user.model

import java.io.Serializable

class JwtResponse(val token: String) : Serializable {

    companion object {
        private const val serialVersionUID = -8091879091924046844L
    }
}