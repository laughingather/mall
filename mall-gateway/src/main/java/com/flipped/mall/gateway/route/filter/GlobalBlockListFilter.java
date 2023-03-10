package com.flipped.mall.gateway.route.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 黑名单过滤器
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Component
@ConditionalOnProperty(value = "mall.gateway.route.enhance.enable", havingValue = "true")
public class GlobalBlockListFilter extends AbstractGatewayFilterFactory<GlobalBlockListFilter.Config> {

    public GlobalBlockListFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String url = exchange.getRequest().getURI().getPath();
            if (config.matchBlockList(url)) {
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private final List<Pattern> blockListUrlPattern = new ArrayList<>();
        private List<String> blockListUrl;

        public boolean matchBlockList(String url) {
            return !blockListUrlPattern.isEmpty() && blockListUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
        }

        public List<String> getBlacklistUrl() {
            return blockListUrl;
        }

        public void setBlacklistUrl(List<String> blockListUrl) {
            this.blockListUrl = blockListUrl;
            this.blockListUrlPattern.clear();
            this.blockListUrl.forEach(url -> this.blockListUrlPattern.add(Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE)));
        }
    }

}