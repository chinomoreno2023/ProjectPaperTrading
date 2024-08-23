package options.papertrading.controllers.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("trick")
public class TrickController {

    @GetMapping
    public ResponseEntity<String> openStartPage() {
        return ResponseEntity.ok("Open start page");
    }
}