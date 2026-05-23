#!/bin/bash
set -euo pipefail

GF_HOME="${PATH_GF_HOME:-/opt/gfinstall}"
APP_WAR="${GF_HOME}/custom/project-tracker.war"
DEPLOY_WAR="${GF_HOME}/glassfish/domains/domain1/autodeploy/project-tracker.war"

: "${DB_HOST:?DB_HOST is required}"
: "${DB_PORT:?DB_PORT is required}"
: "${DB_NAME:?DB_NAME is required}"
: "${DB_USER:?DB_USER is required}"
: "${DB_PASSWORD:?DB_PASSWORD is required}"

cleanup() {
    asadmin --interactive=false stop-domain >/dev/null 2>&1 || true
}

trap cleanup EXIT

asadmin --interactive=false start-domain

if ! asadmin --interactive=false list-jdbc-connection-pools | grep -Fxq "ProjectTrackerPool"; then
    asadmin --interactive=false create-jdbc-connection-pool \
        --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
        --restype javax.sql.DataSource \
        --property "serverName=${DB_HOST}:portNumber=${DB_PORT}:databaseName=${DB_NAME}:user=${DB_USER}:password=${DB_PASSWORD}:useSSL=false:allowPublicKeyRetrieval=true" \
        ProjectTrackerPool
fi

if ! asadmin --interactive=false list-jdbc-resources | grep -Fxq "jdbc/projectTracker"; then
    asadmin --interactive=false create-jdbc-resource \
        --connectionpoolid ProjectTrackerPool \
        jdbc/projectTracker
fi

asadmin --interactive=false stop-domain
trap - EXIT

cp "${APP_WAR}" "${DEPLOY_WAR}"
