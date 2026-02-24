package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/fornecedores")
public class FornecedorResource {

    @Autowired
    private FornecedorService service;

    @GetMapping
    public ResponseEntity<List<Fornecedor>> findAll() {
        List<Fornecedor> fornecedores = service.findAll();
        return ResponseEntity.ok().body(fornecedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> findById(@PathVariable Long id) {
        Fornecedor fornecedor = service.findById(id);
        return ResponseEntity.ok().body(fornecedor);
    }

    @PostMapping
    public ResponseEntity<Fornecedor> insert(@RequestBody Fornecedor fornecedor) {
        fornecedor = service.insert(fornecedor);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(fornecedor.getId()).toUri();
        return ResponseEntity.created(uri).body(fornecedor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> update(@PathVariable Long id, @RequestBody Fornecedor fornecedor) {
        fornecedor = service.update(id, fornecedor);
        return ResponseEntity.ok().body(fornecedor);
    }
}
