package be.nicholasmeyers.skodagoogleactions.service;

import be.nicholasmeyers.skodagoogleactions.resource.request.InputRequestResource;
import be.nicholasmeyers.skodagoogleactions.resource.response.HookWebResponseResource;

public interface WebhookService {
    HookWebResponseResource handleAction(InputRequestResource resource);
}
