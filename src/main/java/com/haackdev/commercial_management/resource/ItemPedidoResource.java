package com.haackdev.commercial_management.resource;

import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.service.ItemPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/itens_pedidos")
public class ItemPedidoResource {

    @Autowired
    private ItemPedidoService service;

    @GetMapping
    public ResponseEntity<List<ItemPedido>> findAll() {
        List<ItemPedido> itensPedidos = service.findAll();
        return ResponseEntity.ok().body(itensPedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> findById(@PathVariable Long id) {
        ItemPedido itemPedido = service.findById(id);
        return ResponseEntity.ok().body(itemPedido);
    }

    @PostMapping
    public ResponseEntity<ItemPedido> insert(@RequestBody ItemPedido itemPedido) {
        itemPedido = service.insert(itemPedido);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(itemPedido.getId()).toUri();
        return ResponseEntity.created(uri).body(itemPedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> update(@PathVariable Long id, @RequestBody ItemPedido itemPedido) {
        itemPedido = service.update(id, itemPedido);
        return ResponseEntity.ok().body(itemPedido);
    }
}
