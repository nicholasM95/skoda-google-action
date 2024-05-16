package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;
import org.springframework.stereotype.Service;

@Service("action.devices.DISCONNECT")
public class DisconnectService implements WebhookService {
    @Override
    public HookWebResponseResource handleAction(InputRequestResource resource) {
        throw new UnsupportedOperationException("UnsupportedOperationException");
    }
}
