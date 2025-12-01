#!/bin/bash

echo "========================================"
echo "Deteniendo todos los servicios..."
echo "========================================"
echo ""

if [ -f .pids ]; then
    PIDS=$(cat .pids)
    for PID in $PIDS; do
        if ps -p $PID > /dev/null 2>&1; then
            echo "Deteniendo proceso $PID..."
            kill $PID 2>/dev/null
        fi
    done
    rm .pids
fi

# También intentar detener cualquier proceso Java de Spring Boot
pkill -f "spring-boot:run" 2>/dev/null

echo ""
echo "Todos los servicios han sido detenidos."

