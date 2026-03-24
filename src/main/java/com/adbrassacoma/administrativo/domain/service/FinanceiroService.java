package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoPeriodoRelatorio;
import com.adbrassacoma.administrativo.domain.model.CodigoFinanceiroDefinicao;
import com.adbrassacoma.administrativo.domain.model.Financeiro;
import com.adbrassacoma.administrativo.domain.model.Membros;
import com.adbrassacoma.administrativo.infrastructure.dto.request.AtualizarFinanceiroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.request.CadastroFinanceiroRequest;
import com.adbrassacoma.administrativo.infrastructure.dto.response.FinanceiroResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.MembroFinanceiroResponse;
import com.adbrassacoma.administrativo.infrastructure.dto.response.RelatorioFinanceiroResponse;
import com.adbrassacoma.administrativo.infrastructure.exception.FinanceiroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.exception.MembroNaoEncontradoException;
import com.adbrassacoma.administrativo.infrastructure.repository.FinanceiroRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.MembrosRepository;
import com.adbrassacoma.administrativo.infrastructure.validator.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceiroService {

    private final FinanceiroRepository financeiroRepository;
    private final MembrosRepository membrosRepository;
    private final CodigoFinanceiroCatalog codigoFinanceiroCatalog;

    @Transactional
    public FinanceiroResponse cadastrar(CadastroFinanceiroRequest request) {
        CodigoFinanceiroDefinicao definicao = resolverCodigo(request.codigoFinanceiro());

        BigDecimal entrada = request.entrada() != null ? request.entrada() : BigDecimal.ZERO;
        BigDecimal saida = request.saida() != null ? request.saida() : BigDecimal.ZERO;
        validarValoresPorTipo(definicao.tipo(), entrada, saida);

        log.info("Iniciando cadastro de registro financeiro. Código: {}, tipo: {}, Entrada: {}, Saída: {}",
                definicao.codigo(), definicao.tipo(), entrada, saida);

        Membros membro = null;
        if (request.membroId() != null) {
            log.debug("Buscando membro associado. Membro ID: {}", request.membroId());
            membro = membrosRepository.findById(request.membroId())
                    .orElseThrow(() -> {
                        log.warn("Membro não encontrado para registro financeiro. Membro ID: {}", request.membroId());
                        return new MembroNaoEncontradoException("Membro não encontrado com ID: " + request.membroId());
                    });
        }

        Financeiro financeiro = Financeiro.builder()
                .codigoFinanceiro(definicao.codigo())
                .tipo(definicao.tipo())
                .categoria(definicao.categoria())
                .entrada(entrada)
                .saida(saida)
                .observacao(request.observacao())
                .membro(membro)
                .build();

        financeiro = financeiroRepository.save(financeiro);
        log.info("Registro financeiro cadastrado com sucesso. ID: {}, código: {}", financeiro.getId(),
                financeiro.getCodigoFinanceiro());

        return toFinanceiroResponse(financeiro);
    }

    @Transactional(readOnly = true)
    public List<FinanceiroResponse> listarTodos() {
        return financeiroRepository.findAll().stream()
                .map(this::toFinanceiroResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceiroResponse buscarPorId(Long id) {
        Financeiro financeiro = financeiroRepository.findById(id)
                .orElseThrow(() -> new FinanceiroNaoEncontradoException("Financeiro não encontrado com ID: " + id));
        return toFinanceiroResponse(financeiro);
    }

    @Transactional(readOnly = true)
    public List<FinanceiroResponse> buscarPorTipo(TipoMovimentacaoFinanceira tipo) {
        List<Financeiro> financeiros = financeiroRepository.findByTipo(tipo);

        if (financeiros.isEmpty()) {
            throw new FinanceiroNaoEncontradoException("Nenhum registro financeiro encontrado com tipo: " + tipo);
        }

        return financeiros.stream()
                .map(this::toFinanceiroResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FinanceiroResponse> buscarPorMembro(Long membroId) {
        if (!membrosRepository.existsById(membroId)) {
            throw new MembroNaoEncontradoException("Membro não encontrado com ID: " + membroId);
        }

        List<Financeiro> financeiros = financeiroRepository.findByMembroId(membroId);

        if (financeiros.isEmpty()) {
            throw new FinanceiroNaoEncontradoException(
                    "Nenhum registro financeiro encontrado para o membro com ID: " + membroId);
        }

        return financeiros.stream()
                .map(this::toFinanceiroResponse)
                .toList();
    }

    @Transactional
    public FinanceiroResponse atualizar(Long id, AtualizarFinanceiroRequest request) {
        log.info("Iniciando atualização de registro financeiro. ID: {}", id);

        Financeiro financeiro = financeiroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar registro financeiro inexistente. ID: {}", id);
                    return new FinanceiroNaoEncontradoException("Financeiro não encontrado com ID: " + id);
                });

        CodigoFinanceiroDefinicao definicao = resolverCodigo(request.codigoFinanceiro());
        BigDecimal entrada = request.entrada() != null ? request.entrada() : BigDecimal.ZERO;
        BigDecimal saida = request.saida() != null ? request.saida() : BigDecimal.ZERO;
        validarValoresPorTipo(definicao.tipo(), entrada, saida);

        Membros membro = null;
        if (request.membroId() != null) {
            log.debug("Buscando membro associado para atualização. Membro ID: {}", request.membroId());
            membro = membrosRepository.findById(request.membroId())
                    .orElseThrow(() -> {
                        log.warn("Membro não encontrado para atualização de registro financeiro. Membro ID: {}",
                                request.membroId());
                        return new MembroNaoEncontradoException("Membro não encontrado com ID: " + request.membroId());
                    });
        }

        financeiro.setCodigoFinanceiro(definicao.codigo());
        financeiro.setTipo(definicao.tipo());
        financeiro.setCategoria(definicao.categoria());
        financeiro.setEntrada(entrada);
        financeiro.setSaida(saida);
        financeiro.setObservacao(request.observacao());
        financeiro.setMembro(membro);

        financeiro = financeiroRepository.save(financeiro);
        log.info("Registro financeiro atualizado com sucesso. ID: {}, código: {}", financeiro.getId(),
                financeiro.getCodigoFinanceiro());

        return toFinanceiroResponse(financeiro);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Iniciando exclusão de registro financeiro. ID: {}", id);

        if (!financeiroRepository.existsById(id)) {
            log.warn("Tentativa de deletar registro financeiro inexistente. ID: {}", id);
            throw new FinanceiroNaoEncontradoException("Financeiro não encontrado com ID: " + id);
        }

        financeiroRepository.deleteById(id);
        log.info("Registro financeiro deletado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public RelatorioFinanceiroResponse obterDadosRelatorio(LocalDate dataInicial, LocalDate dataFinal,
            TipoPeriodoRelatorio tipoPeriodo) {
        log.info("Obtendo dados para relatório financeiro. Tipo: {}, Data inicial: {}", tipoPeriodo, dataInicial);

        if (dataInicial == null) {
            throw new IllegalArgumentException("Data inicial é obrigatória para o relatório");
        }

        LocalDate dataInicioPeriodo;
        LocalDate dataFimPeriodo;

        switch (tipoPeriodo) {
            case PERSONALIZADO -> {
                if (dataFinal == null) {
                    throw new IllegalArgumentException("Data final é obrigatória para período personalizado");
                }
                if (dataFinal.isBefore(dataInicial)) {
                    throw new IllegalArgumentException("Data final não pode ser anterior à data inicial");
                }
                dataInicioPeriodo = dataInicial;
                dataFimPeriodo = dataFinal;
            }
            case SEMANAL -> {
                dataInicioPeriodo = dataInicial.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                dataFimPeriodo = dataInicial.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }
            case MENSAL -> {
                dataInicioPeriodo = dataInicial.with(TemporalAdjusters.firstDayOfMonth());
                dataFimPeriodo = dataInicial.with(TemporalAdjusters.lastDayOfMonth());
            }
            default -> throw new IllegalArgumentException("Tipo de período inválido: " + tipoPeriodo);
        }

        LocalDateTime inicio = dataInicioPeriodo.atStartOfDay();
        LocalDateTime fim = dataFimPeriodo.atTime(23, 59, 59, 999_999_999);

        List<Financeiro> financeiros = financeiroRepository.findByDataRegistroBetween(inicio, fim);
        List<FinanceiroResponse> itens = financeiros.stream()
                .map(this::toFinanceiroResponse)
                .toList();

        BigDecimal totalEntrada = financeiros.stream()
                .map(Financeiro::getEntrada)
                .filter(e -> e != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSaida = financeiros.stream()
                .map(Financeiro::getSaida)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = totalEntrada.subtract(totalSaida);

        log.info("Relatório financeiro: {} itens, total entrada: {}, total saída: {}, saldo: {}",
                itens.size(), totalEntrada, totalSaida, saldo);

        return new RelatorioFinanceiroResponse(
                dataInicioPeriodo,
                dataFimPeriodo,
                tipoPeriodo,
                itens,
                totalEntrada,
                totalSaida,
                saldo);
    }

    private CodigoFinanceiroDefinicao resolverCodigo(Integer codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("Código financeiro é obrigatório");
        }
        return codigoFinanceiroCatalog.buscar(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Código financeiro inválido: " + codigo));
    }

    private void validarValoresPorTipo(TipoMovimentacaoFinanceira tipo, BigDecimal entrada, BigDecimal saida) {
        final BigDecimal valorEntrada = Objects.requireNonNullElse(entrada, BigDecimal.ZERO);
        final BigDecimal valorSaida = Objects.requireNonNullElse(saida, BigDecimal.ZERO);

        if (valorEntrada.signum() < 0 || valorSaida.signum() < 0) {
            throw new IllegalArgumentException("Valores de entrada e saída não podem ser negativos");
        }

        final boolean temEntrada = valorEntrada.signum() > 0;
        final boolean temSaida = valorSaida.signum() > 0;

        switch (tipo) {
            case ENTRADA -> {
                if (!temEntrada || temSaida) {
                    throw new IllegalArgumentException(!temEntrada
                            ? "Para código de entrada, o valor de entrada deve ser maior que zero"
                            : "Para código de entrada, o valor de saída deve ser zero");
                }
            }
            case SAIDA -> {
                if (!temSaida || temEntrada) {
                    throw new IllegalArgumentException(!temSaida
                            ? "Para código de saída, o valor de saída deve ser maior que zero"
                            : "Para código de saída, o valor de entrada deve ser zero");
                }
            }
        }
    }

    private FinanceiroResponse toFinanceiroResponse(Financeiro financeiro) {
        MembroFinanceiroResponse membroResponse = null;
        if (financeiro.getMembro() != null) {
            membroResponse = new MembroFinanceiroResponse(
                    financeiro.getMembro().getId(),
                    financeiro.getMembro().getNome(),
                    CpfValidator.format(financeiro.getMembro().getCpf()));
        }

        String descricaoCodigo = codigoFinanceiroCatalog.buscar(financeiro.getCodigoFinanceiro())
                .map(CodigoFinanceiroDefinicao::descricao)
                .orElse("");

        return new FinanceiroResponse(
                financeiro.getId(),
                financeiro.getCodigoFinanceiro(),
                descricaoCodigo,
                financeiro.getTipo(),
                financeiro.getCategoria(),
                financeiro.getEntrada(),
                financeiro.getSaida(),
                financeiro.getObservacao(),
                financeiro.getDataRegistro(),
                membroResponse);
    }
}
