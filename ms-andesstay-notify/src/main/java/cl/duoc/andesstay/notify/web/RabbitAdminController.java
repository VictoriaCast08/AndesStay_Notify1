package cl.duoc.andesstay.notify.web;

import cl.duoc.andesstay.notify.dto.CreateQueueRequest;
import cl.duoc.andesstay.notify.service.RabbitAdminService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/queues")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createQueue(@Valid @RequestBody CreateQueueRequest request) {
        rabbitAdminService.createQueue(request.getName(), request.isDurable());
        return ResponseEntity.ok(Map.of("message", "Cola '" + request.getName() + "' creada correctamente"));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> deleteQueue(@PathVariable String name) {
        boolean deleted = rabbitAdminService.deleteQueue(name);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Cola '" + name + "' eliminada correctamente"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "No se pudo eliminar la cola '" + name + "'"));
    }
}