package be.nicholasmeyers.skodagoogleactions.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthController {

    @GetMapping
    public ResponseEntity<Void> auth(@RequestParam Map<String, String> req) {
        HttpHeaders headers = new HttpHeaders();

        String redirectUri = req.get("redirect_uri");
        String state = req.get("state");
        String code = "foobar_code";

        headers.add("Location", redirectUri + "?state=" + state + "&code=" + code);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
