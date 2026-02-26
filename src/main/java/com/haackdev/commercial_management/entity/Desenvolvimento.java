package com.haackdev.commercial_management.entity;

import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_desenvolvimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Desenvolvimento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "tipo", nullable = false)
    private String tipo; // Ex: Desenvolvimento, Amostra, Pilotagem

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "status", nullable = false)
    private StatusDesenvolvimento status; // Ex: Aprovado, Reprovado, Em análise

    @Column(name = "motivo_reprovacao")
    private String motivoReprovacao;

    @Column(name = "virou_pedido", nullable = false)
    private Boolean virouPedido = false;

    @Column(name = "valor_convertido", precision = 10, scale = 2)
    private BigDecimal valorConvertido;

    @Column(name = "data_conversao")
    private LocalDate dataConversao;
}
