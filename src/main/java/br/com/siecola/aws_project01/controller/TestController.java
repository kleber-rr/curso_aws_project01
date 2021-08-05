package br.com.siecola.aws_project01.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/test")
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name){
        LOG.info("Test DOG controller - name: {}", name);
        return ResponseEntity.ok("Name of dog: " + name);
    }

    @GetMapping("/fish/{name}")
    public ResponseEntity<?> fishTest(@PathVariable String name){
        LOG.info("Test FISH controller - name: {}", name);
        return ResponseEntity.ok("Name of fish: " + name);
    }

    @GetMapping("/cat/{name}")
    public ResponseEntity<?> catTest(@PathVariable String name){
        LOG.info("Test CAT controller - name: {}", name);
        return ResponseEntity.ok("Name of cat: " + name);
    }
}
