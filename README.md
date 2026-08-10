### Spring Boot Kafka — Boleto

Projeto com duas aplicações Spring Boot que se comunicam por Apache Kafka, utilizando Avro e Confluent Schema Registry.

![Arquitetura](images/arquitetura.gif)

### Aplicações

* **api-boleto** — emite e gerencia boletos.
* **validador-boleto** — valida boletos e confirma pagamentos.

### Tecnologias

Java 21 · Spring Boot 4 · Kafka · Avro · Schema Registry · JPA · H2 · Docker

## Como funciona

```text
api-boleto
    │
    │ boleto-emitido
    ▼
Kafka
    │
    ▼
validador-boleto
    │
    │ boleto-pago
    ▼
Kafka
    │
    ▼
api-boleto
```

Ao emitir um boleto, a `api-boleto` salva os dados no banco e publica um evento.

O `validador-boleto` recebe o evento, valida o boleto e permite confirmar o pagamento.

A confirmação é publicada no Kafka e posteriormente consumida pela `api-boleto`, que atualiza o boleto.

## Executando

### 1. Inicie o Kafka

Na raiz do projeto:

```bash
docker compose -f compose.yml up -d
```

Serviços:

```text
Kafka             localhost:9092
Schema Registry   localhost:8085
Kafka UI          localhost:8090
```

### 2. Inicie o validador

```bash
cd validador-boleto
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Porta: `8383`

### 3. Inicie a API

```bash
cd api-boleto
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Porta: `8282`

## Tópicos

| Tópico            | Função                 |
| ----------------- | ---------------------- |
| `boleto-emitido`  | Boleto criado          |
| `boleto-validado` | Resultado da validação |
| `boleto-pago`     | Pagamento confirmado   |

## Principais endpoints

### api-boleto — `8282`

```text
POST   /api/v1/boletos
GET    /api/v1/boletos
GET    /api/v1/boletos/{id}
PUT    /api/v1/boletos/{id}
PATCH  /api/v1/boletos/{id}/cancelar
```

Swagger:

`http://localhost:8282/swagger-ui/index.html`

### validador-boleto — `8383`

```text
POST /pagamentos/confirmar
```

Swagger:

`http://localhost:8383/swagger-ui/index.html`

## Configuração

Por padrão:

```text
Kafka:             localhost:9092
Schema Registry:   http://localhost:8085
```

As duas aplicações utilizam H2 em memória para persistência local.

Os schemas Avro estão em:

```text
src/main/avro/boletoevento.avsc
```


