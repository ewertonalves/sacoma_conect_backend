package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.AssistenciaSocial;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AssistenciaSocialResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.AssistenciaSocialNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.AssistenciaSocialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssistenciaSocialService")
class AssistenciaSocialServiceTest {

    @Mock
    private AssistenciaSocialRepository assistenciaSocialRepository;

    @InjectMocks
    private AssistenciaSocialService assistenciaSocialService;

    @Test
    void cadastrarDeveSalvarERetornarResponse() {
        CadastroAssistenciaSocialRequest request = new CadastroAssistenciaSocialRequest(
                "Arroz",
                new BigDecimal("50"),
                LocalDate.now().plusMonths(1),
                "Família A",
                new BigDecimal("2"),
                LocalDate.now()
        );
        AssistenciaSocial salvo = AssistenciaSocial.builder()
                .id(1L)
                .nomeAlimento("Arroz")
                .quantidade(new BigDecimal("50"))
                .dataValidade(request.dataValidade())
                .familiaBeneficiada("Família A")
                .quantidadeCestasBasicas(new BigDecimal("2"))
                .dataEntregaCesta(request.dataEntregaCesta())
                .dataRegistro(LocalDateTime.now())
                .build();
        when(assistenciaSocialRepository.save(any())).thenReturn(salvo);

        AssistenciaSocialResponse response = assistenciaSocialService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Arroz", response.nomeAlimento());
        assertEquals(0, new BigDecimal("50").compareTo(response.quantidade()));

        ArgumentCaptor<AssistenciaSocial> captor = ArgumentCaptor.forClass(AssistenciaSocial.class);
        verify(assistenciaSocialRepository).save(captor.capture());
        assertEquals("Arroz", captor.getValue().getNomeAlimento());
    }

    @Test
    void listarTodosSemBuscaDeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        AssistenciaSocial entidade = AssistenciaSocial.builder().id(1L).nomeAlimento("A").quantidade(BigDecimal.ONE)
                .dataValidade(LocalDate.now()).dataRegistro(LocalDateTime.now()).build();
        when(assistenciaSocialRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entidade)));

        Page<AssistenciaSocialResponse> result = assistenciaSocialService.listarTodos(pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
    }

    @Test
    void listarTodosComBuscaDeveChamarBuscarComFiltro() {
        Pageable pageable = PageRequest.of(0, 10);
        when(assistenciaSocialRepository.buscarComFiltro("arroz", pageable)).thenReturn(Page.empty());

        assistenciaSocialService.listarTodos(pageable, "  arroz  ");

        verify(assistenciaSocialRepository).buscarComFiltro("arroz", pageable);
    }

    @Test
    void buscarPorIdDeveLancarQuandoNaoEncontrado() {
        when(assistenciaSocialRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AssistenciaSocialNaoEncontradoException.class,
                () -> assistenciaSocialService.buscarPorId(999L));
    }

    @Test
    void buscarPorIdDeveRetornarResponseQuandoEncontrado() {
        AssistenciaSocial entidade = AssistenciaSocial.builder().id(1L).nomeAlimento("A").quantidade(BigDecimal.ONE)
                .dataValidade(LocalDate.now()).dataRegistro(LocalDateTime.now()).build();
        when(assistenciaSocialRepository.findById(1L)).thenReturn(Optional.of(entidade));

        AssistenciaSocialResponse response = assistenciaSocialService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals("A", response.nomeAlimento());
    }

    @Test
    void atualizarDeveLancarQuandoNaoEncontrado() {
        when(assistenciaSocialRepository.findById(999L)).thenReturn(Optional.empty());
        AtualizarAssistenciaSocialRequest request = new AtualizarAssistenciaSocialRequest(null, null, null, null, null, null);

        assertThrows(AssistenciaSocialNaoEncontradoException.class,
                () -> assistenciaSocialService.atualizar(999L, request));
    }

    @Test
    void atualizarDeveSalvarERetornarResponse() {
        AssistenciaSocial existente = AssistenciaSocial.builder().id(1L).nomeAlimento("Antigo").quantidade(BigDecimal.ONE)
                .dataValidade(LocalDate.now()).dataRegistro(LocalDateTime.now()).build();
        when(assistenciaSocialRepository.findById(1L)).thenReturn(Optional.of(existente));
        existente.setNomeAlimento("Novo");
        when(assistenciaSocialRepository.save(any())).thenReturn(existente);
        AtualizarAssistenciaSocialRequest request = new AtualizarAssistenciaSocialRequest("Novo", null, null, null, null, null);

        AssistenciaSocialResponse response = assistenciaSocialService.atualizar(1L, request);

        assertEquals("Novo", response.nomeAlimento());
        verify(assistenciaSocialRepository).save(existente);
    }

    @Test
    void deletarDeveLancarQuandoNaoExiste() {
        when(assistenciaSocialRepository.existsById(999L)).thenReturn(false);

        assertThrows(AssistenciaSocialNaoEncontradoException.class, () -> assistenciaSocialService.deletar(999L));
        verify(assistenciaSocialRepository, never()).deleteById(any());
    }

    @Test
    void deletarDeveChamarDeleteQuandoExiste() {
        when(assistenciaSocialRepository.existsById(1L)).thenReturn(true);

        assistenciaSocialService.deletar(1L);

        verify(assistenciaSocialRepository).deleteById(1L);
    }
}
