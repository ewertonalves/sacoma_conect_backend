package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.Endereco;
import com.adbrassacoma.administrativo.domain.model.Membros;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.EnderecoRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.exception.CpfInvalidoException;
import com.adbrassacoma.administrativo.infrastructure.exception.CpfJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.MembroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.EnderecoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.MembrosRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MembroService")
class MembroServiceTest {

    @Mock
    private MembrosRepository membrosRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private MembroService membroService;

    private static EnderecoRequest enderecoRequest() {
        return new EnderecoRequest("Rua A", "100", "01310100", "Centro", "São Paulo", "SP", null);
    }

    @Test
    void cadastrarDeveLancarQuandoCpfInvalido() {
        CadastroMembroRequest request = new CadastroMembroRequest("Nome", "RG", "11111111111", "RI", "Cargo", enderecoRequest());

        assertThrows(CpfInvalidoException.class, () -> membroService.cadastrar(request));
        verify(membrosRepository, never()).save(any());
    }

    @Test
    void cadastrarDeveLancarQuandoCpfRgOuRiJaExiste() {
        CadastroMembroRequest request = new CadastroMembroRequest("Nome", "123", "111.444.777-35", "RI1", "Cargo", enderecoRequest());
        Membros existente = Membros.builder().id(2L).nome("X").rg("R").cpf("11144477735").endereco(Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build()).build();
        when(membrosRepository.findByCpf("11144477735")).thenReturn(Optional.of(existente));

        assertThrows(CpfJaCadastradoException.class, () -> membroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveSalvarERetornarResponse() {
        CadastroMembroRequest request = new CadastroMembroRequest("João", "RG1", "111.444.777-35", "RI1", "Pastor", enderecoRequest());
        when(membrosRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(membrosRepository.findByRg("RG1")).thenReturn(Optional.empty());
        when(membrosRepository.findByRi("RI1")).thenReturn(Optional.empty());
        Endereco endereco = Endereco.builder().id(1L).rua("Rua A").numero("100").cep("01310100").bairro("Centro").cidade("São Paulo").estado("SP").build();
        when(enderecoRepository.save(any())).thenReturn(endereco);
        Membros membro = Membros.builder().id(1L).nome("João").rg("RG1").cpf("11144477735").ri("RI1").cargo("Pastor").endereco(endereco).build();
        when(membrosRepository.save(any())).thenReturn(membro);

        var response = membroService.cadastrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("João", response.nome());
        assertEquals("111.444.777-35", response.cpf());
        verify(membrosRepository).save(any());
    }

    @Test
    void buscarPorIdDeveLancarQuandoNaoEncontrado() {
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MembroNaoEncontradoException.class, () -> membroService.buscarPorId(999L));
    }

    @Test
    void buscarPorCpfDeveLancarQuandoCpfInvalido() {
        assertThrows(CpfInvalidoException.class, () -> membroService.buscarPorCpf("000"));
    }

    @Test
    void buscarPorCpfDeveLancarQuandoMembroNaoEncontrado() {
        when(membrosRepository.findByCpf("12345678976")).thenReturn(Optional.empty());

        when(membrosRepository.findByCpf("11144477735")).thenReturn(Optional.empty());
        assertThrows(MembroNaoEncontradoException.class, () -> membroService.buscarPorCpf("111.444.777-35"));
    }

    @Test
    void buscarPorRiDeveLancarQuandoNaoEncontrado() {
        when(membrosRepository.findByRi("RI999")).thenReturn(Optional.empty());

        assertThrows(MembroNaoEncontradoException.class, () -> membroService.buscarPorRi("RI999"));
    }

    @Test
    void listarTodosDeveRetornarLista() {
        Endereco e = Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build();
        Membros m = Membros.builder().id(1L).nome("M").rg("R").cpf("11144477735").endereco(e).build();
        when(membrosRepository.findAll()).thenReturn(List.of(m));

        var list = membroService.listarTodos();

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id());
    }

    @Test
    void atualizarDeveLancarQuandoMembroNaoEncontrado() {
        when(membrosRepository.findById(999L)).thenReturn(Optional.empty());
        AtualizarMembroRequest req = new AtualizarMembroRequest("Nome", "RG", "RI", "Cargo", enderecoRequest());

        assertThrows(MembroNaoEncontradoException.class, () -> membroService.atualizar(999L, req));
    }

    @Test
    void deletarDeveLancarQuandoNaoExiste() {
        when(membrosRepository.existsById(999L)).thenReturn(false);

        assertThrows(MembroNaoEncontradoException.class, () -> membroService.deletar(999L));
        verify(membrosRepository, never()).deleteById(any());
    }

    @Test
    void buscarPorNomeDeveRetornarLista() {
        Endereco e = Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build();
        Membros m = Membros.builder().id(1L).nome("João Silva").rg("R").cpf("11144477735").endereco(e).build();
        when(membrosRepository.findByNomeContainingIgnoreCase("João")).thenReturn(List.of(m));

        var list = membroService.buscarPorNome("João");

        assertEquals(1, list.size());
        assertEquals("João Silva", list.get(0).nome());
    }

    @Test
    void buscarPorCpfDeveRetornarMembroQuandoEncontrado() {
        Endereco e = Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build();
        Membros m = Membros.builder().id(1L).nome("M").rg("R").cpf("11144477735").endereco(e).build();
        when(membrosRepository.findByCpf("11144477735")).thenReturn(Optional.of(m));

        var response = membroService.buscarPorCpf("111.444.777-35");

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("111.444.777-35", response.cpf());
    }

    @Test
    void buscarPorRiDeveRetornarMembroQuandoEncontrado() {
        Endereco e = Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build();
        Membros m = Membros.builder().id(1L).nome("M").rg("R").cpf("11144477735").ri("RI1").endereco(e).build();
        when(membrosRepository.findByRi("RI1")).thenReturn(Optional.of(m));

        var response = membroService.buscarPorRi("RI1");

        assertNotNull(response);
        assertEquals("RI1", response.ri());
    }

    @Test
    void atualizarDeveSalvarERetornarResponse() {
        Endereco endereco = Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build();
        Membros membro = Membros.builder().id(1L).nome("Antigo").rg("RG1").cpf("11144477735").endereco(endereco).build();
        when(membrosRepository.findById(1L)).thenReturn(Optional.of(membro));
        when(membrosRepository.findByRg("RG2")).thenReturn(Optional.empty());
        when(membrosRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        AtualizarMembroRequest req = new AtualizarMembroRequest("Novo Nome", "RG2", null, "Cargo", enderecoRequest());

        var response = membroService.atualizar(1L, req);

        assertNotNull(response);
        assertEquals("Novo Nome", response.nome());
        verify(membrosRepository).save(any());
    }

    @Test
    void cadastrarDeveLancarQuandoRgJaExiste() {
        CadastroMembroRequest request = new CadastroMembroRequest("Nome", "RG123", "111.444.777-35", null, "Cargo", enderecoRequest());
        when(membrosRepository.findByCpf("11144477735")).thenReturn(Optional.empty());
        Membros existente = Membros.builder().id(2L).nome("X").rg("RG123").cpf("outro").endereco(Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build()).build();
        when(membrosRepository.findByRg("RG123")).thenReturn(Optional.of(existente));

        assertThrows(com.adbrassacoma.administrativo.infrastructure.exception.RgJaCadastradoException.class,
                () -> membroService.cadastrar(request));
    }

    @Test
    void cadastrarDeveLancarQuandoRiJaExiste() {
        CadastroMembroRequest request = new CadastroMembroRequest("Nome", "RG1", "111.444.777-35", "RI-X", "Cargo", enderecoRequest());
        when(membrosRepository.findByCpf("11144477735")).thenReturn(Optional.empty());
        when(membrosRepository.findByRg("RG1")).thenReturn(Optional.empty());
        Membros existente = Membros.builder().id(2L).nome("X").rg("R").cpf("outro").ri("RI-X").endereco(Endereco.builder().id(1L).rua("R").numero("1").cep("1").bairro("B").cidade("C").estado("SP").build()).build();
        when(membrosRepository.findByRi("RI-X")).thenReturn(Optional.of(existente));

        assertThrows(com.adbrassacoma.administrativo.infrastructure.exception.RiJaCadastradoException.class,
                () -> membroService.cadastrar(request));
    }
}
