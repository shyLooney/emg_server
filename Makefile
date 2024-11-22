local: docker-build run

clean:
	docker stop emg-server & mvn clean & docker rm emg-server

java-build:
	mvn clean package

docker-build:
	docker build -f Dockerfile -t emg-server:latest .

run:
	docker-compose up -d