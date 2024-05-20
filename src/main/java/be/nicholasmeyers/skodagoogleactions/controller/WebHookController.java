package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skodagoogleactions.exception.WebHookInputException;
import be.nicholasmeyers.skodagoogleactions.resource.request.HookRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import be.nicholasmeyers.skodagoogleactions.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/webhook")
@RestController
public class WebHookController {

    private final Map<String, WebhookService> webhookServiceMap;

    @PostMapping
    public ResponseEntity<HookWebResponseResource> webhook(@RequestBody HookRequestResource resource) {
        addMDCContext(resource);
        if (resource.inputs().size() == 1) {
            return ResponseEntity.ok(getService(resource.inputs().getFirst().intent()).handleAction(resource.inputs().getFirst()));
        }
        throw new WebHookInputException("Invalid input size");
    }

    private WebhookService getService(String intent) {
        log.info("Get service class for: {}", intent);
        return webhookServiceMap.get(intent);
    }

    private void addMDCContext(HookRequestResource resource) {
        String device = "EMPTY";
        if ("action.devices.QUERY".equals(resource.inputs().getFirst().intent())) {
            device = resource.inputs().getFirst().payload().devices().toString();
        } else if ("action.devices.EXECUTE".equals(resource.inputs().getFirst().intent())) {
            device = resource.inputs().getFirst().payload().commands().getFirst().devices().getFirst().id().toString();
        }
        MDC.put("action", resource.inputs().getFirst().intent());
        MDC.put("device", device);
    }
}
