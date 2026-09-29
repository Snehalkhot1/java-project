FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY src /app/src
COPY bills.dat /app/bills.dat
COPY inventory.dat /app/inventory.dat

RUN mkdir -p /app/bin
RUN javac -d /app/bin $(find /app/src -name "*.java")

CMD ["java", "-cp", "/app/bin", "com.fruitshop.Main"]
