package com.banco.bankingsystem.controller;

import com.banco.bankingsystem.dto.CuentaDTO;
import com.banco.bankingsystem.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> crear(@Valid @RequestBody CuentaDTO dto) {
        CuentaDTO creada = cuentaService.crear(dto);
        return ResponseEntity.created(URI.create("/cuentas/" + creada.getNumeroCuenta())).body(creada);
    }

    @GetMapping
    public List<CuentaDTO> listar() {
        return cuentaService.listar();
    }

    @GetMapping("/{numeroCuenta}")
    public CuentaDTO obtener(@PathVariable Long numeroCuenta) {
        return cuentaService.obtener(numeroCuenta);
    }

    @PutMapping("/{numeroCuenta}")
    public CuentaDTO actualizar(@PathVariable Long numeroCuenta, @Valid @RequestBody CuentaDTO dto) {
        return cuentaService.actualizar(numeroCuenta, dto);
    }

    @PatchMapping("/{numeroCuenta}")
    public CuentaDTO actualizarParcial(@PathVariable Long numeroCuenta, @RequestBody CuentaDTO dto) {
        return cuentaService.actualizarParcial(numeroCuenta, dto);
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> eliminar(@PathVariable Long numeroCuenta) {
        cuentaService.eliminar(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
