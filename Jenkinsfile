pipeline {
    agent any

    environment {
        APP_NAME        = 'vinheria-agnello'
        SOURCE_DIR      = '/var/workspace/vinheria-agnello'
        DEPLOY_DIR      = '/var/www/vinheria-agnello'
        HOST_DEPLOY_DIR = 'C:/deploy-vinheria'
    }

    stages {
        stage('1. Preparar Workspace') {
            steps {
                echo "Preparando código-fonte do projeto ${APP_NAME}..."
                sh "cp -rf ${SOURCE_DIR}/. ."
            }
        }

        stage('2. Build Maven') {
            steps {
                echo 'Deploy da Vinheria - Compilando projeto Java com Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3. Publicar Artefatos') {
            steps {
                echo "Copiando artefatos, serviços e infra para ${DEPLOY_DIR}..."
                sh '''
                    mkdir -p ${DEPLOY_DIR}
                    rm -rf ${DEPLOY_DIR}/ROOT.war ${DEPLOY_DIR}/*.jar ${DEPLOY_DIR}/infra ${DEPLOY_DIR}/services
                    cp -f target/*.war ${DEPLOY_DIR}/ROOT.war 2>/dev/null || true
                    cp -f target/*.jar ${DEPLOY_DIR}/ 2>/dev/null || true
                    cp -rf infra ${DEPLOY_DIR}/
                    cp -rf services ${DEPLOY_DIR}/ 2>/dev/null || true
                    echo "Deploy da Vinheria concluído em $(date)" > ${DEPLOY_DIR}/deploy_info.txt
                '''
            }
        }

        stage('4. Deploy App (Tomcat)') {
            steps {
                echo 'Subindo aplicação web Java (.war) no Tomcat...'
                sh '''
                    if [ -f "${DEPLOY_DIR}/ROOT.war" ]; then
                        docker rm -f vinheria-web 2>/dev/null || true
                        docker run -d --name vinheria-web \
                          -p 127.0.0.1:8086:8080 \
                          -v "${HOST_DEPLOY_DIR}:/usr/local/tomcat/webapps" \
                          tomcat:9.0-jdk17
                    else
                        echo "Nenhum arquivo .war encontrado para subir no Tomcat."
                    fi
                '''
            }
        }

        stage('5. Deploy Infra (Docker Compose)') {
            steps {
                echo 'Subindo microsserviços e validando DHCP/DNS (dnsmasq, catalog, order, probe)...'
                sh '''
                    docker compose -f ${DEPLOY_DIR}/infra/docker-compose.yml down --remove-orphans || true
                    docker compose -f ${DEPLOY_DIR}/infra/docker-compose.yml build --no-cache
                    docker compose -f ${DEPLOY_DIR}/infra/docker-compose.yml up -d
                '''
            }
        }
    }

    post {
        success {
            echo "Deploy concluído! Web: http://127.0.0.1:8086 | Catalog: http://127.0.0.1:8081/health | Order: http://127.0.0.1:8082/health"
        }
        failure {
            echo "Falha durante o pipeline do ${APP_NAME}. Verifique o estágio com erro no Console Output."
        }
    }
}