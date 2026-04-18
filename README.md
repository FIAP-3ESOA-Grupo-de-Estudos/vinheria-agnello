# Vinheria Agnelo

Aplicação web de e-commerce de vinhos construída com Java, JSP e MySQL, com suporte a deploy no Google Cloud Platform.

## Requisitos

- Java 17
- Maven 3.9+
- MySQL 8.0+

## Configuração local

1. Clone o repositório:
   ```bash
   git clone https://github.com/FIAP-3ESOA-Grupo-de-Estudos/vinheria-agnelo.git
   cd vinheria-agnelo
   ```

2. Crie o banco de dados e execute os scripts:
   ```bash
   mysql -u root -p < sql/schema.sql
   mysql -u root -p vinheria_agnello < sql/seed.sql
   ```

3. Configure as variáveis de ambiente:
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/vinheria_agnello
   export DB_USER=root
   export DB_PASS=sua_senha
   ```

4. Compile e execute:
   ```bash
   mvn compile
   ```

## Deploy no GCP

### Pré-requisitos

- Conta no [Google Cloud](https://cloud.google.com) com um projeto criado
- [gcloud CLI](https://cloud.google.com/sdk/docs/install) instalado e autenticado
- Instância Cloud SQL (MySQL) criada

### Passos

1. Armazene a senha do banco no Secret Manager:
   ```bash
   echo -n "sua_senha" | gcloud secrets create db-pass --data-file=-
   ```

2. Atualize `app.yaml` com os valores do seu projeto:
   ```yaml
   env_variables:
     DB_URL: "jdbc:mysql:///vinheria_agnello?cloudSqlInstance=PROJECT:REGION:INSTANCE&socketFactory=com.google.cloud.sql.mysql.SocketFactory&cloudSqlRefreshStrategy=lazy"
     DB_USER: "seu_usuario"
   ```

3. Faça o deploy:
   ```bash
   mvn package appengine:deploy
   ```

> A senha do banco nunca deve ser adicionada ao `app.yaml` ou qualquer arquivo versionado. Ela e buscada automaticamente no Secret Manager em producao.
