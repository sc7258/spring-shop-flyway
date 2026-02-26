package com.sc7258.springshopflyway.common.security

import com.fasterxml.jackson.annotation.JsonProperty
import com.sc7258.springshopflyway.common.exception.LoginFailedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
@Profile("!test")
class KeycloakLoginTokenIssuer(
    private val restClientBuilder: RestClient.Builder,
    @Value("\${keycloak.auth-server-url}") private val authServerUrl: String,
    @Value("\${keycloak.realm}") private val realm: String,
    @Value("\${keycloak.client-id}") private val clientId: String,
    @Value("\${keycloak.client-secret}") private val clientSecret: String
) : LoginTokenIssuer {

    override fun issue(email: String, password: String): String {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "password")
            add("client_id", clientId)
            add("client_secret", clientSecret)
            add("username", email)
            add("password", password)
        }

        val tokenResponse = try {
            restClientBuilder
                .baseUrl(authServerUrl)
                .build()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(KeycloakTokenResponse::class.java)
        } catch (_: RestClientException) {
            throw LoginFailedException()
        }

        val accessToken = tokenResponse?.accessToken ?: throw LoginFailedException()
        return accessToken
    }

    private data class KeycloakTokenResponse(
        @JsonProperty("access_token")
        val accessToken: String?
    )
}
