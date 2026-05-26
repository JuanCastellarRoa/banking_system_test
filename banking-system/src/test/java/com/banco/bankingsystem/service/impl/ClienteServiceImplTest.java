package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.dto.ClienteDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private CuentaRepository cuentaRepository;

    private ClienteServiceImpl service;

    private Cliente clienteEntidad;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        service = new ClienteServiceImpl(clienteRepository, cuentaRepository);

        clienteEntidad = new Cliente();
        clienteEntidad.setPersonaId(1L);
        clienteEntidad.setClienteId("abc123");
        clienteEntidad.setNombre("Jose Lema");
        clienteEntidad.setGenero("M");
        clienteEntidad.setEdad(35);
        clienteEntidad.setIdentificacion("1234567890");
        clienteEntidad.setDireccion("Otavalo sn y principal");
        clienteEntidad.setTelefono("098254785");
        clienteEntidad.setContrasena("1234");
        clienteEntidad.setEstado(true);

        clienteDTO = ClienteDTO.builder()
                .nombre("Jose Lema")
                .genero("M")
                .edad(35)
                .identificacion("1234567890")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .contrasena("1234")
                .estado(true)
                .build();
    }

    @Test
    void crear_cliente_exitoso() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setPersonaId(1L);
            return c;
        });

        ClienteDTO resultado = service.crear(clienteDTO);

        assertThat(resultado.getNombre()).isEqualTo("Jose Lema");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void crear_cliente_sin_contrasena_lanza_excepcion() {
        clienteDTO.setContrasena(null);
        assertThrows(ReglaNegocioException.class, () -> service.crear(clienteDTO));
    }

    @Test
    void crear_cliente_contrasena_en_blanco_lanza_excepcion() {
        clienteDTO.setContrasena("  ");
        assertThrows(ReglaNegocioException.class, () -> service.crear(clienteDTO));
    }

    @Test
    void crear_cliente_identificacion_duplicada_lanza_excepcion() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.crear(clienteDTO));
    }

    @Test
    void obtenerPorClienteId_exitoso() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));

        ClienteDTO resultado = service.obtenerPorClienteId("abc123");

        assertThat(resultado.getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    void obtenerPorClienteId_no_encontrado_lanza_excepcion() {
        when(clienteRepository.findByClienteId("xyz")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorClienteId("xyz"));
    }

    @Test
    void listar_retorna_todos() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteEntidad));

        List<ClienteDTO> lista = service.listar();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    void actualizar_cliente_exitoso() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntidad);

        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Jose Actualizado")
                .contrasena("nueva")
                .estado(true)
                .build();

        ClienteDTO resultado = service.actualizar("abc123", dto);

        verify(clienteRepository).save(any(Cliente.class));
        assertThat(resultado).isNotNull();
    }

    @Test
    void actualizar_cliente_sin_contrasena_mantiene_anterior() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Sin nueva contraseña")
                .contrasena("")
                .estado(true)
                .build();

        service.actualizar("abc123", dto);

        assertThat(clienteEntidad.getContrasena()).isEqualTo("1234");
    }

    @Test
    void actualizarParcial_solo_nombre() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteDTO dto = ClienteDTO.builder().nombre("Nuevo Nombre").build();

        ClienteDTO resultado = service.actualizarParcial("abc123", dto);

        assertThat(clienteEntidad.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(clienteEntidad.getTelefono()).isEqualTo("098254785");
    }

    @Test
    void actualizarParcial_estado() {
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteDTO dto = ClienteDTO.builder().estado(false).build();

        service.actualizarParcial("abc123", dto);

        assertThat(clienteEntidad.getEstado()).isFalse();
    }

    @Test
    void eliminar_cliente_sin_cuentas_activas_exitoso() {
        when(cuentaRepository.existsByClienteClienteIdAndEstadoTrue("abc123")).thenReturn(false);
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(clienteEntidad));

        service.eliminar("abc123");

        verify(clienteRepository).delete(clienteEntidad);
    }

    @Test
    void eliminar_cliente_con_cuentas_activas_lanza_excepcion() {
        when(cuentaRepository.existsByClienteClienteIdAndEstadoTrue("abc123")).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.eliminar("abc123"));
        verify(clienteRepository, never()).delete(any());
    }

    @Test
    void eliminar_cliente_no_encontrado_lanza_excepcion() {
        when(cuentaRepository.existsByClienteClienteIdAndEstadoTrue("xyz")).thenReturn(false);
        when(clienteRepository.findByClienteId("xyz")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar("xyz"));
    }
}
