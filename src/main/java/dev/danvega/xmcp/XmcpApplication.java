package dev.danvega.xmcp;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class XmcpApplication {

	static void main(String[] args) {
		SpringApplication.run(XmcpApplication.class, args);
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider tools) {
		return builder.defaultTools(tools).build();
	}
}
