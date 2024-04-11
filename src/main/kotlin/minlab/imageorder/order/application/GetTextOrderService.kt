package minlab.imageorder.order.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class GetTextOrderService(
    private val restClient: RestClient
) {
    @Value("\${naver.clova.X-OCR-URL}")
    private lateinit var clovaUrl: String

    @Value("\${naver.clova.X-OCR-SECRET}")
    private lateinit var clovaKey: String

    fun getTextOrder(textImageRequestDTO: TextImageRequestDTO): Any? {
        val result = postRequest(clovaUrl, clovaKey, textImageRequestDTO).body<Any>()
        return result
    }

    fun postRequest(uri: String, apiKey: String, requestDTO: Any): RestClient.ResponseSpec {
        return restClient.post()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("X-OCR-SECRET", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestDTO)
            .retrieve()
    }
}


data class Image(
    val format: String,
    val name: String,
    val data: String?,
    val url: String
)

data class TextImageRequestDTO(
    val images: List<Image>,
    val lang: String,
    val requestId: String,
    val resultType: String,
    val timestamp: String,
    val version: String
)

data class TextImageResponseDTO(
    val version: String,
    val requestId: String,
    val timestamp: Long,
    val images: List<ImageResult>
)

data class ImageResult(
    val uid: String?,
    val name: String?,
    val inferResult: String?,
    val message: String?,
    val fields: List<Field>?,
    val validationResult: ValidationResult?
)

data class Field(
    val inferText: String?,
    val inferConfidence: Double?
)

data class ValidationResult(
    val result: String?
)
