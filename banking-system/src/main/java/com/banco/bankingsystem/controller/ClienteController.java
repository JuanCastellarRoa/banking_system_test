package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.ClienteDTO;
import com.banco.bankingsystem.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        ClienteDTO creado = clienteService.crear(dto);
        return ResponseEntity.created(URI.create("/clientes/" + creado.getClienteId())).body(creado);
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{clienteId}")
    public ClienteDTO obtener(@PathVariable String clienteId) {
        return clienteService.obtenerPorClienteId(clienteId);
    }

    @PutMapping("/{clienteId}")
    public ClienteDTO actualizar(@PathVariable String clienteId, @Valid @RequestBody ClienteDTO dto) {
        return clienteService.actualizar(clienteId, dto);
    }

    @PatchMapping("/{clienteId}")
    public ClienteDTO actualizarParcial(@PathVariable String clienteId, @RequestBody ClienteDTO dto) {
        return clienteService.actualizarParcial(clienteId, dto);
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable String clienteId) {
        clienteService.eliminar(clienteId);
        return ResponseEntity.noContent().build();
    }
}
