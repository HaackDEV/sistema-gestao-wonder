package com.haackdev.commercial_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Cliente implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(name = "cnpj", nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(name = "endereco_completo")
    private String enderecoCompleto;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "telefone_geral")
    private String telefoneGeral;

    @Column(name = "email_geral")
    private String emailGeral;

    @Column(name = "contato_desenvolvimento")
    private String contatoDesenvolvimento;

    @Column(name = "contato_compras")
    private String contatoCompras;

    @Column(name = "condicoes_pagamento")
    private String condicoesPagamento;
}
