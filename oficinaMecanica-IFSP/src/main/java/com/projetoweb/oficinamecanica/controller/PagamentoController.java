package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.PagamentoRequestDto;
import com.projetoweb.oficinamecanica.dto.PagamentoResponseDto;
import com.projetoweb.oficinamecanica.services.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PagamentoResponseDto> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(pagamentoService.findByOrderId(orderId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE', 'ADMIN')")
    public ResponseEntity<PagamentoResponseDto> registrar(@Valid @RequestBody PagamentoRequestDto dto) {
        PagamentoResponseDto response = pagamentoService.registrar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
