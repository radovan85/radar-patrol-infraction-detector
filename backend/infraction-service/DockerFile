FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/infraction-service-1.0-SNAPSHOT.zip /app/infraction-service.zip

RUN unzip /app/infraction-service.zip && rm /app/infraction-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

RUN mv /app/infraction-service-1.0-SNAPSHOT /app/infraction-service

RUN chmod +x /app/infraction-service/bin/infraction-service

CMD ["/bin/bash", "-c", "exec /app/infraction-service/bin/infraction-service"]