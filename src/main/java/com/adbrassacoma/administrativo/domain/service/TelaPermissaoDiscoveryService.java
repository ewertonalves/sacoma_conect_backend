package com.adbrassacoma.administrativo.domain.service;

import com.adbrassacoma.administrativo.domain.model.TelaPermissao;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Serviço responsável por descobrir automaticamente as telas de permissão
 * através da análise dos controllers usando Reflection.
 */
@Service
@Slf4j
public class TelaPermissaoDiscoveryService {

    private static final Pattern PATH_ID_PATTERN = Pattern.compile("^/\\{id\\}$|^/\\{.*id.*\\}$");
    private static final Pattern PATH_ID_EDITAR_PATTERN = Pattern.compile("^/\\{id\\}/editar$|^/\\{.*id.*\\}/editar$");
    private static final Set<String> ENDPOINTS_IGNORADOS = Set.of("/api/auth", "/api/permissoes", "/api/cep");

    private final ApplicationContext applicationContext;

    public TelaPermissaoDiscoveryService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<TelaPermissao> descobrirTelas() {
        log.info("Iniciando descoberta automática de telas de permissão...");

        Map<String, TelaPermissao> telasMap = new LinkedHashMap<>();
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);

        for (Object controller : controllers.values()) {
            Class<?> controllerClass = controller.getClass();

            if (controllerClass.getName().contains("$Proxy")) {
                controllerClass = controllerClass.getSuperclass();
            }

            RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
            if (classMapping == null) {
                continue;
            }

            String basePath = extractPath(classMapping.value(), classMapping.path());
            if (basePath == null || basePath.isEmpty()) {
                continue;
            }

            String frontendBasePath = basePath.replaceFirst("^/api", "");
            String recurso = extractRecurso(basePath);

            Method[] methods = controllerClass.getDeclaredMethods();
            for (Method method : methods) {
                TelaPermissao tela = processarMetodo(method, recurso, frontendBasePath, basePath);
                if (tela != null) {
                    telasMap.putIfAbsent(tela.getId(), tela);
                }
            }
        }

        adicionarTelasEspeciais(telasMap);

        List<TelaPermissao> telas = new ArrayList<>(telasMap.values());
        log.info("Descoberta concluída: {} telas encontradas", telas.size());

        return telas;
    }

    private TelaPermissao processarMetodo(Method method, String recurso, String frontendBasePath, String apiBasePath) {
        if (!temMapeamentoHttp(method) || deveIgnorarEndpoint(apiBasePath)) {
            return null;
        }

        String httpMethod = obterMetodoHttp(method);
        if (httpMethod == null)
            return null;

        String methodPath = getMethodPath(method);

        String telaId = gerarTelaId(recurso, httpMethod, methodPath);

        if (telaId == null)
            return null;

        String rota = gerarRotaFrontend(frontendBasePath, methodPath, httpMethod);
        String nome = obterNomeTela(method, recurso, httpMethod, methodPath);
        String descricao = obterDescricaoTela(method, nome);

        return TelaPermissao.builder()
                .id(telaId)
                .nome(nome)
                .rota(rota)
                .descricao(descricao)
                .build();
    }

    private String getMethodPath(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping annotation = method.getAnnotation(GetMapping.class);
            return extractPath(annotation.value(), annotation.path());
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping annotation = method.getAnnotation(PostMapping.class);
            return extractPath(annotation.value(), annotation.path());
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping annotation = method.getAnnotation(PutMapping.class);
            return extractPath(annotation.value(), annotation.path());
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
            return extractPath(annotation.value(), annotation.path());
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            return extractPath(annotation.value(), annotation.path());
        }
        return "";
    }

    private String gerarTelaId(String recurso, String httpMethod, String methodPath) {
        if ("GET".equals(httpMethod) && (methodPath == null || methodPath.isEmpty() || "/".equals(methodPath))) {
            return recurso;
        }

        if ("POST".equals(httpMethod) && (methodPath == null || methodPath.isEmpty() || "/".equals(methodPath))) {
            return recurso + "-novo";
        }

        if ("GET".equals(httpMethod) && methodPath != null && PATH_ID_PATTERN.matcher(methodPath).matches()) {
            return recurso + "-detalhes";
        }

        if ("PUT".equals(httpMethod) && methodPath != null && PATH_ID_PATTERN.matcher(methodPath).matches()) {
            return recurso + "-editar";
        }

        if ("DELETE".equals(httpMethod) && methodPath != null && PATH_ID_PATTERN.matcher(methodPath).matches()) {
            return null;
        }

        if (methodPath != null) {
            if ("GET".equals(httpMethod) && "/novo".equals(methodPath)) {
                return recurso + "-novo";
            }

            if ("GET".equals(httpMethod) && PATH_ID_EDITAR_PATTERN.matcher(methodPath).matches()) {
                return recurso + "-editar";
            }
        }

        return null;
    }

    private String gerarRotaFrontend(String basePath, String methodPath, String httpMethod) {
        if (methodPath == null || methodPath.isEmpty() || methodPath.equals("/")) {
            return basePath;
        }

        if (PATH_ID_PATTERN.matcher(methodPath).matches()) {
            if ("GET".equals(httpMethod)) {
                return basePath + "/:id";
            }
            if ("PUT".equals(httpMethod)) {
                return basePath + "/:id/editar";
            }
        }

        String rota = basePath + methodPath;
        rota = rota.replaceAll("\\{id\\}", ":id");
        rota = rota.replaceAll("\\{[^}]*id[^}]*\\}", ":id");
        rota = rota.replaceFirst("^/api", "");
        return rota;
    }

    private String extractRecurso(String basePath) {
        String path = basePath.replaceFirst("^/api", "");
        path = path.replaceFirst("^/", "");
        path = path.replaceFirst("/$", "");
        return path;
    }

    private String extractPath(String[] value, String[] path) {
        if (value != null && value.length > 0 && !value[0].isEmpty()) {
            return value[0];
        }
        if (path != null && path.length > 0 && !path[0].isEmpty()) {
            return path[0];
        }
        return "";
    }

    private boolean temMapeamentoHttp(Method method) {
        return Stream
                .of(GetMapping.class, PostMapping.class, PutMapping.class, DeleteMapping.class, RequestMapping.class)
                .anyMatch(method::isAnnotationPresent);
    }

    private String obterMetodoHttp(Method method) {
        Map<Class<? extends Annotation>, String> httpMethods = Map.of(
                GetMapping.class, "GET",
                PostMapping.class, "POST",
                PutMapping.class, "PUT",
                DeleteMapping.class, "DELETE");
        for (var entry : httpMethods.entrySet()) {
            if (method.isAnnotationPresent(entry.getKey())) {
                return entry.getValue();
            }
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            RequestMethod[] methods = mapping.method();
            if (methods.length > 0) {
                return methods[0].name();
            }
        }
        return null;
    }

    private boolean deveIgnorarEndpoint(String basePath) {
        return ENDPOINTS_IGNORADOS.stream().anyMatch(basePath::startsWith);
    }

    private String obterNomeTela(Method method, String recurso, String httpMethod, String methodPath) {
        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null && operation.summary() != null && !operation.summary().isEmpty()) {
            return operation.summary();
        }

        String recursoFormatado = formatarRecurso(recurso);

        List<Map.Entry<BiPredicate<String, String>, Function<String, String>>> regras = List.of(
                new AbstractMap.SimpleEntry<>(
                        (m, p) -> "GET".equals(m) && (p == null || p.isEmpty() || "/".equals(p)),
                        r -> recursoFormatado),
                new AbstractMap.SimpleEntry<>(
                        (m, p) -> "POST".equals(m) || ("GET".equals(m) && p != null && "/novo".equals(p)),
                        r -> "Cadastrar " + recursoFormatado),
                new AbstractMap.SimpleEntry<>(
                        (m, p) -> "PUT".equals(m) || ("GET".equals(m) && p != null && p.matches(".*editar.*")),
                        r -> "Editar " + recursoFormatado),
                new AbstractMap.SimpleEntry<>(
                        (m, p) -> "GET".equals(m) && p != null && p.matches(".*\\{id\\}.*"),
                        r -> "Detalhes do " + recursoFormatado));
        return regras.stream()
                .filter(entry -> entry.getKey().test(httpMethod, methodPath))
                .map(entry -> entry.getValue().apply(recurso))
                .findFirst()
                .orElse(recursoFormatado);
    }

    private String obterDescricaoTela(Method method, String nome) {
        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null && operation.description() != null && !operation.description().isEmpty()) {
            return operation.description();
        }
        return nome;
    }

    private String formatarRecurso(String recurso) {
        if (recurso == null || recurso.isEmpty()) {
            return "";
        }
        String formatado = recurso.replace("-", " ");
        String[] palavras = formatado.split(" ");
        StringBuilder resultado = new StringBuilder();
        Arrays.stream(palavras)
                .filter(palavra -> !palavra.isEmpty())
                .forEach(palavra -> resultado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1))
                        .append(" "));
        return resultado.toString().trim();
    }

    private void adicionarTelasEspeciais(Map<String, TelaPermissao> telasMap) {
        telasMap.putIfAbsent("dashboard", TelaPermissao.builder()
                .id("dashboard")
                .nome("Dashboard")
                .rota("/dashboard")
                .descricao("Página inicial do sistema")
                .build());
    }
}
