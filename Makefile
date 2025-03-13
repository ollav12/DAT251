.PHONY: all frontend backend

all: backend frontend

backend:
	cd demo && ./gradlew clean build

frontend:
	cd frontend && npm ci && npm run build
