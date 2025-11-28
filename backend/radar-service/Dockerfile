FROM tomcat:10.1-jdk21

WORKDIR /app

COPY target/radar-service.war /app/app.war

# Promena porta iz default 8080 u 8082
RUN sed -i 's/port="8080"/port="8082"/' /usr/local/tomcat/conf/server.xml

# Deploy WAR kao ROOT aplikaciju
RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

# Environment varijable za bazu
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

CMD ["catalina.sh", "run"]
