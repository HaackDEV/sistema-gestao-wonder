package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RestController
@RequestMapping(value = "/produtos")
public class ProdutoResource {

    @Autowired
    private ProdutoService service;

    @Operation(summary = "Busca todos os produtos", description = "Exibe o catálogo completo de todos os produtos homologados no sistema.")
    @GetMapping
    public ResponseEntity<List<Produto>> findAll() {
        List<Produto> produtos = service.findAll();
        return ResponseEntity.ok().body(produtos);
    }

    @Operation(summary = "Busca um produto por ID", description = "Exibe os detalhes, preços e atribuições técnicas de um produto utilizando seu ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        Produto produto = service.findById(id);
        return ResponseEntity.ok().body(produto);
    }

    @Operation(summary = "Insere um novo produto", description = "Adiciona um produto inédito e novo ao catálogo, associando as suas métricas unitárias.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Recurso criado com sucesso")
    @PostMapping
    public ResponseEntity<Produto> insert(@RequestBody Produto produto) {
        produto = service.insert(produto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(produto.getId()).toUri();
        return ResponseEntity.created(uri).body(produto);
    }

    @Operation(summary = "Deleta um produto por ID", description = "Desabilita e remove um cadastro de produto existente, que não possui associações passadas conflituosas.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Recurso deletado com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um produto por ID", description = "Atualiza a base e configuração de um produto usando o seu ID correspondente.")
    @PutMapping("/{id}")
    public ResponseEntity<Produto> update(@PathVariable Long id, @RequestBody Produto produto) {
        produto = service.update(id, produto);
        return ResponseEntity.ok().body(produto);
    }
}
