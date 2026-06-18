package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.EntradaEstoqueRequestDto;
import com.projetoweb.oficinamecanica.dto.ProdutoRequestDto;
import com.projetoweb.oficinamecanica.dto.ProdutoResponseDto;
import com.projetoweb.oficinamecanica.services.EstoqueService;
import com.projetoweb.oficinamecanica.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;

    public ProdutoController(ProdutoService produtoService, EstoqueService estoqueService) {
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(produtoService.findAll(pageable));
    }

    @GetMapping("/estoque-critico")
    public ResponseEntity<Page<ProdutoResponseDto>> findEstoqueCritico(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(estoqueService.findEstoqueCritico(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<ProdutoResponseDto> insert(@Valid @RequestBody ProdutoRequestDto dto) {
        ProdutoResponseDto response = produtoService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{id}/entrada")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<ProdutoResponseDto> registrarEntrada(@PathVariable Long id,
                                                               @Valid @RequestBody EntradaEstoqueRequestDto dto) {
        return ResponseEntity.ok(estoqueService.registrarEntrada(id, dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<ProdutoResponseDto> update(@PathVariable Long id,
                                                     @Valid @RequestBody ProdutoRequestDto dto) {
        return ResponseEntity.ok(produtoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
