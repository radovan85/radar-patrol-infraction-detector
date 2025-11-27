FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/registration-service-1.0-SNAPSHOT.zip /app/registration-service.zip

RUN unzip /app/registration-service.zip && rm /app/registration-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

RUN mv /app/registration-service-1.0-SNAPSHOT /app/registration-service

RUN chmod +x /app/registration-service/bin/registration-service

CMD ["/bin/bash", "-c", "exec /app/registration-service/bin/registration-service"]