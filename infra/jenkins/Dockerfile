FROM jenkins/jenkins:lts-jdk17

USER root

# Maven for the builds, curl for the checks; the Docker CLI comes from the official image below.
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven curl && \
    rm -rf /var/lib/apt/lists/*

# Docker CLI with the Compose v2 and Buildx plugins, from the official Docker image.
COPY --from=docker:cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker:cli /usr/local/libexec/docker/cli-plugins/docker-compose /usr/local/libexec/docker/cli-plugins/docker-compose
COPY --from=docker:cli /usr/local/libexec/docker/cli-plugins/docker-buildx /usr/local/libexec/docker/cli-plugins/docker-buildx

# The repository is mounted from the host, owned by another user: let Git read it.
RUN git config --system --add safe.directory '*'

USER jenkins

# No setup wizard (JCasC configures everything). Local checkout: the job clones the mounted repository
# through a file:// URL, which the Git plugin refuses unless allowed.
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true"
# Outside JENKINS_HOME, so a new image's configuration is not hidden by the jenkins-home volume.
ENV CASC_JENKINS_CONFIG=/usr/share/jenkins/casc.yaml

RUN jenkins-plugin-cli --plugins \
    workflow-aggregator \
    git \
    configuration-as-code \
    job-dsl \
    timestamper

COPY casc.yaml /usr/share/jenkins/casc.yaml
