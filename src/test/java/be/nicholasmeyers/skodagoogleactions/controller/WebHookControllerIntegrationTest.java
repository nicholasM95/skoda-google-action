package be.nicholasmeyers.skodagoogleactions.controller;

import be.nicholasmeyers.skodagoogleactions.client.*;
import be.nicholasmeyers.skodagoogleactions.client.resource.*;
import be.nicholasmeyers.skodagoogleactions.core.security.SecurityConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

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

    @MockBean
    private CoolingClient coolingClient;

    @MockBean
    private FlashClient flashClient;

    @MockBean
    private HonkClient honkClient;

    @MockBean
    private LocationClient locationClient;

    @MockBean
    private RequestClient requestClient;

    @MockBean
    private StatusClient statusClient;

    @MockBean
    private VentilatorClient ventilatorClient;

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
            FieldWebResponseResource field = new FieldWebResponseResource();
            field.setId("0x0301030006");
            field.setValue("450");

            DataWebResponseResource data = new DataWebResponseResource();
            data.setId("0x030103FFFF");
            data.setFields(Collections.singletonList(field));

            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(Collections.singletonList(data));
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);

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
            FieldWebResponseResource field = new FieldWebResponseResource();
            field.setId("0x0301030006");
            field.setValue("450");

            DataWebResponseResource data = new DataWebResponseResource();
            data.setId("0x030103FFFF");
            data.setFields(Collections.singletonList(field));

            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(Collections.singletonList(data));
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);

            ReportWebResponseResource report = new ReportWebResponseResource();
            report.setRemainingClimateTime(20);
            CoolingWebResponseResource cooling  = new CoolingWebResponseResource();
            cooling.setReport(report);
            ResponseEntity<CoolingWebResponseResource> coolingResponseEntity = ResponseEntity.ok(cooling);
            Mockito.when(coolingClient.getCoolingStatus("QMGAG8BEQSY003476")).thenReturn(coolingResponseEntity);

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
            FieldWebResponseResource field = new FieldWebResponseResource();
            field.setId("0x0301030006");
            field.setValue("450");

            DataWebResponseResource data = new DataWebResponseResource();
            data.setId("0x030103FFFF");
            data.setFields(Collections.singletonList(field));

            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(Collections.singletonList(data));
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);

            ReportWebResponseResource report = new ReportWebResponseResource();
            report.setRemainingClimateTime(0);
            CoolingWebResponseResource cooling  = new CoolingWebResponseResource();
            cooling.setReport(report);
            ResponseEntity<CoolingWebResponseResource> coolingResponseEntity = ResponseEntity.ok(cooling);
            Mockito.when(coolingClient.getCoolingStatus("QMGAG8BEQSY003476")).thenReturn(coolingResponseEntity);

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
        public void queryEmptyStatus() throws Exception {
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
                        "detail":"Status is empty",
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
        public void queryEmptyData() throws Exception {
            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(List.of());
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);

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
                        "detail":"Data is not complete",
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
        public void queryEmptyField() throws Exception {
            DataWebResponseResource data = new DataWebResponseResource();
            data.setId("0x030103FFFF");
            data.setFields(List.of());

            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(Collections.singletonList(data));
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);

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
                        "detail":"Fields are not complete",
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
        public void queryInvalidKilometer() throws Exception {
            FieldWebResponseResource field = new FieldWebResponseResource();
            field.setId("0x0301030006");
            field.setValue("ERROR");

            DataWebResponseResource data = new DataWebResponseResource();
            data.setId("0x030103FFFF");
            data.setFields(Collections.singletonList(field));

            StatusWebResponseResource status = new StatusWebResponseResource();
            status.setData(Collections.singletonList(data));
            ResponseEntity<StatusWebResponseResource> statusResponseEntity = ResponseEntity.ok(status);
            Mockito.when(statusClient.getStatus("QMGAG8BEQSY003476")).thenReturn(statusResponseEntity);


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
                        "detail":"Fields are not complete",
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
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

            FlashWebResponseResource flash = new FlashWebResponseResource();
            flash.status("REQUEST_IN_PROGRESS");
            ResponseEntity<FlashWebResponseResource> flashResponseEntity = ResponseEntity.ok(flash);
            FlashWebRequestResource flashWebRequestResource = new FlashWebRequestResource(1, 1, 30);
            Mockito.when(flashClient.flash("QMGAG8BEQSY003476", flashWebRequestResource)).thenReturn(flashResponseEntity);

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
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

            FlashWebResponseResource flash = new FlashWebResponseResource();
            ResponseEntity<FlashWebResponseResource> flashResponseEntity = ResponseEntity.ok(flash);
            FlashWebRequestResource flashWebRequestResource = new FlashWebRequestResource(1, 1, 30);
            Mockito.when(flashClient.flash("QMGAG8BEQSY003476", flashWebRequestResource)).thenReturn(flashResponseEntity);

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
        public void executeSwitch1LocationNotFound() throws Exception {
            LocationWebResponseResource location = new LocationWebResponseResource();
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

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
                        "detail":"Can't find location",
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
        public void executeSwitch1FlashFailed() throws Exception {
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

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
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

            HonkWebResponseResource honk = new HonkWebResponseResource();
            honk.status("REQUEST_IN_PROGRESS");
            ResponseEntity<HonkWebResponseResource> honkResponseEntity = ResponseEntity.ok(honk);
            HonkWebRequestResource honkWebRequestResource = new HonkWebRequestResource(1, 1, 30);
            Mockito.when(honkClient.honk("QMGAG8BEQSY003476", honkWebRequestResource)).thenReturn(honkResponseEntity);

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
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

            HonkWebResponseResource honk = new HonkWebResponseResource();
            ResponseEntity<HonkWebResponseResource> honkResponseEntity = ResponseEntity.ok(honk);
            HonkWebRequestResource honkWebRequestResource = new HonkWebRequestResource(1, 1, 30);
            Mockito.when(honkClient.honk("QMGAG8BEQSY003476", honkWebRequestResource)).thenReturn(honkResponseEntity);

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
        public void executeSwitch2LocationNotFound() throws Exception {
            LocationWebResponseResource location = new LocationWebResponseResource();
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

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
                        "detail":"Can't find location",
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
        public void executeSwitch2HonkFailed() throws Exception {
            LocationWebResponseResource location = new LocationWebResponseResource();
            location.setLatitude(1);
            location.setLongitude(1);
            ResponseEntity<LocationWebResponseResource> locationResponseEntity = ResponseEntity.ok(location);
            Mockito.when(locationClient.getLocation("QMGAG8BEQSY003476")).thenReturn(locationResponseEntity);

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
            VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(30, "1111");
            VentilatorWebResponseResource ventilator = new VentilatorWebResponseResource();
            ventilator.setId("5555");
            ResponseEntity<VentilatorWebResponseResource> ventilatorResponseEntity = ResponseEntity.ok(ventilator);
            Mockito.when(ventilatorClient.startVentilator("QMGAG8BEQSY003476", ventilatorWebRequestResource)).thenReturn(ventilatorResponseEntity);

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
            VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(30, "1111");
            VentilatorWebResponseResource ventilator = new VentilatorWebResponseResource();
            ResponseEntity<VentilatorWebResponseResource> ventilatorResponseEntity = ResponseEntity.ok(ventilator);
            Mockito.when(ventilatorClient.startVentilator("QMGAG8BEQSY003476", ventilatorWebRequestResource)).thenReturn(ventilatorResponseEntity);

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
            VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(0, "1111");
            VentilatorWebResponseResource ventilator = new VentilatorWebResponseResource();
            ventilator.setId("5555");
            ResponseEntity<VentilatorWebResponseResource> ventilatorResponseEntity = ResponseEntity.ok(ventilator);
            Mockito.when(ventilatorClient.stopVentilator("QMGAG8BEQSY003476", ventilatorWebRequestResource)).thenReturn(ventilatorResponseEntity);

            RequestWebResponseResource request = new RequestWebResponseResource();
            request.setStatus("request_successful");
            ResponseEntity<RequestWebResponseResource> requestResponseEntity = ResponseEntity.ok(request);
            Mockito.when(requestClient.getRequest("QMGAG8BEQSY003476", "5555")).thenReturn(requestResponseEntity);

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
            VentilatorWebRequestResource ventilatorWebRequestResource = new VentilatorWebRequestResource(0, "1111");
            VentilatorWebResponseResource ventilator = new VentilatorWebResponseResource();
            ResponseEntity<VentilatorWebResponseResource> ventilatorResponseEntity = ResponseEntity.ok(ventilator);
            Mockito.when(ventilatorClient.stopVentilator("QMGAG8BEQSY003476", ventilatorWebRequestResource)).thenReturn(ventilatorResponseEntity);

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
