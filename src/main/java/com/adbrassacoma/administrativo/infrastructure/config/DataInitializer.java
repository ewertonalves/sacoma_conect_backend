package com.adbrassacoma.administrativo.infrastructure.config;

import com.adbrassacoma.administrativo.domain.enums.Role;
import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import com.adbrassacoma.administrativo.domain.model.Usuario;
import com.adbrassacoma.administrativo.domain.service.TelaPermissaoDiscoveryService;
import com.adbrassacoma.administrativo.infrastructure.repository.TelaPermissaoRepository;
import com.adbrassacoma.administrativo.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TelaPermissaoRepository telaPermissaoRepository;
    private final TelaPermissaoDiscoveryService telaPermissaoDiscoveryService;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdmin();
        criarTelasPermissao();
    }

    private void criarUsuarioAdmin() {
        String adminEmail = "admin@administrativo.com";

        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador Master")
                    .email(adminEmail)
                    .senha(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            usuarioRepository.save(admin);
        } else {
            log.info("Usuário admin master já existe no sistema.");
        }
    }

    private void criarTelasPermissao() {
        // Descobre automaticamente as telas através dos controllers
        List<TelaPermissao> telas = telaPermissaoDiscoveryService.descobrirTelas();

        int telasCriadas = 0;
        int telasAtualizadas = 0;

        for (TelaPermissao tela : telas) {
            if (tela.getId() == null) {
                log.warn("Tela sem ID ignorada: {}", tela.getNome());
                continue;
            }

            if (!telaPermissaoRepository.existsById(tela.getId())) {
                telaPermissaoRepository.save(tela);
                telasCriadas++;
                log.debug("Tela criada: {} - {} ({})", tela.getId(), tela.getNome(), tela.getRota());
            } else {
                // Atualiza telas existentes caso tenham mudado (rota, nome, descrição)
                TelaPermissao telaExistente = telaPermissaoRepository.findById(tela.getId()).orElse(null);
                if (telaExistente != null) {
                    boolean precisaAtualizar = false;

                    if (!tela.getNome().equals(telaExistente.getNome())) {
                        telaExistente.setNome(tela.getNome());
                        precisaAtualizar = true;
                    }
                    if (!tela.getRota().equals(telaExistente.getRota())) {
                        telaExistente.setRota(tela.getRota());
                        precisaAtualizar = true;
                    }
                    if (tela.getDescricao() != null && !tela.getDescricao().equals(telaExistente.getDescricao())) {
                        telaExistente.setDescricao(tela.getDescricao());
                        precisaAtualizar = true;
                    }

                    if (precisaAtualizar) {
                        telaPermissaoRepository.save(telaExistente);
                        telasAtualizadas++;
                        log.debug("Tela atualizada: {} - {}", tela.getId(), tela.getNome());
                    }
                }
            }
        }

        if (telasCriadas > 0 || telasAtualizadas > 0) {
            log.info("{} telas de permissão criadas e {} atualizadas com sucesso!", telasCriadas, telasAtualizadas);
        } else {
            log.info("Todas as telas de permissão já estão atualizadas no sistema.");
        }
    }
}
