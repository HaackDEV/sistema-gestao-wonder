package com.haackdev.commercial_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "codigo_produto", nullable = false, unique = true)
    private String codigoProduto;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "cor")
    private String cor;

    @Column(name = "material")
    private String material;

    @Column(name = "valor_custo", precision = 10, scale = 2)
    private BigDecimal valorCusto;

    @Column(name = "valor_venda", precision = 10, scale = 2)
    private BigDecimal valorVenda;
}
