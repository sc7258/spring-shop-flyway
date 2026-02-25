package com.sc7258.springshopflyway.common.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class KeycloakJwtRolesConverter(
    private val clientId: String
) : Converter<Jwt, Collection<GrantedAuthority>> {

    @Suppress("UNCHECKED_CAST")
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.claims["resource_access"] as? Map<String, Any> ?: return emptyList()
        val clientAccess = resourceAccess[clientId] as? Map<String, Any> ?: return emptyList()
        val roles = clientAccess["roles"] as? List<String> ?: return emptyList()

        return roles.map { role ->
            SimpleGrantedAuthority("ROLE_${role.uppercase()}")
        }
    }
}
