package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.ServicoRequestDto;
import com.projetoweb.oficinamecanica.dto.ServicoResponseDto;
import com.projetoweb.oficinamecanica.services.ServicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public ResponseEntity<Page<ServicoResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(servicoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDto> insert(@Valid @RequestBody ServicoRequestDto dto) {
        ServicoResponseDto response = servicoService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDto> update(@PathVariable Long id,
                                                     @Valid @RequestBody ServicoRequestDto dto) {
        return ResponseEntity.ok(servicoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        servicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
