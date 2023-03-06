FROM registry.sicredi.in/sicredi-openjdk11:latest
EXPOSE 8080
ADD build/libs/investment-approval-config*.jar /opt/api.jar
ENTRYPOINT exec java $JAVA_OPTS $APPDYNAMICS -jar /opt/api.jar