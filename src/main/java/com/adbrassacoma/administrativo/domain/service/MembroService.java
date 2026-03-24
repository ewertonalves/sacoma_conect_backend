package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.Endereco;
import com.adbrassacoma.administrativo.domain.model.Membros;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroMembroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.EnderecoRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.EnderecoResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.MembroResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.CpfInvalidoException;
import com.adbrassacoma.administrativo.infrastructure.exception.CpfJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.MembroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.RgJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.RiJaCadastradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.EnderecoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.MembrosRepository;
import com.adbrassacoma.administrativo.infrastructure.validator.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembroService {

    private final MembrosRepository membrosRepository;
    private final EnderecoRepository enderecoRepository;

    @Transactional
    public MembroResponse cadastrar(CadastroMembroRequest request) {
        String nome = blankToNull(request.nome());
        String rg = blankToNull(request.rg());
        String cpfBruto = blankToNull(request.cpf());
        String ri = blankToNull(request.ri());
        String cargo = blankToNull(request.cargo());

        log.info("Iniciando cadastro de membro. Nome: {}, CPF: {}", nome, cpfBruto);

        validarCpf(cpfBruto);
        validarCpfUnico(cpfBruto, null);
        validarRgUnico(rg, null);

        if (ri != null) {
            validarRiUnico(ri, null);
        }

        String cpfLimpo = CpfValidator.unformat(cpfBruto);

        Endereco endereco = null;
        if (request.endereco() != null && !isEnderecoTotalmenteVazio(request.endereco())) {
            endereco = criarEndereco(normalizeEnderecoRequest(request.endereco()));
            endereco = enderecoRepository.save(endereco);
        }

        Membros membro = Membros.builder()
                .nome(nome)
                .rg(rg)
                .cpf(cpfLimpo)
                .ri(ri)
                .cargo(cargo)
                .endereco(endereco)
                .build();

        membro = membrosRepository.save(membro);
        log.info("Membro cadastrado com sucesso. ID: {}, Nome: {}, CPF: {}", membro.getId(), membro.getNome(), cpfBruto);

        return toMembroResponse(membro);
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> listarTodos() {
        return membrosRepository.findAll().stream()
                .map(this::toMembroResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MembroResponse buscarPorId(Long id) {
        Membros membro = membrosRepository.findById(id)
                .orElseThrow(() -> new MembroNaoEncontradoException("Membro não encontrado com ID: " + id));
        return toMembroResponse(membro);
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> buscarPorNome(String nome) {
        List<Membros> membros = membrosRepository.findByNomeContainingIgnoreCase(nome);
        
        return membros.stream()
                .map(this::toMembroResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MembroResponse buscarPorCpf(String cpf) {
        String cpfLimpo = CpfValidator.unformat(cpf);
        if (cpfLimpo == null) {
            throw new CpfInvalidoException("CPF inválido: " + cpf);
        }
        
        Membros membro = membrosRepository.findByCpf(cpfLimpo)
                .orElseThrow(() -> new MembroNaoEncontradoException("Membro não encontrado com CPF: " + cpf));
        return toMembroResponse(membro);
    }

    @Transactional(readOnly = true)
    public MembroResponse buscarPorRi(String ri) {
        Membros membro = membrosRepository.findByRi(ri)
                .orElseThrow(() -> new MembroNaoEncontradoException("Membro não encontrado com RI: " + ri));
        return toMembroResponse(membro);
    }

    @Transactional
    public MembroResponse atualizar(Long id, AtualizarMembroRequest request) {
        log.info("Iniciando atualização de membro. ID: {}", id);
        
        Membros membro = membrosRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar membro inexistente. ID: {}", id);
                    return new MembroNaoEncontradoException("Membro não encontrado com ID: " + id);
                });

        validarRgUnico(request.rg(), id);
        
        if (request.ri() != null && !request.ri().isBlank()) {
            validarRiUnico(request.ri(), id);
        }

        membro.setNome(request.nome());
        membro.setRg(request.rg());
        membro.setRi(request.ri());
        membro.setCargo(request.cargo());

        if (membro.getEndereco() == null) {
            if (request.endereco() != null && !isEnderecoTotalmenteVazio(request.endereco())) {
                Endereco novo = criarEndereco(normalizeEnderecoRequest(request.endereco()));
                membro.setEndereco(enderecoRepository.save(novo));
            }
        } else if (request.endereco() != null) {
            atualizarEndereco(membro.getEndereco(), normalizeEnderecoRequest(request.endereco()));
        }

        membro = membrosRepository.save(membro);
        log.info("Membro atualizado com sucesso. ID: {}, Nome: {}", membro.getId(), membro.getNome());

        return toMembroResponse(membro);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão de membro. ID: {}", id);
        
        if (!membrosRepository.existsById(id)) {
            log.warn("Tentativa de deletar membro inexistente. ID: {}", id);
            throw new MembroNaoEncontradoException("Membro não encontrado com ID: " + id);
        }
        
        membrosRepository.deleteById(id);
        log.info("Membro deletado com sucesso. ID: {}", id);
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return;
        }
        if (!CpfValidator.isValid(cpf)) {
            throw new CpfInvalidoException("CPF inválido: " + cpf);
        }
    }

    private void validarCpfUnico(String cpf, Long idExcluir) {
        String cpfLimpo = CpfValidator.unformat(cpf);
        if (cpfLimpo == null) {
            return;
        }
        membrosRepository.findByCpf(cpfLimpo)
                .ifPresent(membro -> {
                    if (idExcluir == null || !membro.getId().equals(idExcluir)) {
                        throw new CpfJaCadastradoException("CPF já cadastrado no sistema");
                    }
                });
    }

    private void validarRgUnico(String rg, Long idExcluir) {
        if (rg == null || rg.isBlank()) {
            return;
        }
        membrosRepository.findByRg(rg)
                .ifPresent(membro -> {
                    if (idExcluir == null || !membro.getId().equals(idExcluir)) {
                        throw new RgJaCadastradoException("RG já cadastrado no sistema");
                    }
                });
    }

    private void validarRiUnico(String ri, Long idExcluir) {
        if (ri == null || ri.isBlank()) {
            return;
        }
        membrosRepository.findByRi(ri)
                .ifPresent(membro -> {
                    if (idExcluir == null || !membro.getId().equals(idExcluir)) {
                        throw new RiJaCadastradoException("RI já cadastrado no sistema");
                    }
                });
    }

    private Endereco criarEndereco(EnderecoRequest request) {
        return Endereco.builder()
                .rua(request.rua())
                .numero(request.numero())
                .cep(request.cep())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .estado(request.estado())
                .complemento(request.complemento())
                .build();
    }

    private void atualizarEndereco(Endereco endereco, EnderecoRequest request) {
        endereco.setRua(request.rua());
        endereco.setNumero(request.numero());
        endereco.setCep(request.cep());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        endereco.setComplemento(request.complemento());
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean isEnderecoTotalmenteVazio(EnderecoRequest r) {
        if (r == null) {
            return true;
        }
        return isBlank(r.rua()) && isBlank(r.numero()) && isBlank(r.cep()) && isBlank(r.bairro())
                && isBlank(r.cidade()) && isBlank(r.estado()) && isBlank(r.complemento());
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static EnderecoRequest normalizeEnderecoRequest(EnderecoRequest r) {
        if (r == null) {
            return null;
        }
        return new EnderecoRequest(
                blankToNull(r.rua()),
                blankToNull(r.numero()),
                blankToNull(r.cep()),
                blankToNull(r.bairro()),
                blankToNull(r.cidade()),
                blankToNull(r.estado()),
                blankToNull(r.complemento()));
    }

    private MembroResponse toMembroResponse(Membros membro) {
        EnderecoResponse enderecoResponse = membro.getEndereco() != null
                ? toEnderecoResponse(membro.getEndereco())
                : null;
        return new MembroResponse(
                membro.getId(),
                membro.getNome(),
                membro.getRg(),
                CpfValidator.format(membro.getCpf()),
                membro.getRi(),
                membro.getCargo(),
                enderecoResponse);
    }

    private EnderecoResponse toEnderecoResponse(Endereco endereco) {
        return new EnderecoResponse(
                endereco.getId(),
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getCep(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getComplemento()
        );
    }
}
