package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.TipoFinanceiro;
import com.adbrassacoma.administrativo.domain.model.Financeiro;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroFinanceiroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.FinanceiroResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.FinanceiroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.MembroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.FinanceiroRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.MembrosRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinanceiroService")
class FinanceiroServiceTest {

    @Mock
    private FinanceiroRepository financeiroRepository;

    @Mock
    private MembrosRepository membrosRepository;

    @InjectMocks
    private FinanceiroService financeiroService;

    @Test
    void cadastrarDeveLancarQuandoEntradaESaidaNulos() {
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(null, null, TipoFinanceiro.DIZIMO, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
        verify(financeiroRepository, never()).save(any());
    }

    @Test
    void cadastrarDeveLancarQuandoEntradaNegativa() {
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                new BigDecimal("-10"), BigDecimal.ZERO, TipoFinanceiro.DIZIMO, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveLancarQuandoMembroNaoExiste() {
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                new BigDecimal("100"), null, TipoFinanceiro.DIZIMO, null, 999L);
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MembroNaoEncontradoException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveSalvarERetornarResponse() {
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                new BigDecimal("100"), BigDecimal.ZERO, TipoFinanceiro.DIZIMO, "Obs", null);
        Financeiro salvo = Financeiro.builder().id(1L).entrada(new BigDecimal("100")).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).observacao("Obs").dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.save(any())).thenReturn(salvo);

        FinanceiroResponse response = financeiroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(0, new BigDecimal("100").compareTo(response.entrada()));
        assertEquals(TipoFinanceiro.DIZIMO, response.tipo());
    }

    @Test
    void listarTodosDeveRetornarLista() {
        Financeiro f = Financeiro.builder().id(1L).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.OFERTAS).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findAll()).thenReturn(List.of(f));

        var list = financeiroService.listarTodos();

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id());
    }

    @Test
    void buscarPorIdDeveLancarQuandoNaoEncontrado() {
        when(financeiroRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(FinanceiroNaoEncontradoException.class, () -> financeiroService.buscarPorId(999L));
    }

    @Test
    void buscarPorTipoDeveLancarQuandoListaVazia() {
        when(financeiroRepository.findByTipo(TipoFinanceiro.DIZIMO)).thenReturn(List.of());

        assertThrows(FinanceiroNaoEncontradoException.class,
                () -> financeiroService.buscarPorTipo(TipoFinanceiro.DIZIMO));
    }

    @Test
    void buscarPorMembroDeveLancarQuandoMembroNaoExiste() {
        when(membrosRepository.existsById(999L)).thenReturn(false);

        assertThrows(MembroNaoEncontradoException.class, () -> financeiroService.buscarPorMembro(999L));
    }

    @Test
    void deletarDeveLancarQuandoNaoExiste() {
        when(financeiroRepository.existsById(999L)).thenReturn(false);

        assertThrows(FinanceiroNaoEncontradoException.class, () -> financeiroService.deletar(999L));
        verify(financeiroRepository, never()).deleteById(any());
    }

    @Test
    void deletarDeveChamarDeleteQuandoExiste() {
        when(financeiroRepository.existsById(1L)).thenReturn(true);

        financeiroService.deletar(1L);

        verify(financeiroRepository).deleteById(1L);
    }

    @Test
    void cadastrarDeveLancarQuandoSaidaNegativa() {
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                BigDecimal.ZERO, new BigDecimal("-5"), TipoFinanceiro.OFERTAS, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveSalvarComMembroQuandoMembroIdInformado() {
        com.adbrassacoma.administrativo.domain.model.Membros membro = com.adbrassacoma.administrativo.domain.model.Membros.builder()
                .id(1L).nome("M").cpf("11144477735").build();
        when(membrosRepository.findById(1L)).thenReturn(Optional.of(membro));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                new BigDecimal("50"), null, TipoFinanceiro.DIZIMO, null, 1L);
        Financeiro salvo = Financeiro.builder().id(1L).entrada(new BigDecimal("50")).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).membro(membro).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.save(any())).thenReturn(salvo);

        FinanceiroResponse response = financeiroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void buscarPorTipoDeveRetornarLista() {
        Financeiro f = Financeiro.builder().id(1L).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findByTipo(TipoFinanceiro.DIZIMO)).thenReturn(List.of(f));

        var list = financeiroService.buscarPorTipo(TipoFinanceiro.DIZIMO);

        assertEquals(1, list.size());
        assertEquals(TipoFinanceiro.DIZIMO, list.get(0).tipo());
    }

    @Test
    void buscarPorMembroDeveLancarQuandoListaVazia() {
        when(membrosRepository.existsById(1L)).thenReturn(true);
        when(financeiroRepository.findByMembroId(1L)).thenReturn(List.of());

        assertThrows(FinanceiroNaoEncontradoException.class, () -> financeiroService.buscarPorMembro(1L));
    }

    @Test
    void buscarPorMembroDeveRetornarLista() {
        when(membrosRepository.existsById(1L)).thenReturn(true);
        Financeiro f = Financeiro.builder().id(1L).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findByMembroId(1L)).thenReturn(List.of(f));

        var list = financeiroService.buscarPorMembro(1L);

        assertEquals(1, list.size());
    }

    @Test
    void atualizarDeveLancarQuandoMembroNaoExiste() {
        Financeiro fin = Financeiro.builder().id(1L).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findById(1L)).thenReturn(Optional.of(fin));
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());
        com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest req =
                new com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest(
                        BigDecimal.ONE, BigDecimal.ZERO, TipoFinanceiro.DIZIMO, null, 999L);

        assertThrows(MembroNaoEncontradoException.class, () -> financeiroService.atualizar(1L, req));
    }

    @Test
    void atualizarDeveSalvarERetornarResponse() {
        Financeiro fin = Financeiro.builder().id(1L).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .tipo(TipoFinanceiro.DIZIMO).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findById(1L)).thenReturn(Optional.of(fin));
        when(financeiroRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest req =
                new com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest(
                        new BigDecimal("200"), BigDecimal.ZERO, TipoFinanceiro.OFERTAS, "Obs", null);

        FinanceiroResponse response = financeiroService.atualizar(1L, req);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("200").compareTo(response.entrada()));
        assertEquals(TipoFinanceiro.OFERTAS, response.tipo());
    }
}
