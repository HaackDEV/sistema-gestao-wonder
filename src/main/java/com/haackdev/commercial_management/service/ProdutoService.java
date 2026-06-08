package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.ProdutoRequest;
import com.haackdev.commercial_management.dto.response.ProdutoResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.mapper.ProdutoMapper;
import com.haackdev.commercial_management.repository.FornecedorRepository;
import com.haackdev.commercial_management.repository.ProdutoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final FornecedorRepository fornecedorRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper, FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.fornecedorRepository = fornecedorRepository;
    }

    // Busca todos os produtos cadastrados
    public List<ProdutoResponse> findAll() {
        return produtoRepository.findAll().stream().map(produtoMapper::ProdutoToProdutoResponse).toList();
    }

    // Busca um produto pelo ID
    public ProdutoResponse findById(Long id) {
        return produtoRepository.findById(id).map(produtoMapper::ProdutoToProdutoResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo produto
    public ProdutoResponse insert(ProdutoRequest produtoRequest) {
        Produto produto = produtoMapper.requestToProduto(produtoRequest);
        produto.setId(null);

        if (!fornecedorRepository.existsById(produtoRequest.fornecedorId())) {
            throw new ResourceNotFoundException(produtoRequest.fornecedorId());
        }

        Fornecedor fornecedor = fornecedorRepository.getReferenceById(produtoRequest.fornecedorId());
        produto.setFornecedor(fornecedor);

        Produto produtoResponse = produtoRepository.save(produto);
        return produtoMapper.ProdutoToProdutoResponse(produtoResponse);
    }

    // Deleta um produto pelo ID
    public void delete(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        try {
            produtoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um produto pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um produto existente
    public ProdutoResponse update(Long id, ProdutoRequest produtoRequest) {
        try {
            Produto entity = produtoRepository.getReferenceById(id);
            updateData(entity, produtoRequest);

            if (!fornecedorRepository.existsById(produtoRequest.fornecedorId())) {
                throw new ResourceNotFoundException(produtoRequest.fornecedorId());
            }

            Fornecedor fornecedor = fornecedorRepository.getReferenceById(produtoRequest.fornecedorId());
            entity.setFornecedor(fornecedor);

            entity = produtoRepository.save(entity);
            return produtoMapper.ProdutoToProdutoResponse(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do produto
    private void updateData(Produto entity, ProdutoRequest novoProduto) {
        entity.setDescricao(novoProduto.descricao());
        entity.setTipo(novoProduto.tipo());
        entity.setCor(novoProduto.cor());
        entity.setMaterial(novoProduto.material());
        entity.setValorCusto(novoProduto.valorCusto());
        entity.setValorVenda(novoProduto.valorVenda());
        entity.setCodigoProduto(novoProduto.codigoProduto());
    }
}
