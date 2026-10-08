# E-commerce Analytics Service

Serviço de leitura e agregação de dados de vendas. Consome eventos de pedidos e pagamentos via Kafka, persiste os dados analíticos em MongoDB e disponibiliza consultas de vendas e indicadores para dashboards.

## Tecnologias

- Java 25, Maven e Spring Boot 4.1.1.
- Spring MVC, Spring Security e OAuth2 Resource Server para validação JWT.
- Spring Data MongoDB.
- Spring for Apache Kafka para consumo de eventos.
- Spring Boot Actuator e Micrometer Prometheus Registry.
- springdoc-openapi / Swagger UI.
- Testes com JUnit Jupiter, Mockito e Testcontainers para MongoDB e Kafka.

## Executar localmente

Pré-requisitos: JDK 25, MongoDB e Kafka acessíveis. O perfil de desenvolvimento é ativado por padrão; ajuste a URI do MongoDB em `src/main/resources/application-dev.yaml` conforme o ambiente.

```bash
./mvnw spring-boot:run
```

Variáveis de configuração principais:

| Variável | Uso |
|---|---|
| `SERVER_PORT` | Porta HTTP (padrão `8080`). |
| `SPRING_MONGODB_URI` | URI do MongoDB, se fornecida pelo ambiente. |
| `KAFKA_BOOTSTRAP_SERVERS` | Endereço do cluster Kafka (padrão local `localhost:9092`). |
| `KAFKA_CONSUMER_GROUP_ID` | Grupo consumidor (padrão `analytics-group`). |
| `JWT_PUBLIC_KEY` | Chave pública para validação dos tokens. |

O serviço escuta por padrão os tópicos `order-created-event`, `order-updated-event` e `payment-created-event`. Use chaves próprias fora do ambiente local de desenvolvimento.

## Testes

```bash
./mvnw test
```

Docker deve estar disponível para os testes que inicializam MongoDB ou Kafka por Testcontainers.

## API e observabilidade

Os endpoints de analytics estão sob `/analytics`, incluindo consultas de vendas e dashboard. A documentação HTTP está disponível localmente em:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Métricas Prometheus: `http://localhost:8080/actuator/prometheus`
- Saúde: `http://localhost:8080/actuator/health`

## Projeto

Veja a arquitetura e os demais serviços no [README central do BFF](https://github.com/wlailson/e-commerce-BFF-service#readme).
