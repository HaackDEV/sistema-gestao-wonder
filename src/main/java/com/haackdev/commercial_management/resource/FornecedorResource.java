package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.dto.request.FornecedorRequest;
import com.haackdev.commercial_management.dto.response.FornecedorResponse;
import com.haackdev.commercial_management.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Fornecedores", description = "Endpoints para gerenciamento de fornecedores")
@RestController
@RequestMapping(value = "/fornecedores")
public class FornecedorResource {

    // Injeção de dependência via construtor
    private final FornecedorService service;

    public FornecedorResource(FornecedorService service) {this.service = service;}

    @Operation(summary = "Busca todos os fornecedores", description = "Retorna uma lista completa contendo todos os fornecedores registrados.")
    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> findAll() {
        List<FornecedorResponse> fornecedores = service.findAll();
        return ResponseEntity.ok().body(fornecedores);
    }

    @Operation(summary = "Busca um fornecedor por ID", description = "Busca de forma pontual um fornecedor na base de dados através de seu ID.")
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> findById(@PathVariable Long id) {
        FornecedorResponse fornecedor = service.findById(id);
        return ResponseEntity.ok().body(fornecedor);
    }

    @Operation(summary = "Insere um novo fornecedor", description = "Realiza o cadastro de um novo fornecedor na plataforma.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<FornecedorResponse> insert(@Valid @RequestBody FornecedorRequest fornecedor) {
        FornecedorResponse response = service.insert(fornecedor);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Deleta um fornecedor por ID", description = "Exclui um fornecedor específico a partir de seu ID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um fornecedor por ID", description = "Muda os dados corporativos e cadastrais de um fornecedor já registrado com o ID providenciado.")
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> update(@PathVariable Long id, @Valid @RequestBody FornecedorRequest fornecedor) {
        FornecedorResponse fornecedorResponse = service.update(id, fornecedor);
        return ResponseEntity.ok().body(fornecedorResponse);
    }
}
