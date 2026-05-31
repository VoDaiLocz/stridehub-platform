#!/usr/bin/env sh
set -eu

if [ -z "${STRIDEHUB_DB_URL:-}" ] && [ -n "${STRIDEHUB_DATABASE_URL:-}" ]; then
  database_url="${STRIDEHUB_DATABASE_URL#postgresql://}"
  database_host_and_path="${database_url#*@}"
  export STRIDEHUB_DB_URL="jdbc:postgresql://${database_host_and_path}"
fi

exec java -jar /app/stridehub.jar
