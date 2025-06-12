package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skoda.api.client.CarCoolingInfo;
import be.nicholasmeyers.skoda.api.client.CarReport;
import be.nicholasmeyers.skoda.api.client.CarService;
import be.nicholasmeyers.skoda.api.client.CarServiceException;
import be.nicholasmeyers.skoda.api.client.CarStatus;
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
    private CarService carService;

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
            CarStatus carStatus = Mockito.mock(CarStatus.class);
            Mockito.when(carStatus.getKilometer()).thenReturn(450);
            Mockito.when(carService.getStatus("QMGAG8BEQSY003476")).thenReturn(carStatus);

            CarReport carReport = Mockito.mock(CarReport.class);
            Mockito.when(carReport.getRemainingClimateTime()).thenReturn(0);

            CarCoolingInfo carCoolingInfo = Mockito.mock(CarCoolingInfo.class);
            Mockito.when(carCoolingInfo.getReport()).thenReturn(carReport);

            Mockito.when(carService.getCooling("QMGAG8BEQSY003476")).thenReturn(carCoolingInfo);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
                                        },{
                                            "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
                                        },{
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
            CarStatus carStatus = Mockito.mock(CarStatus.class);
            Mockito.when(carStatus.getKilometer()).thenReturn(450);
            Mockito.when(carService.getStatus("QMGAG8BEQSY003476")).thenReturn(carStatus);

            CarReport carReport = Mockito.mock(CarReport.class);
            Mockito.when(carReport.getRemainingClimateTime()).thenReturn(20);

            CarCoolingInfo carCoolingInfo = Mockito.mock(CarCoolingInfo.class);
            Mockito.when(carCoolingInfo.getReport()).thenReturn(carReport);

            Mockito.when(carService.getCooling("QMGAG8BEQSY003476")).thenReturn(carCoolingInfo);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
                                        },{
                                            "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
                                        },{
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
        public void queryAirCoolerOff() throws Exception {
            CarStatus carStatus = Mockito.mock(CarStatus.class);
            Mockito.when(carStatus.getKilometer()).thenReturn(450);
            Mockito.when(carService.getStatus("QMGAG8BEQSY003476")).thenReturn(carStatus);

            CarReport carReport = Mockito.mock(CarReport.class);
            Mockito.when(carReport.getRemainingClimateTime()).thenReturn(0);

            CarCoolingInfo carCoolingInfo = Mockito.mock(CarCoolingInfo.class);
            Mockito.when(carCoolingInfo.getReport()).thenReturn(carReport);

            Mockito.when(carService.getCooling("QMGAG8BEQSY003476")).thenReturn(carCoolingInfo);

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
                                        },{
                                            "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
                                        },{
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
        public void queryAirCoolerException() throws Exception {
            CarStatus carStatus = Mockito.mock(CarStatus.class);
            Mockito.when(carStatus.getKilometer()).thenReturn(450);
            Mockito.when(carService.getStatus("QMGAG8BEQSY003476")).thenReturn(carStatus);

            CarReport carReport = Mockito.mock(CarReport.class);
            Mockito.when(carReport.getRemainingClimateTime()).thenReturn(0);

            CarCoolingInfo carCoolingInfo = Mockito.mock(CarCoolingInfo.class);
            Mockito.when(carCoolingInfo.getReport()).thenReturn(carReport);

            Mockito.when(carService.getCooling("QMGAG8BEQSY003476"))
                    .thenThrow(new CarServiceException("", ""));

            String requestBody = """
                    {
                        "requestId": "123",
                        "inputs": [
                            {
                                "intent": "action.devices.QUERY",
                                "payload": {
                                    "devices": [
                                        {
                                            "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
                                        },{
                                            "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
                                        },{
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
            Mockito.when(carService.getStatus("QMGAG8BEQSY003476")).thenThrow(new CarServiceException("", ""));

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
        public void executeSwitch1() throws Exception {
            Mockito.when(carService.flash("QMGAG8BEQSY003476", 30)).thenReturn("REQUEST_IN_PROGRESS");

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
                                                    "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
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
                    this.getClass().getResourceAsStream("execute_switch_1_success_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeSwitch1Failure() throws Exception {
            Mockito.when(carService.flash("QMGAG8BEQSY003476", 30)).thenReturn(null);

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
                                                    "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
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
                    this.getClass().getResourceAsStream("execute_switch_1_failure_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeSwitch1FlashFailed() throws Exception {
            Mockito.when(carService.flash("QMGAG8BEQSY003476", 30))
                    .thenThrow(new CarServiceException("", ""));

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
                                                    "id": "6abb7eaa-08a8-44c0-83a7-9c3c658bd63e"
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

            String responseBody = """
                    {
                        "title":"Skoda Service unavailable",
                        "status":503,
                        "detail":"Can't flash lights",
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

        @Test
        public void executeSwitch2() throws Exception {
            Mockito.when(carService.honk("QMGAG8BEQSY003476", 30)).thenReturn("REQUEST_IN_PROGRESS");

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
                                                    "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
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
                    this.getClass().getResourceAsStream("execute_switch_2_success_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeSwitch2Failure() throws Exception {
            Mockito.when(carService.honk("QMGAG8BEQSY003476", 30)).thenReturn(null);

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
                                                    "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
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
                    this.getClass().getResourceAsStream("execute_switch_2_failure_response.json"))
            ));

            mockMvc.perform(post("/webhook")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().json(responseBody, STRICT));
        }

        @Test
        public void executeSwitch2HonkFailed() throws Exception {
            Mockito.when(carService.honk("QMGAG8BEQSY003476", 30))
                    .thenThrow(new CarServiceException("", ""));

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
                                                    "id": "883f8b70-1649-41f2-8a53-b41df7214f4a"
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

            String responseBody = """
                    {
                        "title":"Skoda Service unavailable",
                        "status":503,
                        "detail":"Can't honk",
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


        @Test
        public void executeVentilatorOn() throws Exception {
           Mockito.when(carService.startVentilator("QMGAG8BEQSY003476", "1111", 30))
                    .thenReturn("5555");

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
            Mockito.when(carService.startVentilator("QMGAG8BEQSY003476", "1111", 30)).thenReturn(null);

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
            Mockito.when(carService.stopVentilator("QMGAG8BEQSY003476", "1111")).thenReturn("5555");
            Mockito.when(carService.getRequest("QMGAG8BEQSY003476", "5555")).thenReturn("request_successful");

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
            Mockito.when(carService.stopVentilator("QMGAG8BEQSY003476", "1111")).thenReturn(null);

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
