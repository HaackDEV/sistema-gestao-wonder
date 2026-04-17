package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.dto.request.ClienteRequest;
import com.haackdev.commercial_management.dto.response.ClienteResponse;
import com.haackdev.commercial_management.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteResource {

    // Injeção de dependência via construtor
    private final ClienteService service;

    public ClienteResource(ClienteService service) {
        this.service = service;
    }

    @Operation(summary = "Busca todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados no sistema.")
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        List<ClienteResponse> clientes = service.findAll();
        return ResponseEntity.ok().body(clientes);
    }

    @Operation(summary = "Busca um cliente por ID", description = "Recupera os detalhes de um cliente em específico filtrando pelo seu ID único.")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long id) {
        ClienteResponse response = service.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Insere um novo cliente", description = "Cadastra um novo cliente na base de dados com as informações fornecidas e retorna o cliente criado.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<ClienteResponse> insert(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = service.insert(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Deleta um cliente por ID", description = "Remove um cliente do sistema com base no ID fornecido.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um cliente por ID", description = "Modifica as informações de um cliente existente na base de dados.")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = service.update(id, request);
        return ResponseEntity.ok().body(response);
    }
}
