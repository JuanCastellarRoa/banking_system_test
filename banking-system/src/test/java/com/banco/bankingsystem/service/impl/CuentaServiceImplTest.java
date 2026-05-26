package com.banco.bankingsystem.service.impl;

import com.banco.bankingsystem.domain.Cliente;
import com.banco.bankingsystem.domain.Cuenta;
import com.banco.bankingsystem.domain.Movimiento;
import com.banco.bankingsystem.domain.TipoCuenta;
import com.banco.bankingsystem.dto.CuentaDTO;
import com.banco.bankingsystem.exception.RecursoNoEncontradoException;
import com.banco.bankingsystem.exception.ReglaNegocioException;
import com.banco.bankingsystem.repository.ClienteRepository;
import com.banco.bankingsystem.repository.CuentaRepository;
import com.banco.bankingsystem.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock private CuentaRepository cuentaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private MovimientoRepository movimientoRepository;

    private CuentaServiceImpl service;

    private Cliente cliente;
    private Cuenta cuenta;
    private CuentaDTO cuentaDTO;

    @BeforeEach
    void setUp() {
        service = new CuentaServiceImpl(cuentaRepository, clienteRepository, movimientoRepository);

        cliente = new Cliente();
        cliente.setPersonaId(1L);
        cliente.setClienteId("abc123");
        cliente.setNombre("Jose Lema");
        cliente.setEstado(true);

        cuenta = new Cuenta();
        cuenta.setNumeroCuenta(225487L);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldoInicial(new BigDecimal("500"));
        cuenta.setEstado(true);
        cuenta.setCliente(cliente);

        cuentaDTO = CuentaDTO.builder()
                .numeroCuenta(225487L)
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500"))
                .estado(true)
                .clienteId("abc123")
                .build();
    }

    @Test
    void crear_cuenta_exitosa() {
        when(cuentaRepository.existsById(225487L)).thenReturn(false);
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.of(cliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        CuentaDTO resultado = service.crear(cuentaDTO);

        assertThat(resultado.getNumeroCuenta()).isEqualTo(225487L);
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void crear_cuenta_numero_duplicado_lanza_excepcion() {
        when(cuentaRepository.existsById(225487L)).thenReturn(true);
        assertThrows(ReglaNegocioException.class, () -> service.crear(cuentaDTO));
    }

    @Test
    void crear_cuenta_cliente_no_encontrado_lanza_excepcion() {
        when(cuentaRepository.existsById(225487L)).thenReturn(false);
        when(clienteRepository.findByClienteId("abc123")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.crear(cuentaDTO));
    }

    @Test
    void obtener_cuenta_exitosa() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        CuentaDTO resultado = service.obtener(225487L);

        assertThat(resultado.getNumeroCuenta()).isEqualTo(225487L);
        assertThat(resultado.getSaldo()).isEqualByComparingTo("500");
    }

    @Test
    void obtener_cuenta_con_movimiento_usa_saldo_del_movimiento() {
        Movimiento mov = new Movimiento();
        mov.setSaldo(new BigDecimal("800"));
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.of(mov));

        CuentaDTO resultado = service.obtener(225487L);

        assertThat(resultado.getSaldo()).isEqualByComparingTo("800");
    }

    @Test
    void obtener_cuenta_no_encontrada_lanza_excepcion() {
        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(999L));
    }

    @Test
    void listar_retorna_todas() {
        when(cuentaRepository.findAll()).thenReturn(List.of(cuenta));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        List<CuentaDTO> lista = service.listar();

        assertThat(lista).hasSize(1);
    }

    @Test
    void actualizar_cuenta_exitosa() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        CuentaDTO dto = CuentaDTO.builder()
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000"))
                .estado(true)
                .build();

        CuentaDTO resultado = service.actualizar(225487L, dto);

        verify(cuentaRepository).save(any(Cuenta.class));
        assertThat(resultado).isNotNull();
    }

    @Test
    void actualizar_cuenta_con_nuevo_cliente() {
        Cliente otroCliente = new Cliente();
        otroCliente.setClienteId("def456");
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(clienteRepository.findByClienteId("def456")).thenReturn(Optional.of(otroCliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        CuentaDTO dto = CuentaDTO.builder()
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500"))
                .estado(true)
                .clienteId("def456")
                .build();

        service.actualizar(225487L, dto);

        assertThat(cuenta.getCliente().getClienteId()).isEqualTo("def456");
    }

    @Test
    void actualizarParcial_tipo_cuenta() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());

        CuentaDTO dto = CuentaDTO.builder().tipoCuenta(TipoCuenta.CORRIENTE).build();
        service.actualizarParcial(225487L, dto);

        assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
    }

    @Test
    void actualizarParcial_cliente_no_encontrado_lanza_excepcion() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(clienteRepository.findByClienteId("noexiste")).thenReturn(Optional.empty());

        CuentaDTO dto = CuentaDTO.builder().clienteId("noexiste").build();
        assertThrows(RecursoNoEncontradoException.class, () -> service.actualizarParcial(225487L, dto));
    }

    @Test
    void eliminar_cuenta_saldo_cero_exitosa() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());
        cuenta.setSaldoInicial(BigDecimal.ZERO);

        service.eliminar(225487L);

        verify(cuentaRepository).delete(cuenta);
    }

    @Test
    void eliminar_cuenta_con_saldo_lanza_excepcion() {
        when(cuentaRepository.findById(225487L)).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaNumeroCuentaOrderByFechaDescMovimientoIdDesc(225487L))
                .thenReturn(Optional.empty());
        // saldoInicial = 500, no es 0

        assertThrows(ReglaNegocioException.class, () -> service.eliminar(225487L));
        verify(cuentaRepository, never()).delete(any());
    }
}
