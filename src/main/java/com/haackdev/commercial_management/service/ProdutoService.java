package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.repository.ProdutoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Busca todos os produtos cadastrados
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    // Busca um produto pelo ID
    public Produto findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo produto
    public Produto insert(Produto produto) {
        return produtoRepository.save(produto);
    }

    // Deleta um produto pelo ID
    public void delete(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            produtoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um produto que possui pedidos ou desenvolvimentos vinculados.");
        }
    }

    // Atualiza um produto existente
    public Produto update(Long id, Produto produto) {
        try {
            Produto entity = produtoRepository.getReferenceById(id);
            updateData(entity, produto);
            return produtoRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do produto
    private void updateData(Produto entity, Produto novoProduto) {
        entity.setDescricao(novoProduto.getDescricao());
        entity.setTipo(novoProduto.getTipo());
        entity.setCor(novoProduto.getCor());
        entity.setMaterial(novoProduto.getMaterial());
        entity.setValorCusto(novoProduto.getValorCusto());
        entity.setValorVenda(novoProduto.getValorVenda());
    }
}
