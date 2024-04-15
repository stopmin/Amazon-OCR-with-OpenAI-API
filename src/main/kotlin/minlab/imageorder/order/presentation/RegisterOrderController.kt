package minlab.imageorder.order.presentation

import minlab.imageorder.common.ResultDTO
import minlab.imageorder.common.logInfo
import minlab.imageorder.order.application.*
import minlab.imageorder.order.domain.OrderRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RegisterOrderController(
    private val aiTextOrderService: AITextOrderService,
    private val naverOcrService: NaverOcrService,
    private val orderRepository: OrderRepository
) {
    @PostMapping("/register/image-order")
    suspend fun getTextOrder(@RequestBody registerDTO: RegisterDTO): ResponseEntity<ResultDTO> {
        val ocrImageRequestDTO = OCRImageRequestDTO(
            timestamp = System.currentTimeMillis().toString(),
            images = listOf(
                Image(url = registerDTO.orderImageUrl)
            )
        )

        val ocrTextResponse: OCRTextResponseDTO? = naverOcrService.convertImageToText(ocrImageRequestDTO)
        logInfo("ocrTextResponse : $ocrTextResponse")
        val generateOrder = aiTextOrderService.generateOrder(ocrTextResponse)

        orderRepository.save(generateOrder.mapToDomain())
        return ResponseEntity.ok(
            ResultDTO(
                message = "오더 변환 완료",
                result = generateOrder
            )
        )
    }

}

data class RegisterDTO(
    val orderImageUrl: String,
)
