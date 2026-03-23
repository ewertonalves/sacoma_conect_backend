package com.adbrassacoma.administrativo.infrastructure.config;

import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import com.adbrassacoma.administrativo.domain.service.TelaPermissaoDiscoveryService;
import com.adbrassacoma.administrativo.infrastructure.repository.TelaPermissaoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("DataInitializer")
class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TelaPermissaoRepository telaPermissaoRepository;

    @MockitoBean
    private TelaPermissaoDiscoveryService telaPermissaoDiscoveryService;

    @Test
    void runNaoDeveLancarExcecaoQuandoAdminNaoExiste() throws Exception {
        when(usuarioRepository.existsByEmail("admin@administrativo.com")).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TelaPermissao tela = TelaPermissao.builder().id("membros").nome("Membros").rota("/membros").build();
        when(telaPermissaoDiscoveryService.descobrirTelas()).thenReturn(List.of(tela));
        when(telaPermissaoRepository.existsById("membros")).thenReturn(false);
        when(telaPermissaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        dataInitializer.run();

        verify(usuarioRepository).existsByEmail("admin@administrativo.com");
        verify(usuarioRepository).save(any());
    }

    @Test
    void runNaoDeveCriarAdminQuandoJaExiste() throws Exception {
        when(usuarioRepository.existsByEmail("admin@administrativo.com")).thenReturn(true);
        when(telaPermissaoDiscoveryService.descobrirTelas()).thenReturn(List.of());

        dataInitializer.run();

        verify(usuarioRepository, atLeastOnce()).existsByEmail("admin@administrativo.com");
        verify(telaPermissaoDiscoveryService, atLeastOnce()).descobrirTelas();
    }
}
