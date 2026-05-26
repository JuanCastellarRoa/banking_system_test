#!/usr/bin/env bash
set -e

# Try to determine DB host/port from env or SPRING_DATASOURCE_URL
if [ -n "$DB_HOST" ] && [ -n "$DB_PORT" ]; then
  host="$DB_HOST"
  port="$DB_PORT"
elif [ -n "$SPRING_DATASOURCE_URL" ]; then
  url="$SPRING_DATASOURCE_URL"
  # parse jdbc:postgresql://host:port/db
  url_no_prefix="${url#*://}"
  host_port="${url_no_prefix%%/*}"
  host="${host_port%%:*}"
  port="${host_port##*:}"
else
  echo "[wait-for-db] No DB_HOST/DB_PORT or SPRING_DATASOURCE_URL provided; starting app immediately."
  exec java -jar /app/app.jar
fi

TIMEOUT=${WAIT_TIMEOUT:-60}

echo "[wait-for-db] Waiting for database $host:$port (timeout ${TIMEOUT}s)..."
start_time=$(date +%s)
while ! bash -c "cat < /dev/tcp/$host/$port" >/dev/null 2>&1; do
  sleep 1
  now=$(date +%s)
  elapsed=$((now - start_time))
  if [ "$elapsed" -ge "$TIMEOUT" ]; then
    echo "[wait-for-db] Timeout after ${elapsed}s waiting for $host:$port"
    exit 1
  fi
done

echo "[wait-for-db] Database is reachable. Starting application..."
exec java -jar /app/app.jar
