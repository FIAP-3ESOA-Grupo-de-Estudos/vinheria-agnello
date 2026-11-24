pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    // Checks the repository for new commits every 2 minutes: a commit on the branch starts a deploy.
    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        APP_NAME   = 'vinheria-agnello'
        // Publishing folder: the vinheria-deploy volume (see infra/docker-compose.jenkins.yml).
        DEPLOY_DIR = '/var/www/vinheria-agnello'
        // Tomcat's webapps folder: the vinheria-webapps volume, shared with the vinheria-web container.
        WEBAPPS_DIR = '/var/www/webapps'
        WEBAPPS_VOLUME = 'vinheria-webapps'
    }

    stages {
        // The code comes from the Git checkout Jenkins does before the first stage (Declarative: Checkout SCM).

        stage('1. Build') {
            steps {
                echo "Deploy da Vinheria - compilando ${APP_NAME} (commit ${env.GIT_COMMIT})"
                sh 'mvn -B clean package'
            }
        }

        stage('2. Testes dos microsserviços') {
            steps {
                sh 'cd services/catalog-service && mvn -B test'
                sh 'cd services/order-service && mvn -B test'
            }
        }

        stage('3. Publicar artefatos') {
            steps {
                echo "Publicando em ${DEPLOY_DIR}..."
                sh '''
                    set -eu
                    rm -rf "${DEPLOY_DIR:?}/infra" "${DEPLOY_DIR:?}/services"
                    mkdir -p "${DEPLOY_DIR}" "${WEBAPPS_DIR}"
                    cp target/*.war "${WEBAPPS_DIR}/ROOT.war"
                    cp -R infra services "${DEPLOY_DIR}/"
                    printf 'Deploy da Vinheria concluído em %s, commit %s\\n' "$(date)" "${GIT_COMMIT}" > "${DEPLOY_DIR}/deploy_info.txt"
                    cat "${DEPLOY_DIR}/deploy_info.txt"
                '''
            }
        }

        stage('4. Deploy da aplicação web (Tomcat)') {
            steps {
                sh '''
                    set -eu
                    docker rm -f vinheria-web >/dev/null 2>&1 || true
                    docker run -d --name vinheria-web --restart unless-stopped \
                      -p 127.0.0.1:8086:8080 \
                      -v "${WEBAPPS_VOLUME}:/usr/local/tomcat/webapps" \
                      tomcat:9.0-jdk17
                '''
            }
        }

        stage('5. Deploy dos microsserviços (Docker Compose)') {
            steps {
                echo 'Subindo dnsmasq, Catalog Service, Order Service e probe...'
                // Rebuilds only what changed and recreates only the containers whose image or configuration changed.
                sh 'docker compose -f "${DEPLOY_DIR}/infra/docker-compose.yml" up -d --build --remove-orphans'
            }
        }

        stage('6. Verificar o ambiente') {
            steps {
                sh 'bash "${DEPLOY_DIR}/infra/verify.sh"'
                // Tomcat takes a few seconds to unpack ROOT.war.
                sh '''
                    for attempt in $(seq 1 30); do
                      docker exec vinheria-web curl -fsS -o /dev/null http://localhost:8080/ && { echo "Tomcat: aplicação web no ar"; exit 0; }
                      sleep 2
                    done
                    echo "Tomcat: a aplicação web não respondeu em 60 s" >&2
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Deploy concluído! Web: http://127.0.0.1:8086 | Catalog: http://127.0.0.1:8081/health | Order: http://127.0.0.1:8082/health'
        }
        failure {
            echo "Falha no pipeline do ${APP_NAME}. Veja o estágio com erro no Console Output."
        }
    }
}
