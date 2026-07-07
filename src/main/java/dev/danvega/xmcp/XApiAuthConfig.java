package dev.danvega.xmcp;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Adds the X API app-only Bearer token to MCP requests bound for api.x.com.
 *
 * <p>Spring AI 2.0 has no per-connection headers property, so the header goes on
 * via an {@link McpClientCustomizer}. Matching on the destination host keeps the
 * token off the no-auth docs.x.com connection and works regardless of how the
 * connection is named.
 */
@Configuration
class XApiAuthConfig {

	@Bean
	McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> xApiBearerAuth(@Value("${X_BEARER_TOKEN:}") String token) {
		McpSyncHttpClientRequestCustomizer addBearer = (request, method, uri, endpoint, context) -> {
			if (StringUtils.hasText(token) && "api.x.com".equals(uri.getHost())) {
				request.header("Authorization", "Bearer " + token);
			}
		};
		return (name, builder) -> builder.httpRequestCustomizer(addBearer);
	}
}
