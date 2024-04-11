package minlab.imageorder.order.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import minlab.imageorder.common.ResultDTO
import minlab.imageorder.order.application.GetTextOrderService
import minlab.imageorder.order.application.RegisterOrderService
import minlab.imageorder.order.application.TextImageRequestDTO
import minlab.imageorder.order.application.TextImageResponseDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RegisterOrderController(
    private val getTextOrderService: GetTextOrderService,
    private val registerOrderService: RegisterOrderService,
    private val objectMapper: ObjectMapper
) {
    @PostMapping("/text-order")
    fun getTextOrder(@RequestBody textImageRequestDTO: TextImageRequestDTO): ResponseEntity<ResultDTO> {
        val textOrder = getTextOrderService.getTextOrder(textImageRequestDTO)
        val textImageResponseDTO = objectMapper.readValue(textOrder.toString(), TextImageResponseDTO::class.java)


        return ResponseEntity.ok(
            ResultDTO(
                message = "text order success",
                result = textImageResponseDTO
            )
        )
    }

}


