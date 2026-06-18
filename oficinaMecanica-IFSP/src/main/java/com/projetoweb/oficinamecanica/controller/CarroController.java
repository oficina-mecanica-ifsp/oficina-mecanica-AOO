package com.projetoweb.oficinamecanica.controller;

import com.projetoweb.oficinamecanica.dto.CarroRequestDto;
import com.projetoweb.oficinamecanica.dto.CarroResponseDto;
import com.projetoweb.oficinamecanica.services.CarroService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/carros")
public class CarroController {

    private final CarroService carroService;

    public CarroController(CarroService carroService) {
        this.carroService = carroService;
    }

    @GetMapping
    public ResponseEntity<Page<CarroResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(carroService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarroResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(carroService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CarroResponseDto> insert(@Valid @RequestBody CarroRequestDto dto) {
        CarroResponseDto response = carroService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarroResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody CarroRequestDto dto) {
        return ResponseEntity.ok(carroService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
