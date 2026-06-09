package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.service.PedidoService;
import jakarta.validation.Valid;
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

    private final PedidoService pedidoService;

    public PedidoResource(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Busca todos os pedidos", description = "Recupera uma relação com todos os pedidos finalizados e/ou em andamento no sistema.")
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll() {
        List<PedidoResponse> pedidos = pedidoService.findAll();
        return ResponseEntity.ok().body(pedidos);
    }

    @Operation(summary = "Busca um pedido por ID", description = "Acessa todos os dados de um pedido e também a sua lista inteira de itens detalhada.")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable Long id) {
        PedidoResponse pedido = pedidoService.findById(id);
        return ResponseEntity.ok().body(pedido);
    }

    @Operation(summary = "Insere um novo pedido", description = "Inicia e cadastra localmente um novo pedido em branco ou com base em requisições de itens.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<PedidoResponse> insert(@Valid @RequestBody PedidoRequest pedidoRequest) {
        PedidoResponse pedidoResponse = pedidoService.insert(pedidoRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pedidoResponse.id()).toUri();
        return ResponseEntity.created(uri).body(pedidoResponse);
    }

    @Operation(summary = "Deleta um pedido por ID", description = "Deleta de forma cabal um pedido específico do sistema utilizando sua chave ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um pedido por ID", description = "Edita livremente as informações referentes a um pedido específico fornecido por URL.")
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(@PathVariable Long id, @Valid @RequestBody PedidoRequest pedidoRequest) {
        PedidoResponse response = pedidoService.update(id, pedidoRequest);
        return ResponseEntity.ok().body(response);
    }
}
