package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.dto.ClienteRequestDto;
import com.projetoweb.oficinamecanica.dto.ClienteResponseDto;
import com.projetoweb.oficinamecanica.entities.Cliente;
import com.projetoweb.oficinamecanica.exceptions.ResourceNotFoundException;
import com.projetoweb.oficinamecanica.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Page<ClienteResponseDto> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(ClienteResponseDto::from);
    }

    public ClienteResponseDto findById(Long id) {
        Cliente entity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + id));
        return ClienteResponseDto.from(entity);
    }

    public ClienteResponseDto findByDoc(String doc) {
        Cliente entity = clienteRepository.findByDoc(doc)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com documento: " + doc));
        return ClienteResponseDto.from(entity);
    }

    @Transactional
    public ClienteResponseDto insert(ClienteRequestDto dto) {
        Cliente entity = new Cliente();
        entity.setNome(dto.nome());
        entity.setTelefone(dto.telefone());
        entity.setEmail(dto.email());
        entity.setDoc(dto.doc());
        return ClienteResponseDto.from(clienteRepository.save(entity));
    }

    @Transactional
    public ClienteResponseDto update(Long id, ClienteRequestDto dto) {
        Cliente entity = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + id));
        entity.setNome(dto.nome());
        entity.setTelefone(dto.telefone());
        entity.setEmail(dto.email());
        entity.setDoc(dto.doc());
        return ClienteResponseDto.from(clienteRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}