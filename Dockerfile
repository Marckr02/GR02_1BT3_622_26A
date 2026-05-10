FROM tomcat:9.0-jdk21

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/GR02_1BT3_622_26A.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD bash -c "catalina.sh start && sleep 8 && \
    echo '' && \
    echo '╔══════════════════════════════════════════════════════════════════╗' && \
    echo '║                                                                  ║' && \
    echo '║        Despliegue realizado exitosamente.                        ║' && \
    echo '║                                                                  ║' && \
    echo '║   Ingrese al localhost desde su navegador con su puerto          ║' && \
    echo '║   seleccionado para entrar a la aplicacion del                   ║' && \
    echo '║   Sistema de gestion de Dark Kitchens.                           ║' && \
    echo '║                                                                  ║' && \
    echo '╚══════════════════════════════════════════════════════════════════╝' && \
    echo '' && \
    tail -f /usr/local/tomcat/logs/catalina.out"