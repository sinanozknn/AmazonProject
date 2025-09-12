# Gauge + Java Web UI Test Project Dockerfile
FROM openjdk:17-jdk-slim

# Çalışma dizinini ayarla
WORKDIR /app

# Gerekli sistem paketlerini yükle
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Gauge'yi yükle
RUN curl -SsL https://downloads.gauge.org/stable | sh

# Gauge PATH'ini ayarla
ENV PATH="/root/.gauge/bin:${PATH}"

# Gauge plugin'lerini yükle
RUN gauge install java && \
    gauge install html-report && \
    gauge install xml-report && \
    gauge install screenshot

# Maven bağımlılıklarını önce kopyala ve yükle (cache optimization)
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:resolve && \
    rm -rf /var/lib/apt/lists/*

# Proje dosyalarını kopyala
COPY . .

# Maven ile projeyi derle
RUN mvn clean compile test-compile

# Chrome/Chromium'u headless modda çalıştırmak için yükle
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-driver \
    && rm -rf /var/lib/apt/lists/*

# Chrome driver path'ini ayarla
ENV CHROME_DRIVER_PATH="/usr/bin/chromedriver"
ENV CHROME_BIN="/usr/bin/chromium"

# Varsayılan entrypoint
ENTRYPOINT ["gauge"]

# Varsayılan komut
CMD ["run", "specs"]

# Kullanım örnekleri:
# docker run --rm project-name gauge run specs
# docker run --rm project-name gauge run specs/AmazonCase.spec
# docker run --rm -v $(pwd)/reports:/app/reports project-name gauge run specs
