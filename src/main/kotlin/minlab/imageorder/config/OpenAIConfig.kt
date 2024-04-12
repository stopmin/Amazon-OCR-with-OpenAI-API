package minlab.imageorder.config

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

@Configuration
class OpenAIConfig {
    @Value("\${openAI.SECRET-KEY}")
    private lateinit var apiKey: String

    @Bean
    fun OpenAI(): OpenAI {
        val config = OpenAIConfig(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds),
            host = OpenAIHost.OpenAI

        )
        return OpenAI(config)
    }
}

