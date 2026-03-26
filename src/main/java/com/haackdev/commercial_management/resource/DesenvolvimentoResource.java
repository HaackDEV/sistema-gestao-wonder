package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.Desenvolvimento;
import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.service.DesenvolvimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Desenvolvimentos", description = "Endpoints para gerenciamento de desenvolvimentos")
@RestController
@RequestMapping("/desenvolvimentos")
public class DesenvolvimentoResource {

    @Autowired
    private DesenvolvimentoService service;

    @Operation(summary = "Busca todos os desenvolvimentos", description = "Retorna uma lista de todos os desenvolvimentos (potenciais pedidos) cadastrados.")
    @GetMapping
    public ResponseEntity<List<Desenvolvimento>> findAll() {
        List<Desenvolvimento> desenvolvimentos = service.findAll();
        return ResponseEntity.ok().body(desenvolvimentos);
    }

    @Operation(summary = "Busca um desenvolvimento por ID", description = "Recupera os detalhes de um desenvolvimento específico utilizando o seu ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Desenvolvimento> findById(@PathVariable Long id) {
        Desenvolvimento desenvolvimento = service.findById(id);
        return ResponseEntity.ok().body(desenvolvimento);
    }

    @Operation(summary = "Insere um novo desenvolvimento", description = "Cadastra um novo desenvolvimento na base de dados com as informações providenciadas.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<Desenvolvimento> insert(@RequestBody Desenvolvimento desenvolvimento) {
        desenvolvimento = service.insert(desenvolvimento);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(desenvolvimento.getId()).toUri();
        return ResponseEntity.created(uri).body(desenvolvimento);
    }

    @Operation(summary = "Deleta um desenvolvimento por ID", description = "Remove permanentemente um registro de desenvolvimento do banco de dados pelo seu ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um desenvolvimento por ID", description = "Altera as propriedades e o status de um desenvolvimento existente.")
    @PutMapping("/{id}")
    public ResponseEntity<Desenvolvimento> update(@PathVariable Long id, @RequestBody Desenvolvimento desenvolvimento) {
        desenvolvimento = service.update(id, desenvolvimento);
        return ResponseEntity.ok().body(desenvolvimento);
    }

    @Operation(summary = "Converte um desenvolvimento em pedido", description = "Cria um pedido com os dados do desenvolvimento e marca o desenvolvimento como concluído")
    @PostMapping("/{id}/converter-em-pedido")
    public ResponseEntity<Pedido> converterEmPedido(@PathVariable Long id) {
        Pedido pedido = service.converterEmPedido(id);
        return ResponseEntity.ok().body(pedido);
    }
}
