package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.enums.CategoriaFinanceira;
import com.adbrassacoma.administrativo.domain.enums.TipoMovimentacaoFinanceira;
import com.adbrassacoma.administrativo.domain.model.CodigoFinanceiroDefinicao;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CodigoFinanceiroCatalog {

    private static final List<CodigoFinanceiroDefinicao> TODOS = List.of(
            new CodigoFinanceiroDefinicao(1, "Dizimo", TipoMovimentacaoFinanceira.ENTRADA, CategoriaFinanceira.DIZIMO),
            new CodigoFinanceiroDefinicao(2, "Oferta", TipoMovimentacaoFinanceira.ENTRADA, CategoriaFinanceira.OFERTA),
            new CodigoFinanceiroDefinicao(3, "Oferta Missionaria", TipoMovimentacaoFinanceira.ENTRADA,
                    CategoriaFinanceira.OFERTA_MISSIONARIA),
            new CodigoFinanceiroDefinicao(4, "Oferta Especial", TipoMovimentacaoFinanceira.ENTRADA,
                    CategoriaFinanceira.OFERTA_ESPECIAL),
            new CodigoFinanceiroDefinicao(5, "Doacao", TipoMovimentacaoFinanceira.ENTRADA, CategoriaFinanceira.DOACAO),
            new CodigoFinanceiroDefinicao(6, "Contribuicao", TipoMovimentacaoFinanceira.ENTRADA,
                    CategoriaFinanceira.CONTRIBUICAO),
            new CodigoFinanceiroDefinicao(7, "Outras Entradas", TipoMovimentacaoFinanceira.ENTRADA,
                    CategoriaFinanceira.OUTRAS_ENTRADAS),
            new CodigoFinanceiroDefinicao(100, "Agua", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.AGUA),
            new CodigoFinanceiroDefinicao(101, "Luz", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.LUZ),
            new CodigoFinanceiroDefinicao(102, "Aluguel", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.ALUGUEL),
            new CodigoFinanceiroDefinicao(103, "Manutencao", TipoMovimentacaoFinanceira.SAIDA,
                    CategoriaFinanceira.MANUTENCAO),
            new CodigoFinanceiroDefinicao(104, "Limpeza", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.LIMPEZA),
            new CodigoFinanceiroDefinicao(105, "Material", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.MATERIAL),
            new CodigoFinanceiroDefinicao(106, "Ajuda Social", TipoMovimentacaoFinanceira.SAIDA,
                    CategoriaFinanceira.AJUDA_SOCIAL),
            new CodigoFinanceiroDefinicao(107, "Transporte", TipoMovimentacaoFinanceira.SAIDA,
                    CategoriaFinanceira.TRANSPORTE),
            new CodigoFinanceiroDefinicao(108, "Eventos", TipoMovimentacaoFinanceira.SAIDA, CategoriaFinanceira.EVENTOS),
            new CodigoFinanceiroDefinicao(109, "Oferta para Sede", TipoMovimentacaoFinanceira.SAIDA,
                    CategoriaFinanceira.OFERTA_SEDE),
            new CodigoFinanceiroDefinicao(110, "Outras Saidas", TipoMovimentacaoFinanceira.SAIDA,
                    CategoriaFinanceira.OUTRAS_SAIDAS));

    private final Map<Integer, CodigoFinanceiroDefinicao> porCodigo = TODOS.stream()
            .collect(Collectors.toUnmodifiableMap(CodigoFinanceiroDefinicao::codigo, d -> d));

    public List<CodigoFinanceiroDefinicao> listarTodos() {
        return Collections.unmodifiableList(TODOS);
    }

    public Optional<CodigoFinanceiroDefinicao> buscar(int codigo) {
        return Optional.ofNullable(porCodigo.get(codigo));
    }
}
