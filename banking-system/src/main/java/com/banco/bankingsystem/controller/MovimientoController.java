package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.MovimientoDTO;
import com.banco.bankingsystem.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> crear(@Valid @RequestBody MovimientoDTO dto) {
        MovimientoDTO creado = movimientoService.crear(dto);
        return ResponseEntity.created(URI.create("/movimientos/" + creado.getMovimientoId())).body(creado);
    }

    @GetMapping
    public List<MovimientoDTO> listar() {
        return movimientoService.listar();
    }

    @GetMapping("/{id}")
    public MovimientoDTO obtener(@PathVariable Long id) {
        return movimientoService.obtener(id);
    }

    @PutMapping("/{id}")
    public MovimientoDTO actualizar(@PathVariable Long id, @RequestBody MovimientoDTO dto) {
        return movimientoService.actualizar(id, dto);
    }

    @PatchMapping("/{id}")
    public MovimientoDTO actualizarParcial(@PathVariable Long id, @RequestBody MovimientoDTO dto) {
        return movimientoService.actualizarParcial(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
