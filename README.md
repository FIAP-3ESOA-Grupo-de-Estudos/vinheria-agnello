# vinheria-agnelo

## Instruções

- Para utilizar o `DatabaseConfig`:

```java
import java.sql.Connection;
import br.com.vinheriaagnello.config.DatabaseConfig;

Connection conn = DatabaseConfig.getDataSource().getConnection();
```

## Infraestrutura e microsserviços

O Catalog Service e o Order Service, com DNS e DHCP num ambiente Docker, estão em [`infra/README.md`](infra/README.md) (item 1).

## CI/CD: pipeline Jenkins (item 2)

O Jenkins roda num contêiner, já configurado por código (JCasC), e tem um job, `vinheria-agnello`, que lê o [`Jenkinsfile`](Jenkinsfile) do repositório.

### Arquitetura da esteira

- [`infra/docker-compose.jenkins.yml`](infra/docker-compose.jenkins.yml) sobe só o Jenkins (`agnello-jenkins`): Maven, Docker CLI com Compose v2 e Buildx, e os plugins instalados na imagem ([`infra/jenkins/Dockerfile`](infra/jenkins/Dockerfile)).
- [`infra/docker-compose.yml`](infra/docker-compose.yml) é a pilha da aplicação. O próprio pipeline a sobe no estágio 5.
- **O código vem do Git.** O repositório é montado no Jenkins só para leitura, e o job o clona (`file:///var/workspace/vinheria-agnello`). O Jenkins compila **o que está commitado** na branch, nunca arquivos não commitados. Para publicar o que está no GitHub, faça `git pull` antes. O repositório no GitHub é privado; clonar do clone local evita guardar uma credencial do GitHub no Jenkins.
- **Deploy automático.** O job consulta o repositório a cada 2 minutos (`pollSCM`): um commit novo na branch dispara build e deploy.
- **Pasta de publicação.** O volume `vinheria-deploy` (`/var/www/vinheria-agnello` no Jenkins) recebe `infra/`, `services/` e `deploy_info.txt`. O volume `vinheria-webapps` recebe o `ROOT.war` e é o `webapps` do Tomcat, como a pasta de publicação de um IIS.

### Pré-requisitos

Docker Desktop (ou Docker Engine) com Compose v2. Nenhum caminho fixo do Windows: tudo fica em volumes do Docker.

### 1. Subir o Jenkins

Na raiz do repositório, defina a senha do usuário `admin` (ela não fica no repositório) e suba o contêiner:

```powershell
$env:JENKINS_ADMIN_PASSWORD = "escolha-uma-senha"
docker compose -f infra/docker-compose.jenkins.yml up -d --build
```

No bash: `JENKINS_ADMIN_PASSWORD=escolha-uma-senha docker compose -f infra/docker-compose.jenkins.yml up -d --build`.

O job compila a branch `main`. Para outra branch, defina também `PIPELINE_BRANCH` (por exemplo `PIPELINE_BRANCH=minha-branch`).

### 2. Rodar o pipeline

1. Abra **http://127.0.0.1:8085** e entre com `admin` e a senha que você definiu.
2. Abra o job **`vinheria-agnello`** e clique em **Build Now** (ou faça um commit e espere até 2 minutos).
3. Acompanhe os estágios no **Stage View** ou em **Console Output**.

### 3. Estágios do `Jenkinsfile`

| Estágio | O que faz |
| --- | --- |
| Checkout SCM | Clona a branch do repositório Git (feito pelo Jenkins antes do primeiro estágio). |
| 1. Build | `echo "Deploy da Vinheria"` e `mvn clean package`: empacota a aplicação web (`.war`). |
| 2. Testes dos microsserviços | `mvn test` no Catalog Service e no Order Service. Um teste que falha para o pipeline antes do deploy. |
| 3. Publicar artefatos | Copia o `.war` como `ROOT.war` para o `webapps` do Tomcat, e `infra/` e `services/` para a pasta de publicação, com o commit em `deploy_info.txt`. |
| 4. Deploy da aplicação web (Tomcat) | Recria o contêiner `vinheria-web` (`tomcat:9.0-jdk17`) com o volume `vinheria-webapps`, em `127.0.0.1:8086`. |
| 5. Deploy dos microsserviços | `docker compose up -d --build` a partir da pasta publicada: reconstrói só o que mudou. |
| 6. Verificar o ambiente | Roda o [`infra/verify.sh`](infra/verify.sh) (DNS, DHCP e os microsserviços) e espera o Tomcat responder. |

### 4. Validar

Depois de um `Finished: SUCCESS`:

- Aplicação web (Tomcat): `http://127.0.0.1:8086/`
- Catalog Service e Order Service: veja [`infra/README.md`](infra/README.md); o estágio 6 já roda o `verify.sh`.

### 5. Encerrar

```powershell
docker compose -f infra/docker-compose.jenkins.yml down       # para o Jenkins e mantém o histórico de builds
docker rm -f vinheria-web                                      # para a aplicação web publicada
docker compose -f infra/docker-compose.yml down                # para os microsserviços publicados
```

`down -v` no primeiro comando apaga também o histórico de builds e o cache do Maven.

### Segurança do Jenkins

O contêiner roda como `root` e monta o `docker.sock` do host para poder subir contêineres. Na prática, quem controla esse Jenkins controla o Docker do host. Por isso a porta fica só no loopback (`127.0.0.1:8085`), a senha vem de variável de ambiente e o login é obrigatório. Num servidor de verdade, os builds rodariam em agentes separados, sem o socket do host.

## IoT

A solução IoT para monitorar as condições de armazenamento dos vinhos está organizada em duas etapas:

- Programa do ESP32, sensores e comunicação MQTT em [`iot/arduino/agnello_sensors`](iot/arduino/agnello_sensors).
- Fluxo de monitoramento e dashboard no Node-RED em [`iot/node-red`](iot/node-red).
