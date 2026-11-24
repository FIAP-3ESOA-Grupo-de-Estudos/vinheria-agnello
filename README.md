# vinheria-agnelo

# CI/CD — Pipeline Jenkins (Vinheria Agnello)

Este guia descreve como provisionar o servidor **Jenkins** via Docker Compose e executar a esteira de build e deploy automatizado da aplicação Java Web e dos microsserviços da **Vinheria Agnello**.

## Arquitetura da Esteira

O ambiente de CI/CD é isolado da infraestrutura da aplicação por meio de dois arquivos Compose distintos:
- **`infra/docker-compose.jenkins.yml`**: Sobe exclusivamente o container do Jenkins (`agnello-jenkins`), já configurado com Maven, Docker CLI, Compose v2, Buildx e JCasC.
- **`infra/docker-compose.yml`**: Gerenciado automaticamente pelo próprio pipeline durante o estágio de deploy para subir `dnsmasq`, `catalog`, `order` e `probe`.

---

## Pré-requisitos

- **Docker Desktop** (com suporte a WSL2 / Linux containers em execução).
- Diretório local de destino para publicação de artefatos mapeado no Compose (padrão no Windows: `C:\deploy-vinheria`).

---

## 1. Subindo o Servidor Jenkins

Na raiz do repositório (`vinheria-agnello`), execute:

```powershell
docker compose -f infra/docker-compose.jenkins.yml up -d --build

```

> **Nota:** Na primeira execução, o Docker fará o build da imagem `agnello-jenkins` instalando o Maven, os binários do Docker e os plugins do Jenkins automaticamente (sem necessidade de configuração manual via assistente web).

---

## 2. Acessando o Painel e Executando o Pipeline

1. Abra o navegador e acesse: **`http://127.0.0.1:8085`**
2. Faça login com as credenciais padrão definidas no `infra/jenkins/casc.yaml`:
* **Usuário:** `admin`
* **Senha:** `admin`


3. Na tela inicial (*Dashboard*), clique no job **`vinheria-agnello`** (criado automaticamente).
4. No menu lateral esquerdo, clique em **Build Now**.
5. Acompanhe o progresso visual pelos 5 estágios no **Stage View** ou clique no número da execução (ex.: `#1`) > **Console Output** para ver os logs detalhados.

---

## 3. Estágios do Pipeline (`Jenkinsfile`)

| Estágio | Descrição |
| --- | --- |
| **1. Preparar Workspace** | Copia os arquivos do repositório montado (`/var/workspace/vinheria-agnello`) para o workspace ativo do job. |
| **2. Build Maven** | Executa `mvn clean package -DskipTests` para compilar o código-fonte e empacotar o `.war`. |
| **3. Publicar Artefatos** | Transfere o `.war` (renomeado para `ROOT.war`), `.jar`, `infra/` e `services/` para `/var/www/vinheria-agnello` (`C:\deploy-vinheria`) e registra o timestamp em `deploy_info.txt`. |
| **4. Deploy App (Tomcat)** | Recria o container `vinheria-web` (`tomcat:9.0-jdk17`) montando a pasta de deploy e publicando a aplicação web. |
| **5. Deploy Infra (Docker Compose)** | Derruba instâncias antigas e sobe a stack de microsserviços (`dnsmasq`, `catalog`, `order` e `probe`) a partir da pasta publicada. |

---

## 4. Validando os Serviços Publicados

Após o pipeline finalizar com **`Finished: SUCCESS`**, valide os endpoints no navegador ou via `curl`:

* **Aplicação Web (Tomcat / JSP):** `http://127.0.0.1:8086/`
* **Catalog Service (Healthcheck):** `http://127.0.0.1:8081/health` (esperado: `UP`)
* **Order Service (Healthcheck):** `http://127.0.0.1:8082/health` (esperado: `UP`)

---

## 5. Encerrando o Ambiente

Para parar o servidor Jenkins sem remover o histórico de builds:

```powershell
docker compose -f infra/docker-compose.jenkins.yml down

```

Para parar também os containers publicados pelo pipeline:

```powershell
docker rm -f vinheria-web
docker compose -f C:\deploy-vinheria\infra\docker-compose.yml down

```

## IoT

A solução IoT para monitorar as condições de armazenamento dos vinhos está organizada em duas etapas:

- Programa do ESP32, sensores e comunicação MQTT em [`iot/arduino/agnello_sensors`](iot/arduino/agnello_sensors).
- Fluxo de monitoramento e dashboard no Node-RED em [`iot/node-red`](iot/node-red).
