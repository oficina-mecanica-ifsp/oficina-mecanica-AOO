package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.EntradaEstoqueRequestDto;
import com.projetoweb.oficinamecanica.dto.ProdutoResponseDto;
import com.projetoweb.oficinamecanica.entities.Produto;
import com.projetoweb.oficinamecanica.exceptions.RegraDeNegocioException;
import com.projetoweb.oficinamecanica.exceptions.ResourceNotFoundException;
import com.projetoweb.oficinamecanica.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final EmailService emailService;

    public EstoqueService(ProdutoRepository produtoRepository, EmailService emailService) {
        this.produtoRepository = produtoRepository;
        this.emailService = emailService;
    }

    public Page<ProdutoResponseDto> findEstoqueCritico(Pageable pageable) {
        return produtoRepository.findEstoqueCritico(pageable).map(ProdutoResponseDto::from);
    }

    @Transactional
    public ProdutoResponseDto registrarEntrada(Long produtoId, EntradaEstoqueRequestDto dto) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + produtoId));
        incrementar(produto, dto.quantidade());
        return ProdutoResponseDto.from(produto);
    }

    @Transactional
    public void decrementar(Produto produto, int quantidade) {
        int disponivel = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
        if (disponivel < quantidade) {
            throw new RegraDeNegocioException(
                    "Estoque insuficiente para '" + produto.getNome() + "'. " +
                    "Disponível: " + disponivel + ", solicitado: " + quantidade
            );
        }
        int novaQuantidade = disponivel - quantidade;
        produto.setQuantidade(novaQuantidade);
        produtoRepository.save(produto);

        Integer minimo = produto.getQuantidadeMinima();
        if (minimo != null && novaQuantidade < minimo) {
            emailService.enviarAlertaEstoque(produto.getNome(), novaQuantidade, minimo);
        }
    }

    @Transactional
    public void incrementar(Produto produto, int quantidade) {
        int atual = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
        produto.setQuantidade(atual + quantidade);
        produtoRepository.save(produto);
    }
}
