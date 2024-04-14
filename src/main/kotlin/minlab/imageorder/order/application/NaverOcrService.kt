package minlab.imageorder.order.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class NaverOcrService(
    private val restClient: RestClient
) {
    @Value("\${naver.clova.X-OCR-URL}")
    private lateinit var clovaUrl: String

    @Value("\${naver.clova.X-OCR-SECRET}")
    private lateinit var clovaKey: String


    fun convertImageToText(ocrImageRequestDTO: OCRImageRequestDTO): OCRTextResponseDTO? {
        val result = postRequest(clovaUrl, clovaKey, ocrImageRequestDTO).body<OCRTextResponseDTO>()
        return result
    }


    private fun postRequest(uri: String, apiKey: String, requestDTO: Any): RestClient.ResponseSpec {
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
    val format: String = "png",
    val name: String = "medium",
    val data: String? = null,
    val url: String
)

data class OCRImageRequestDTO(
    val images: List<Image>,
    val lang: String = "ko",
    val requestId: String = "string",
    val resultType: String = "string",
    val timestamp: String,
    val version: String = "V2"
)

data class OCRTextResponseDTO(
    val version: String,
    val requestId: String,
    val timestamp: Long,
    val images: List<ImageResult>
) {
    fun mapToText(): String {
//        return images.flatMap { it.fields?.map { field -> field.inferText } }
        return images.joinToString("\n") {
            it.inferResult ?: ""
        }
    }
}

data class ImageResult(
    val uid: String?,
    val name: String?,
    val inferResult: String?,
    val message: String?,
    val validationResult: ValidationResult?,
    val fields: List<Field>?,
)

data class Field(
    val valueType: String?,
    val boundingPoly: BoundingPoly?,
    val inferText: String?,
    val inferConfidence: Double?
)

data class BoundingPoly(
    val vertices: List<Vertex>?
)

data class Vertex(
    val x: Int,
    val y: Int
)

data class ValidationResult(
    val result: String?
)

