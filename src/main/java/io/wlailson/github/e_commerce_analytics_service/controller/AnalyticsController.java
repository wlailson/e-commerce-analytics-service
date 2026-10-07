package io.wlailson.github.e_commerce_analytics_service.controller;

import io.wlailson.github.e_commerce_analytics_service.dto.DashboardAnalyticsDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.SaleAnalyticsResponseDTO;
import io.wlailson.github.e_commerce_analytics_service.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Consultas de vendas e indicadores consolidados.")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/sales")
    @Operation(
            summary = "Lista as vendas",
            description = "Retorna as vendas registradas em páginas. `page` começa em zero, `size` define o "
                    + "tamanho da página e `sort` aceita o campo e a direção da ordenação. Valores inválidos "
                    + "de paginação usam os valores padrão do Spring Data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de vendas retornada."),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido."),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido."),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro inesperado ao consultar as vendas.",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    public ResponseEntity<Page<SaleAnalyticsResponseDTO>> getSales(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(service.getSales(pageable));
    }

    @GetMapping("/dashboard")
    @Operation(
            summary = "Consulta o dashboard",
            description = "Retorna totais de vendas, receita, ticket médio e a distribuição das vendas por status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicadores do dashboard retornados."),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido."),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido."),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro inesperado ao calcular os indicadores.",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    public ResponseEntity<DashboardAnalyticsDTO> dashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }
}
