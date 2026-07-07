package dev.danvega.xmcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo 2: the real X API, reached directly over streamable-http at
 * {@code https://api.x.com/mcp} with an app-only Bearer token. No bridge.
 *
 * <p>Hit {@code GET /search?topic=...} and the model calls the X API search tool
 * and reads the live results back. Requires the {@code x-api} connection enabled
 * in application.yaml plus {@code X_BEARER_TOKEN} set (see README).
 */
@RestController
class XApiController {

	private static final Logger log = LoggerFactory.getLogger(XApiController.class);

	private final ChatClient chatClient;

	XApiController(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@GetMapping(value = "/search", produces = "text/plain")
	String search(@RequestParam(defaultValue = "Spring Boot 4.1") String topic) {
		var question = """
                Search X for recent posts about "%s" and give me a quick read on
                what developers are talking about. Pull out a few themes and
                mention anything notable. Keep it tight.
                """.formatted(topic);
		log.info("Asking the X API MCP server (live search) for topic: {}", topic);
		return chatClient.prompt()
				.user(question)
				.call()
				.content();
	}
}
