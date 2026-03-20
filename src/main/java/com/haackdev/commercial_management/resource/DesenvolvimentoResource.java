package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.Desenvolvimento;
import com.haackdev.commercial_management.service.DesenvolvimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/desenvolvimentos")
public class DesenvolvimentoResource {

    @Autowired
    private DesenvolvimentoService service;

    @GetMapping
    public ResponseEntity<List<Desenvolvimento>> findAll() {
        List<Desenvolvimento> desenvolvimentos = service.findAll();
        return ResponseEntity.ok().body(desenvolvimentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Desenvolvimento> findById(@PathVariable Long id) {
        Desenvolvimento desenvolvimento = service.findById(id);
        return ResponseEntity.ok().body(desenvolvimento);
    }

    @PostMapping
    public ResponseEntity<Desenvolvimento> insert(@RequestBody Desenvolvimento desenvolvimento) {
        desenvolvimento = service.insert(desenvolvimento);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(desenvolvimento.getId()).toUri();
        return ResponseEntity.created(uri).body(desenvolvimento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Desenvolvimento> update(@PathVariable Long id, @RequestBody Desenvolvimento desenvolvimento) {
        desenvolvimento = service.update(id, desenvolvimento);
        return ResponseEntity.ok().body(desenvolvimento);
    }
}
