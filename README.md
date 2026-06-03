#  Plataforma de Controle Financeiro com IA

Sistema distribuído que registra pagamentos, consolida gastos mensais e utiliza Claude (Anthropic) para gerar recomendações financeiras personalizadas via arquitetura orientada a eventos.

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=flat&logo=spring&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=flat&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Claude](https://img.shields.io/badge/Claude_API-D4868C?style=flat&logo=anthropic&logoColor=white)

---

## 📌 Sobre o Projeto

A maioria das pessoas não sabe quanto gasta por mês até olhar o extrato do cartão no fim do mês. Este sistema resolve isso em tempo real: cada pagamento registrado dispara automaticamente uma cadeia de eventos que consolida o gasto mensal e aciona uma IA para analisar a saúde financeira do usuário e sugerir ações concretas.

O projeto foi desenvolvido com foco em:

- Arquitetura orientada a eventos com serviços desacoplados
- Comunicação exclusivamente assíncrona via Kafka (sem REST entre serviços)
- IA generativa aplicada ao domínio financeiro com Claude
- Isolamento de domínios — cada serviço tem seu próprio banco de dados
- Observabilidade com logs estruturados e integração planejada com Datadog

---

## 🏗️ Arquitetura

<img width="1145" height="628" alt="image" src="https://github.com/user-attachments/assets/4d0dba59-16a7-4a8b-9a33-36567a3423d0" />

Fluxo principal:

```
POST /pagamentos → PagamentoService → [pagamento_criado] → ExtratoService → [extrato_atualizado] → LLMService → Claude API
```

---

## 📦 Serviços

| Serviço | Porta | Banco | Responsabilidade | Publica | Consome |
|---|---|---|---|---|---|
| PagamentoService | 8081 | pagamentodb | Recebe e persiste pagamentos | `pagamento_criado` | — |
| ExtratoService | 8082 | extratodb | Acumula gasto mensal por usuário | `extrato_atualizado` | `pagamento_criado` |
| LLMService | 8083 | llmdb | Gera e persiste insights via Claude | — | `extrato_atualizado` |

---

## 🗂️ Estrutura de cada serviço

```
{Servico}/
├── .gitattributes
├── .gitignore
├── Dockerfile
├── mvnw  /  mvnw.cmd
├── pom.xml
└── src/main/java/com/financeiro/{servico}/
    ├── config/        ← KafkaProducer/Consumer + ClaudeConfig
    ├── controller/    ← Endpoints REST
    ├── dto/           ← Eventos Kafka e objetos de entrada/saída
    ├── model/         ← Entidades JPA
    ├── repository/    ← Interfaces Spring Data JPA
    └── service/       ← Regras de negócio e @KafkaListener
```

---

## 📨 Fluxo de eventos Kafka

### 1. `pagamento_criado`

Publicado por: **PagamentoService**
Consumido por: **ExtratoService**

```json
{
  "pagamentoId": 1,
  "usuarioId": 10,
  "valor": 150.00
}
```

### 2. `extrato_atualizado`

Publicado por: **ExtratoService**
Consumido por: **LLMService**

```json
{
  "extratoId": 20,
  "usuarioId": 10,
  "salario": 5000.00,
  "totalGastoMes": 1350.00,
  "mes": 6,
  "ano": 2026
}
```

---

## 🔌 Endpoints REST

### PagamentoService — `localhost:8081`

| Método | Rota | Descrição |
|---|---|---|
| POST | `/pagamentos` | Registrar um pagamento |

**Body:**
```json
{
  "usuarioId": 10,
  "valor": 200.00,
  "descricao": "Supermercado"
}
```

### ExtratoService — `localhost:8082`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/extrato/{usuarioId}/{mes}/{ano}` | Buscar resumo mensal |

### LLMService — `localhost:8083`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/insights/{usuarioId}` | Listar insights gerados para o usuário |

---

## 🧠 Decisões Técnicas

### Por que Kafka e não REST entre serviços?

REST síncrono cria acoplamento temporal: se o ExtratoService estiver fora do ar no momento do pagamento, o dado se perde. Com Kafka, o evento fica retido no tópico e o consumidor processa quando voltar. Além disso, um terceiro serviço pode ser adicionado ao fluxo sem alterar nenhum serviço existente — basta assinar o tópico.

### Por que um banco por serviço e não um banco central?

Banco compartilhado gera acoplamento de schema: qualquer migração de tabela impacta todos os serviços. Com bancos separados, cada serviço evolui seu modelo de dados de forma independente. O custo é a consistência eventual — aceito neste domínio.

### Por que Claude e não um modelo open-source local?

O objetivo é gerar texto financeiro em português com qualidade e nuance suficientes para ser útil ao usuário. Modelos locais menores apresentam saídas genéricas neste domínio. O Claude via API elimina infra de GPU e entrega qualidade superior para linguagem natural financeira.

### Por que PostgreSQL e não MySQL?

PostgreSQL tem suporte nativo ao tipo `NUMERIC` com precisão arbitrária — essencial para valores monetários sem risco de arredondamento de ponto flutuante. Também tem melhor suporte a JSON nativo, útil para armazenar o payload de insights sem schema rígido.

### Por que Spring Boot e não Quarkus/Micronaut?

O ecossistema Spring (Spring Kafka, Spring Data JPA) tem a integração mais madura e documentada para o stack Kafka + JPA. A curva de adoção é menor e o suporte da comunidade é maior — pragmatismo sobre purismo de performance de startup.

### Por que foco em resumo mensal e não por categoria?

Categorização automática de gastos requer NLP ou tabelas de mapeamento extensas com alta taxa de erro. O resumo mensal resolve o problema principal (quanto gastei este mês?) sem depender de classificação correta de cada transação. É simples, confiável e suficiente para o modelo de IA gerar insights úteis.

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia | Motivo |
|---|---|---|
| Linguagem | Java 17 | LTS, suporte nativo no ecossistema Spring |
| Framework | Spring Boot 3.2 | Integração nativa com Kafka e JPA |
| Mensageria | Apache Kafka | Durabilidade de eventos, desacoplamento, replay |
| Banco de dados | PostgreSQL 15 | Precisão monetária com NUMERIC, robustez transacional |
| IA / LLM | Claude (Anthropic) | Qualidade de linguagem natural em português para domínio financeiro |
| Containerização | Docker + Compose | Ambiente reproduzível com um único comando |
| Observabilidade | SLF4J + Datadog | Logs estruturados locais; Datadog para traces em produção |

---

## 🚀 Como Executar

### Pré-requisitos

- Docker Desktop instalado
- Java 17+
- Chave de API do Claude (Anthropic)

### 1. Configurar a chave do Claude

Crie um arquivo `.env` na raiz do projeto:

```env
CLAUDE_API_KEY=sua_chave_aqui
```

> ⚠️ Nunca suba o `.env` para o Git. Ele já está no `.gitignore`.

### 2. Subir todos os serviços

```bash
docker-compose up --build
```

Para rodar em background:

```bash
docker-compose up --build -d
```

### 3. Verificar se os serviços subiram

```bash
docker-compose ps
```

### 4. Parar tudo

```bash
docker-compose down        # para os containers
docker-compose down -v     # para + apaga os volumes (banco)
```

---


## ⚙️ Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL do PostgreSQL | configurado no compose |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Endereço do broker Kafka | `kafka:9092` |
| `CLAUDE_API_KEY` | Chave da API Anthropic | **obrigatório** |
| `CLAUDE_MODEL` | Modelo Claude utilizado | `claude-sonnet-4-20250514` |

---


## ☁️ Deploy AWS (Planejado)

| Recurso | Uso |
|---|---|
| **ECS** | Orquestração dos containers dos 3 serviços |
| **ECR** | Registro das imagens Docker |


---

## 👨‍💻 Autor

**Herik Kou Homma Kato**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/herik-kato-dev/)
