package minlab.imageorder.order.application

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import minlab.imageorder.order.domain.Order
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AITextOrderService(
    private val openAI: OpenAI,
    private val objectMapper: ObjectMapper
) {
    suspend fun generateOrder(
        ocrTextResponse: OCRTextResponseDTO?
    ): GptOrderResponseDTO {

        val content = """
  너는 배달 플랫폼을 운영하는 상담원이다.
  배달 운송에 대한 정보를 채팅을 통해 제공받으면, 정해진 json 형식에 맞게 데이터를 출력한다.
  인간 관리자의 수고를 덜기 위해서 네가 도움을 주는 것이기 때문에 잘못되거나 불완전한 데이터라면 오히려 관리자에게 피해가 되니, 차라리 넣지 않는게 옳다.
  해당 데이터는 카카오톡이라는 채팅 어플의 캡쳐본을 OCR을 통해 추출한 텍스트 데이터라서, 카카오톡 서비스는 채팅을 수신한 시간이 찍히기 때문에 OCR에 가끔 수신한 시간이 같이 추출되기도 한다.
  이는 유동적으로 변할 수 있기 때문에, 해당 데이터는 무시하고, 채팅 내용만을 분석하여 json 형식에 맞게 데이터를 출력해야 한다.
  
  다음은 json 형식이다. 만약 해당 형식에 맞는 데이터가 감지되지 않는다면 null을 넣어도 된다.
  
  ####json 형식####
  {
    "start_load_address": "string",
    "end_load_address": "string",
    "description": "string"
    "start_load_time": "LocalDateTime",
    "end_load_time": "LocalDateTime",
  } 
  
  다음은, 트럭 운송에 대한 러프한 정보다. 
  만약 운송이 여러개일 경우 모든 정보를 여러개의 json에 걸쳐 출력해야 한다.
  
  #### 채팅을 통해 수집한 배달 정보 ####
                    """.trimIndent()
            .plus(ocrTextResponse!!.mapToText())


        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = content
                ),
            )
        )
        val completions: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val jsonString: String = completions.choices.firstOrNull()?.message?.content.orEmpty()


        // TODO: Result Validation
        val order: GptOrderResponseDTO = objectMapper.readValue(jsonString)
        return order
    }
}

data class GptOrderResponseDTO @JsonCreator constructor(
    @JsonProperty("start_load_address") val startLoadAddress: String,
    @JsonProperty("end_load_address") val endLoadAddress: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("start_load_time") val startLoadTime: LocalDateTime,
    @JsonProperty("end_load_time") val endLoadTime: LocalDateTime
) {
    fun mapToDomain(): Order {
        return Order(
            startLoadAddress = startLoadAddress,
            endLoadAddress = endLoadAddress,
            description = description,
            startLoadTime = startLoadTime,
            endLoadTime = endLoadTime
        )
    }
}


