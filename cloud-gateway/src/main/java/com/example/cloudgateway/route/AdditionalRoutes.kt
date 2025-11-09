package com.example.cloudgateway.route

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
open class AdditionalRoutes {

    @Value($$"${uri.httpbin:http://httpbin.org:80}")
    var uri: String = ""

    @Bean
    open fun additionalRouteLocator(builder: RouteLocatorBuilder) = builder.routes {

        route(id = "anything-route") {
            order(9999)
            host("*.abc.org") and path("/*")
            filters {
                prefixPath("/anything")
                addResponseHeader("X-TestHeader", "anything")
            }
            uri(uri)
        }

        route(id = "test-kotlin") {
            host("kotlin.abc.org") and path("/anything/kotlinroute")
            filters {
                prefixPath("/httpbin")
                addResponseHeader("X-TestHeader", "kotlin")
            }
            uri(uri)
        }
    }
}