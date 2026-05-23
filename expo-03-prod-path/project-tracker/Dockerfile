FROM ghcr.io/eclipse-ee4j/glassfish:latest

ENV PATH_GF_HOME=/opt/gfinstall
ENV DEPLOY_DIR=${PATH_GF_HOME}/glassfish/domains/domain1/autodeploy
ENV CUSTOM_DIR=${PATH_GF_HOME}/custom
ENV APP_WAR=${CUSTOM_DIR}/project-tracker.war
ENV INIT_SH=${CUSTOM_DIR}/init.sh

USER root

# 1. Preparar la carpeta que ejecuta el entrypoint oficial de GlassFish
RUN mkdir -p ${CUSTOM_DIR}

# 2. Copiar la aplicación fuera de autodeploy hasta que existan los recursos JDBC
COPY target/project-tracker.war ${APP_WAR}

# 3. Copiar el script de inicialización que prepara GlassFish antes del arranque final
COPY docker-init.sh ${INIT_SH}

ENV MYSQL_CONNECTOR_VERSION=9.7.0

# 4. Descargar el driver JDBC de MySQL y dejarlo disponible para GlassFish
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_CONNECTOR_VERSION}/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar \
    ${PATH_GF_HOME}/glassfish/domains/domain1/lib/mysql-connector-j.jar

RUN chown glassfish:glassfish \
    ${APP_WAR} \
    ${INIT_SH} \
    ${PATH_GF_HOME}/glassfish/domains/domain1/lib/mysql-connector-j.jar

USER glassfish

EXPOSE 8080 4848
