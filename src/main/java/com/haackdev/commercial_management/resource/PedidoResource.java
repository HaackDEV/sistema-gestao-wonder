package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
@RestController
@RequestMapping(value = "/pedidos")
public class PedidoResource {

    @Autowired
    private PedidoService service;

    @Operation(summary = "Busca todos os pedidos", description = "Recupera uma relação com todos os pedidos finalizados e/ou em andamento no sistema.")
    @GetMapping
    public ResponseEntity<List<Pedido>> findAll() {
        List<Pedido> pedidos = service.findAll();
        return ResponseEntity.ok().body(pedidos);
    }

    @Operation(summary = "Busca um pedido por ID", description = "Acessa todos os dados de um pedido e também a sua lista inteira de itens detalhada.")
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        Pedido pedido = service.findById(id);
        return ResponseEntity.ok().body(pedido);
    }

    @Operation(summary = "Insere um novo pedido", description = "Inicia e cadastra localmente um novo pedido em branco ou com base em requisições de itens.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<Pedido> insert(@RequestBody Pedido pedido) {
        pedido = service.insert(pedido);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pedido.getId()).toUri();
        return ResponseEntity.created(uri).body(pedido);
    }

    @Operation(summary = "Deleta um pedido por ID", description = "Deleta de forma cabal um pedido específico do sistema utilizando sua chave ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um pedido por ID", description = "Edita livremente as informações referentes a um pedido específico fornecido por URL.")
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> update(@PathVariable Long id, @RequestBody Pedido pedido) {
        pedido = service.update(id, pedido);
        return ResponseEntity.ok().body(pedido);
    }
}
