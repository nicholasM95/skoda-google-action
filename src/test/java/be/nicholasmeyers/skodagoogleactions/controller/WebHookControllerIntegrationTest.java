package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skoda.api.client.VehicleAirConditioningStatus;
import be.nicholasmeyers.skoda.api.client.VehicleRange;
import be.nicholasmeyers.skoda.api.client.VehicleService;
import be.nicholasmeyers.skoda.api.client.VehicleServiceException;
import be.nicholasmeyers.skodagoogleactions.core.security.SecurityConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static be.nicholasmeyers.skoda.api.client.VehicleHeaterSource.ELECTRIC;
import static be.nicholasmeyers.skoda.api.client.VehicleTemperatureUnit.CELSIUS;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.test.json.JsonCompareMode.STRICT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebHookController.class)
@ComponentScan(basePackages = {
        "be.nicholasmeyers.skodagoogleactions.controller",
        "be.nicholasmeyers.skodagoogleactions.service"
})
@Import(SecurityConfig.class)
@WithMockUser
public class WebHookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @Nested
    class Sync {

        @Test
        public void sync() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.SYNC"
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("sync_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }


        @Test
        public void syncInvalidInputSize() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": []
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid input size",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }
    }

    @Nested
    class Query {
        @Test
        public void query() throws Exception {
            VehicleRange vehicleRange = Mockito.mock(VehicleRange.class);
            Mockito.when(vehicleRange.getTotalRangeInKm()).thenReturn(200);
            Mockito.when(vehicleRange.getRemainingRangeInKm()).thenReturn(450);
            Mockito.when(vehicleService.getVehicleRange("QMGAG8BEQSY003476")).thenReturn(vehicleRange);

            VehicleAirConditioningStatus vehicleAirConditioningStatus = Mockito.mock(VehicleAirConditioningStatus.class);
            Mockito.when(vehicleAirConditioningStatus.getState()).thenReturn("OFF");
            Mockito.when(vehicleService.getVehicleAirConditioning("QMGAG8BEQSY003476")).thenReturn(vehicleAirConditioningStatus);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                       {
                                            "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                        },{
                                            "id": "2ec009da-cd6f-4adc-9021-9e7861358408"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("query_1_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void queryAirCoolerOn() throws Exception {
            VehicleRange vehicleRange = Mockito.mock(VehicleRange.class);
            Mockito.when(vehicleRange.getTotalRangeInKm()).thenReturn(200);
            Mockito.when(vehicleRange.getRemainingRangeInKm()).thenReturn(450);
            Mockito.when(vehicleService.getVehicleRange("QMGAG8BEQSY003476")).thenReturn(vehicleRange);

            VehicleAirConditioningStatus vehicleAirConditioningStatus = Mockito.mock(VehicleAirConditioningStatus.class);
            Mockito.when(vehicleAirConditioningStatus.getState()).thenReturn("VENTILATION");
            Mockito.when(vehicleService.getVehicleAirConditioning("QMGAG8BEQSY003476")).thenReturn(vehicleAirConditioningStatus);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                        },{
                                            "id": "2ec009da-cd6f-4adc-9021-9e7861358408"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("query_2_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void queryAirCoolerException() throws Exception {
            VehicleRange vehicleRange = Mockito.mock(VehicleRange.class);
            Mockito.when(vehicleRange.getTotalRangeInKm()).thenReturn(200);
            Mockito.when(vehicleRange.getRemainingRangeInKm()).thenReturn(450);
            Mockito.when(vehicleService.getVehicleRange("QMGAG8BEQSY003476")).thenReturn(vehicleRange);


            Mockito.when(vehicleService.getVehicleAirConditioning("QMGAG8BEQSY003476"))
                    .thenThrow(new VehicleServiceException("", ""));

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                        },{
                                            "id": "2ec009da-cd6f-4adc-9021-9e7861358408"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("query_1_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void queryInvalidPayload() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY"
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid payload",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void queryInvalidKilometer() throws Exception {
            Mockito.when(vehicleService.getVehicleRange("QMGAG8BEQSY003476")).thenThrow(new VehicleServiceException("", ""));

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                       {
                                            "id": "2ec009da-cd6f-4adc-9021-9e7861358408"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Skoda Service unavailable",
                        "status":503,
                        "detail":"Can't get kilometer information",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(content().json(responseBody, STRICT));
        }
    }

    @Nested
    class Execute {

        @Test
        public void executeVentilatorOn() throws Exception {
           /*Mockito.when(carService.startVentilator("QMGAG8BEQSY003476", "1111", 30))
                    .thenReturn("5555");*/

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": [
                                                {
                                                    "command": "action.devices.commands.OnOff",
                                                    "params": {
                                                        "on": true
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("execute_ventilator_on_success_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeVentilatorOnFailure() throws Exception {
            Mockito.doThrow(new VehicleServiceException("", ""))
                    .when(vehicleService)
                    .startVehicleAirConditioning("QMGAG8BEQSY003476", ELECTRIC, 20, CELSIUS);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": [
                                                {
                                                    "command": "action.devices.commands.OnOff",
                                                    "params": {
                                                        "on": true
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("execute_ventilator_on_failure_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeVentilatorOff() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": [
                                                {
                                                    "command": "action.devices.commands.OnOff",
                                                    "params": {
                                                        "on": false
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("execute_ventilator_off_success_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeVentilatorOffFailure() throws Exception {
            Mockito.doThrow(new VehicleServiceException("", ""))
                    .when(vehicleService)
                    .stopVehicleAirConditioning("QMGAG8BEQSY003476");

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": [
                                                {
                                                    "command": "action.devices.commands.OnOff",
                                                    "params": {
                                                        "on": false
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = new String(toByteArray(requireNonNull(
                    this.getClass().getResourceAsStream("execute_ventilator_off_failure_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeInvalidPayload() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE"
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid payload",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeInvalidCommandSize() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": []
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid command size",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeInvalidDeviceSize() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": []
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid devices size",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeInvalidExecutionSize() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": []
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid execution size",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeInvalidCommandAction() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.EXECUTE",
                                "payload": {
                                    "commands": [
                                        {
                                            "devices": [
                                                {
                                                    "id": "b1c18c45-8e42-493c-a3c0-928bd631caf7"
                                                }
                                            ],
                                            "execution": [
                                                {
                                                    "command": "action.devices.commands.OnOff"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"Invalid webhook request",
                        "status":400,
                        "detail":"Invalid command action",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }

    }

    @Nested
    class Disconnect {
        @Test
        public void disconnect() throws Exception {
            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.DISCONNECT"
                            }
                        ]
                    }
                    """;

            String responseBody = """
                    {
                        "title":"UnsupportedOperationException",
                        "status":400,
                        "detail":"UnsupportedOperationException",
                        "instance":"/webhook"
                    }
                    """;

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(responseBody, STRICT));
        }
    }
}
