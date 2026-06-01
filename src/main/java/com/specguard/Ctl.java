package com.specguard;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Ctl {
    private final Eng eng = new Eng();

    @PostMapping("/analyze")
    public ResponseEntity<Res> anl(@RequestBody Map<String, String> bod) {
        String inp = bod.getOrDefault("txt", "");
        if (inp.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Res res = eng.run(inp);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/health")
    public ResponseEntity<String> hlt() {
        return ResponseEntity.ok("OK");
    }
}
