package com.xjinyao.xcloud.common.swagger.support;

import com.xjinyao.xcloud.common.swagger.config.SwaggerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Primary
@Component
@RequiredArgsConstructor
public class SwaggerProvider implements SwaggerResourcesProvider {

    private static final String API_URI = "/v2/api-docs";

    private final SwaggerProperties swaggerProperties;

    private final GatewayProperties gatewayProperties;

    @Lazy
    @Autowired
    private RouteLocator routeLocator;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
                        .filter(predicateDefinition -> !swaggerProperties.getIgnoreProviders()
                                .contains(routeDefinition.getId().replaceAll("\\(.*\\)", "")))
                        .forEach(predicateDefinition -> addResources(resources, routeDefinition, predicateDefinition)));
        return resources;
    }

    private void addResources(List<SwaggerResource> resources, RouteDefinition routeDefinition, PredicateDefinition predicateDefinition) {
        Map<String, String> args = predicateDefinition.getArgs();
        if (CollectionUtils.isEmpty(args)) {
            return;
        }
        args.values()
                .parallelStream()
                .findFirst()
                .ifPresent(arg -> resources.add(swaggerResource(routeDefinition.getId(), arg.replace("/**", API_URI))));
    }

    private static SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

}
