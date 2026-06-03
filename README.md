# 💰 Projeto Financeiro — Microserviços

Plataforma de controle financeiro baseada em microsserviços e arquitetura orientada a eventos. Os pagamentos são processados e consolidados mensalmente por um serviço de extrato, que publica eventos consumidos por um serviço de IA responsável por gerar recomendações financeiras personalizadas, projeções de economia e insights sobre os hábitos de consumo do usuário.
---

## 🏗️ Arquitetura

<img width="1145" height="628" alt="image" src="https://github.com/user-attachments/assets/4d0dba59-16a7-4a8b-9a33-36567a3423d0" />


---

## 📦 Serviços

| Serviço            | Porta | Banco        | Responsabilidade                          |
|--------------------|-------|--------------|-------------------------------------------|
| PagamentoService   | 8081  | pagamentodb  | Registrar pagamentos e publicar eventos   |
| ExtratoService     | 8082  | extratodb    | Consolidar gastos mensais do usuário      |
| LLMService         | 8083  | llmdb        | Gerar insights financeiros com Claude     |

---

## 🗂️ Estrutura de cada serviço

```
{Servico}/
├── .gitattributes
├── .gitignore
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/main/java/com/financeiro/{servico}/
    ├── config/        ← Kafka (Producer/Consumer) e ClaudeConfig
    ├── controller/    ← Endpoints REST
    ├── dto/           ← Objetos de entrada/saída e eventos Kafka
    ├── model/         ← Entidades JPA
    ├── repository/    ← Interfaces Spring Data JPA
    └── service/       ← Regras de negócio e listeners Kafka
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

| Método | Rota          | Descrição              |
|--------|---------------|------------------------|
| POST   | /pagamentos   | Registrar um pagamento |

**Body:**
```json
{
  "usuarioId": 10,
  "valor": 150.00,
  "descricao": "Mercado"
}
```

---

### ExtratoService — `localhost:8082`

| Método | Rota                              | Descrição                   |
|--------|-----------------------------------|-----------------------------|
| GET    | /extrato/{usuarioId}/{mes}/{ano}  | Buscar resumo mensal         |

---

### LLMService — `localhost:8083`

| Método | Rota                  | Descrição                          |
|--------|-----------------------|------------------------------------|
| GET    | /insights/{usuarioId} | Listar insights gerados para o usuário |

---

## 🚀 Como rodar

### Pré-requisitos
- Docker Desktop instalado
- Java 17+
- Sua chave da API Claude (Anthropic)

---

### 1. Configurar a chave do Claude

Crie um arquivo `.env` na raiz do projeto (mesma pasta do `docker-compose.yml`):

```env
CLAUDE_API_KEY=sua_chave_aqui
```

> Nunca suba o `.env` para o Git. Ele já está no `.gitignore`.

---

### 2. Subir tudo com Docker

```cmd
docker-compose up --build
```

Para rodar em background:

```cmd
docker-compose up --build -d
```

---

### 3. Verificar se os serviços subiram

```cmd
docker-compose ps
```

---

### 4. Parar tudo

```cmd
docker-compose down
```

Para remover também os volumes (banco de dados):

```cmd
docker-compose down -v
```

---

## 🧪 Testando o fluxo completo

### Passo 1 — Registrar um pagamento

```cmd
curl -X POST http://localhost:8081/pagamentos ^
  -H "Content-Type: application/json" ^
  -d "{\"usuarioId\": 10, \"valor\": 200.00, \"descricao\": \"Supermercado\"}"
```

### Passo 2 — Verificar o extrato atualizado

```cmd
curl http://localhost:8082/extrato/10/6/2026
```

### Passo 3 — Verificar o insight gerado pelo Claude

```cmd
curl http://localhost:8083/insights/10
```

---

## ⚙️ Variáveis de ambiente

| Variável                    | Descrição                        | Padrão                |
|-----------------------------|----------------------------------|-----------------------|
| `SPRING_DATASOURCE_URL`     | URL do PostgreSQL                | configurado no compose|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Endereço do Kafka           | `kafka:9092`          |
| `CLAUDE_API_KEY`            | Chave da API Anthropic           | **obrigatório**       |
| `CLAUDE_MODEL`              | Modelo Claude utilizado          | `claude-sonnet-4-20250514` |

---

## 🔍 Observabilidade

Todos os serviços têm logs estruturados via **SLF4J/Logback**. Futuramente integrar com **Datadog** para:
- Logs centralizados
- Métricas de latência Kafka
- Traces distribuídos entre os serviços

---

## ☁️ Deploy AWS 

| Recurso | Uso |
|---------|-----|
| **ECS** | Rodar os containers dos 3 serviços |
| **ECR** | Armazenar as imagens Docker |


---

## 📁 Arquivos na raiz

| Arquivo              | Descrição                                  |
|----------------------|--------------------------------------------|
| `docker-compose.yml` | Sobe todos os serviços + Kafka + Postgres  |
| `init-db.sh`         | Cria os 3 bancos no PostgreSQL             |
| `.env`               | Suas variáveis secretas (não subir no Git) |
| `README.md`          | Este arquivo                               |
