FROM openjdk:8

COPY reporting-viewer/target/saiku-report-viewer-server.jar /

CMD java -jar saiku-report-viewer-server.jar