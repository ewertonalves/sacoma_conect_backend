package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;
import com.adbrassacoma.administrativo.domain.model.CodigoFinanceiroDefinicao;
import com.adbrassacoma.administrativo.domain.model.Financeiro;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest;
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

    private static final CodigoFinanceiroDefinicao CODE_DIZIMO = new CodigoFinanceiroDefinicao(1, "Dizimo",
            TipoMovimentacaoFinanceira.ENTRADA, CategoriaFinanceira.DIZIMO);
    private static final CodigoFinanceiroDefinicao CODE_OFERTA = new CodigoFinanceiroDefinicao(2, "Oferta",
            TipoMovimentacaoFinanceira.ENTRADA, CategoriaFinanceira.OFERTA);
    private static final CodigoFinanceiroDefinicao CODE_AGUA = new CodigoFinanceiroDefinicao(100, "Agua",
            TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.AGUA);

    @Mock
    private FinanceiroRepository financeiroRepository;

    @Mock
    private MembrosRepository membrosRepository;

    @Mock
    private CodigoFinanceiroCatalog codigoFinanceiroCatalog;

    @InjectMocks
    private FinanceiroService financeiroService;

    @Test
    void cadastrarDeveLancarQuandoCodigoInvalido() {
        when(codigoFinanceiroCatalog.buscar(99)).thenReturn(Optional.empty());
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(99, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
        verify(financeiroRepository, never()).save(any());
    }

    @Test
    void cadastrarDeveLancarQuandoEntradaESaidaZerosParaEntrada() {
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(1, BigDecimal.ZERO, BigDecimal.ZERO, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
        verify(financeiroRepository, never()).save(any());
    }

    @Test
    void cadastrarDeveLancarQuandoEntradaNegativa() {
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                1, new BigDecimal("-10"), BigDecimal.ZERO, null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveLancarQuandoMembroNaoExiste() {
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                1, new BigDecimal("100"), null, null, 999L);
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MembroNaoEncontradoException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveSalvarERetornarResponse() {
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                1, new BigDecimal("100"), BigDecimal.ZERO, "Obs", null);
        Financeiro salvo = Financeiro.builder().id(1L).codigoFinanceiro(1)
                .tipo(TipoMovimentacaoFinanceira.ENTRADA).categoria(CategoriaFinanceira.DIZIMO)
                .entrada(new BigDecimal("100")).saida(BigDecimal.ZERO)
                .observacao("Obs").dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.save(any())).thenReturn(salvo);

        FinanceiroResponse response = financeiroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(0, new BigDecimal("100").compareTo(response.entrada()));
        assertEquals(TipoMovimentacaoFinanceira.ENTRADA, response.tipo());
        assertEquals(CategoriaFinanceira.DIZIMO, response.categoria());
        assertEquals(1, response.codigoFinanceiro());
    }

    @Test
    void listarTodosDeveRetornarLista() {
        Financeiro f = Financeiro.builder().id(1L).codigoFinanceiro(2).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.OFERTA).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findAll()).thenReturn(List.of(f));
        when(codigoFinanceiroCatalog.buscar(2)).thenReturn(Optional.of(CODE_OFERTA));

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
        when(financeiroRepository.findByTipo(TipoMovimentacaoFinanceira.ENTRADA)).thenReturn(List.of());

        assertThrows(FinanceiroNaoEncontradoException.class,
                () -> financeiroService.buscarPorTipo(TipoMovimentacaoFinanceira.ENTRADA));
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
        when(codigoFinanceiroCatalog.buscar(100)).thenReturn(Optional.of(CODE_AGUA));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                100, BigDecimal.ZERO, new BigDecimal("-5"), null, null);

        assertThrows(IllegalArgumentException.class, () -> financeiroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveSalvarComMembroQuandoMembroIdInformado() {
        com.adbrassacoma.administrativo.domain.model.Membros membro = com.adbrassacoma.administrativo.domain.model.Membros.builder()
                .id(1L).nome("M").cpf("11144477735").build();
        when(membrosRepository.findById(1L)).thenReturn(Optional.of(membro));
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        CadastroFinanceiroRequest request = new CadastroFinanceiroRequest(
                1, new BigDecimal("50"), null, null, 1L);
        Financeiro salvo = Financeiro.builder().id(1L).codigoFinanceiro(1).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.DIZIMO).entrada(new BigDecimal("50")).saida(BigDecimal.ZERO)
                .membro(membro).dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.save(any())).thenReturn(salvo);

        FinanceiroResponse response = financeiroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void buscarPorTipoDeveRetornarLista() {
        Financeiro f = Financeiro.builder().id(1L).codigoFinanceiro(1).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.DIZIMO).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findByTipo(TipoMovimentacaoFinanceira.ENTRADA)).thenReturn(List.of(f));
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));

        var list = financeiroService.buscarPorTipo(TipoMovimentacaoFinanceira.ENTRADA);

        assertEquals(1, list.size());
        assertEquals(TipoMovimentacaoFinanceira.ENTRADA, list.get(0).tipo());
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
        Financeiro f = Financeiro.builder().id(1L).codigoFinanceiro(1).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.DIZIMO).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findByMembroId(1L)).thenReturn(List.of(f));
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));

        var list = financeiroService.buscarPorMembro(1L);

        assertEquals(1, list.size());
    }

    @Test
    void atualizarDeveLancarQuandoMembroNaoExiste() {
        Financeiro fin = Financeiro.builder().id(1L).codigoFinanceiro(1).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.DIZIMO).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findById(1L)).thenReturn(Optional.of(fin));
        when(codigoFinanceiroCatalog.buscar(1)).thenReturn(Optional.of(CODE_DIZIMO));
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());
        AtualizarFinanceiroRequest req =
                new AtualizarFinanceiroRequest(1, BigDecimal.ONE, BigDecimal.ZERO, null, 999L);

        assertThrows(MembroNaoEncontradoException.class, () -> financeiroService.atualizar(1L, req));
    }

    @Test
    void atualizarDeveSalvarERetornarResponse() {
        Financeiro fin = Financeiro.builder().id(1L).codigoFinanceiro(1).tipo(TipoMovimentacaoFinanceira.ENTRADA)
                .categoria(CategoriaFinanceira.DIZIMO).entrada(BigDecimal.ONE).saida(BigDecimal.ZERO)
                .dataRegistro(LocalDateTime.now()).build();
        when(financeiroRepository.findById(1L)).thenReturn(Optional.of(fin));
        when(codigoFinanceiroCatalog.buscar(2)).thenReturn(Optional.of(CODE_OFERTA));
        when(financeiroRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        AtualizarFinanceiroRequest req =
                new AtualizarFinanceiroRequest(2, new BigDecimal("200"), BigDecimal.ZERO, "Obs", null);

        FinanceiroResponse response = financeiroService.atualizar(1L, req);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("200").compareTo(response.entrada()));
        assertEquals(TipoMovimentacaoFinanceira.ENTRADA, response.tipo());
        assertEquals(CategoriaFinanceira.OFERTA, response.categoria());
        assertEquals(2, response.codigoFinanceiro());
    }
}
