FROM jenkins/jenkins:lts-jdk17

USER root

# Instala Maven e cliente Docker
RUN apt-get update && \
    apt-get install -y maven docker.io && \
    rm -rf /var/lib/apt/lists/*

# Copia os plugins oficiais Docker Compose v2 e Buildx diretamente da imagem do Docker
COPY --from=docker:cli /usr/local/libexec/docker/cli-plugins/docker-compose /usr/local/libexec/docker/cli-plugins/docker-compose
COPY --from=docker:cli /usr/local/libexec/docker/cli-plugins/docker-buildx /usr/local/libexec/docker/cli-plugins/docker-buildx

# Libera a leitura do repositório montado do Windows no Git
RUN git config --system --add safe.directory /var/workspace/vinheria-agnello && \
    git config --system --add safe.directory /var/workspace/vinheria-agnello/.git

USER jenkins

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc.yaml

RUN jenkins-plugin-cli --plugins \
    workflow-aggregator \
    git \
    configuration-as-code \
    job-dsl

COPY casc.yaml /var/jenkins_home/casc.yaml