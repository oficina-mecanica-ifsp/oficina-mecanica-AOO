package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.CarroRequestDto;
import com.projetoweb.oficinamecanica.dto.CarroResponseDto;
import com.projetoweb.oficinamecanica.entities.Carro;
import com.projetoweb.oficinamecanica.entities.Cliente;
import com.projetoweb.oficinamecanica.exceptions.ResourceNotFoundException;
import com.projetoweb.oficinamecanica.repositories.CarroRepository;
import com.projetoweb.oficinamecanica.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class CarroService {

    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;

    public CarroService(CarroRepository carroRepository, ClienteRepository clienteRepository) {
        this.carroRepository = carroRepository;
        this.clienteRepository = clienteRepository;
    }

    public Page<CarroResponseDto> findAll(Pageable pageable) {
        return carroRepository.findAll(pageable).map(CarroResponseDto::from);
    }

    public Page<CarroResponseDto> findByClienteId(Long clienteId, Pageable pageable) {
        return carroRepository.findByClienteId(clienteId, pageable).map(CarroResponseDto::from);
    }

    public CarroResponseDto findById(Long id) {
        Carro entity = carroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carro não encontrado com id: " + id));
        return CarroResponseDto.from(entity);
    }

    @Transactional
    public CarroResponseDto insert(CarroRequestDto dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + dto.clienteId()));

        Carro entity = new Carro();
        entity.setModelo(dto.modelo());
        entity.setPlaca(dto.placa());
        entity.setCor(dto.cor());
        entity.setAnoFabricacao(dto.anoFabricacao());
        entity.setMarca(dto.marca());
        entity.setCliente(cliente);

        return CarroResponseDto.from(carroRepository.save(entity));
    }

    @Transactional
    public CarroResponseDto update(Long id, CarroRequestDto dto) {
        Carro entity = carroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carro não encontrado com id: " + id));
        entity.setModelo(dto.modelo());
        entity.setPlaca(dto.placa());
        entity.setCor(dto.cor());
        entity.setAnoFabricacao(dto.anoFabricacao());
        entity.setMarca(dto.marca());

        if (!entity.getCliente().getId().equals(dto.clienteId())) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + dto.clienteId()));
            entity.setCliente(novoCliente);
        }

        return CarroResponseDto.from(carroRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!carroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Carro não encontrado com id: " + id);
        }
        carroRepository.deleteById(id);
    }
}