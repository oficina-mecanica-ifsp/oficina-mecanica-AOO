package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.AtualizacaoStatusRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderProdutoRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderRequestDto;
import com.projetoweb.oficinamecanica.dto.OrderResponseDto;
import com.projetoweb.oficinamecanica.dto.OrderServicoRequestDto;
import com.projetoweb.oficinamecanica.services.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> insert(@Valid @RequestBody OrderRequestDto dto) {
        OrderResponseDto response = orderService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> atualizarStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AtualizacaoStatusRequestDto dto) {
        return ResponseEntity.ok(orderService.atualizarStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- ITENS DA ORDER ---

    @PostMapping("/{id}/produtos")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> adicionarProduto(@PathVariable Long id,
                                                             @Valid @RequestBody OrderProdutoRequestDto dto) {
        return ResponseEntity.ok(orderService.adicionarProduto(id, dto));
    }

    @DeleteMapping("/{id}/produtos/{produtoId}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> removerProduto(@PathVariable Long id,
                                                           @PathVariable Long produtoId) {
        return ResponseEntity.ok(orderService.removerProduto(id, produtoId));
    }

    @PostMapping("/{id}/servicos")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> adicionarServico(@PathVariable Long id,
                                                             @Valid @RequestBody OrderServicoRequestDto dto) {
        return ResponseEntity.ok(orderService.adicionarServico(id, dto));
    }

    @DeleteMapping("/{id}/servicos/{servicoId}")
    @PreAuthorize("hasAnyRole('MECANICO', 'ADMIN')")
    public ResponseEntity<OrderResponseDto> removerServico(@PathVariable Long id,
                                                           @PathVariable Long servicoId) {
        return ResponseEntity.ok(orderService.removerServico(id, servicoId));
    }
}
