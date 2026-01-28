package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.AssistenciaSocial;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroAssistenciaSocialRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.AssistenciaSocialResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.AssistenciaSocialNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.AssistenciaSocialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistenciaSocialService {

    private final AssistenciaSocialRepository assistenciaSocialRepository;

    @Transactional
    public AssistenciaSocialResponse cadastrar(CadastroAssistenciaSocialRequest request) {
        log.info("Iniciando cadastro de assistência social. Nome do alimento: {}, Quantidade: {}",
                request.nomeAlimento(), request.quantidade());

        AssistenciaSocial assistenciaSocial = AssistenciaSocial.builder()
                .nomeAlimento(request.nomeAlimento())
                .quantidade(request.quantidade())
                .dataValidade(request.dataValidade())
                .familiaBeneficiada(request.familiaBeneficiada())
                .quantidadeCestasBasicas(request.quantidadeCestasBasicas())
                .dataEntregaCesta(request.dataEntregaCesta())
                .build();

        assistenciaSocial = assistenciaSocialRepository.save(assistenciaSocial);
        log.info("Assistência social cadastrada com sucesso. ID: {}, Nome do alimento: {}",
                assistenciaSocial.getId(), assistenciaSocial.getNomeAlimento());

        return toAssistenciaSocialResponse(assistenciaSocial);
    }

    @Transactional(readOnly = true)
    public Page<AssistenciaSocialResponse> listarTodos(Pageable pageable, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return assistenciaSocialRepository.buscarComFiltro(search.trim(), pageable)
                    .map(this::toAssistenciaSocialResponse);
        }
        return assistenciaSocialRepository.findAll(pageable)
                .map(this::toAssistenciaSocialResponse);
    }

    @Transactional(readOnly = true)
    public AssistenciaSocialResponse buscarPorId(Long id) {
        AssistenciaSocial assistenciaSocial = assistenciaSocialRepository.findById(id)
                .orElseThrow(() -> new AssistenciaSocialNaoEncontradoException(
                        "Assistência social não encontrada com ID: " + id));
        return toAssistenciaSocialResponse(assistenciaSocial);
    }

    @Transactional
    public AssistenciaSocialResponse atualizar(Long id, AtualizarAssistenciaSocialRequest request) {
        log.info("Iniciando atualização de assistência social. ID: {}", id);

        AssistenciaSocial assistenciaSocial = assistenciaSocialRepository.findById(id).orElseThrow(() -> {
            log.warn("Tentativa de atualizar assistência social inexistente. ID: {}", id);
            return new AssistenciaSocialNaoEncontradoException(
                    "Assistência social não encontrada com ID: " + id);
        });

        aplicarAtualizacoes(assistenciaSocial, request);

        assistenciaSocial = assistenciaSocialRepository.save(assistenciaSocial);
        log.info("Assistência social atualizada com sucesso. ID: {}, Nome do alimento: {}",
                assistenciaSocial.getId(), assistenciaSocial.getNomeAlimento());

        return toAssistenciaSocialResponse(assistenciaSocial);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão de assistência social. ID: {}", id);

        if (!assistenciaSocialRepository.existsById(id)) {
            log.warn("Tentativa de deletar assistência social inexistente. ID: {}", id);
            throw new AssistenciaSocialNaoEncontradoException("Assistência social não encontrada com ID: " + id);
        }

        assistenciaSocialRepository.deleteById(id);
        log.info("Assistência social deletada com sucesso. ID: {}", id);
    }

    private AssistenciaSocialResponse toAssistenciaSocialResponse(AssistenciaSocial assistenciaSocial) {
        return new AssistenciaSocialResponse(
                assistenciaSocial.getId(),
                assistenciaSocial.getNomeAlimento(),
                assistenciaSocial.getQuantidade(),
                assistenciaSocial.getDataValidade(),
                assistenciaSocial.getFamiliaBeneficiada(),
                assistenciaSocial.getQuantidadeCestasBasicas(),
                assistenciaSocial.getDataEntregaCesta(),
                assistenciaSocial.getDataRegistro());
    }

    private void aplicarAtualizacoes(AssistenciaSocial assistenciaSocial, AtualizarAssistenciaSocialRequest request) {
        atualizarSeNaoNulo(assistenciaSocial, request.nomeAlimento(), AssistenciaSocial::setNomeAlimento);
        atualizarSeNaoNulo(assistenciaSocial, request.quantidade(), AssistenciaSocial::setQuantidade);
        atualizarSeNaoNulo(assistenciaSocial, request.dataValidade(), AssistenciaSocial::setDataValidade);
        atualizarSeNaoNulo(assistenciaSocial, request.familiaBeneficiada(), AssistenciaSocial::setFamiliaBeneficiada);
        atualizarSeNaoNulo(assistenciaSocial, request.quantidadeCestasBasicas(), AssistenciaSocial::setQuantidadeCestasBasicas);
        atualizarSeNaoNulo(assistenciaSocial, request.dataEntregaCesta(), AssistenciaSocial::setDataEntregaCesta);
    }

    private <T> void atualizarSeNaoNulo(AssistenciaSocial assistenciaSocial, T valor, BiConsumer<AssistenciaSocial, T> setter) {
        if (valor != null) {
            setter.accept(assistenciaSocial, valor);
        }
    }
}
