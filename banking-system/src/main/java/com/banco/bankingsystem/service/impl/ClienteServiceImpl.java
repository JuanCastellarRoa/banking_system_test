package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.dto.ClienteDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.CuentaRepository;
import com.banco.bankingsystem.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, CuentaRepository cuentaRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public ClienteDTO crear(ClienteDTO dto) {
        dto.setClienteId(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria");
        }
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new ReglaNegocioException("La identificación ya existe");
        }
        Cliente cliente = toEntity(new Cliente(), dto);
        return toDto(clienteRepository.save(cliente));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorClienteId(String clienteId) {
        return toDto(buscar(clienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> listar() {
        return clienteRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public ClienteDTO actualizar(String clienteId, ClienteDTO dto) {
        Cliente cliente = buscar(clienteId);
        String contrasenaActual = cliente.getContrasena();
        toEntity(cliente, dto);
        cliente.setClienteId(clienteId);
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            cliente.setContrasena(contrasenaActual);
        }
        return toDto(clienteRepository.save(cliente));
    }

    @Override
    public ClienteDTO actualizarParcial(String clienteId, ClienteDTO dto) {
        Cliente cliente = buscar(clienteId);
        Optional.ofNullable(dto.getNombre()).ifPresent(cliente::setNombre);
        Optional.ofNullable(dto.getGenero()).ifPresent(cliente::setGenero);
        Optional.ofNullable(dto.getEdad()).ifPresent(cliente::setEdad);
        Optional.ofNullable(dto.getIdentificacion()).ifPresent(cliente::setIdentificacion);
        Optional.ofNullable(dto.getDireccion()).ifPresent(cliente::setDireccion);
        Optional.ofNullable(dto.getTelefono()).ifPresent(cliente::setTelefono);
        Optional.ofNullable(dto.getContrasena()).filter(c -> !c.isBlank()).ifPresent(cliente::setContrasena);
        Optional.ofNullable(dto.getEstado()).ifPresent(cliente::setEstado);
        return toDto(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(String clienteId) {
        if (cuentaRepository.existsByClienteClienteIdAndEstadoTrue(clienteId)) {
            throw new ReglaNegocioException(
                "No se puede eliminar el cliente: tiene cuentas activas asociadas.");
        }
        Cliente cliente = buscar(clienteId);
        clienteRepository.delete(cliente);
    }

    private Cliente buscar(String clienteId) {
        return clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + clienteId));
    }

    private Cliente toEntity(Cliente cliente, ClienteDTO dto) {
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setClienteId(dto.getClienteId());
        cliente.setContrasena(dto.getContrasena());
        cliente.setEstado(dto.getEstado() == null ? Boolean.TRUE : dto.getEstado());
        return cliente;
    }

    private ClienteDTO toDto(Cliente c) {
        return ClienteDTO.builder()
                .personaId(c.getPersonaId())
                .clienteId(c.getClienteId())
                .nombre(c.getNombre())
                .genero(c.getGenero())
                .edad(c.getEdad())
                .identificacion(c.getIdentificacion())
                .direccion(c.getDireccion())
                .telefono(c.getTelefono())
                .contrasena(c.getContrasena())
                .estado(c.getEstado())
                .build();
    }
}
