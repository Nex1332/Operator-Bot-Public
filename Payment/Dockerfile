FROM jelastic/maven:3.9.5-openjdk-21

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/Payment
ADD ./Payment/pom.xml $HOME/Payment
ADD ./Payment/src $HOME/Payment/src
RUN mvn -f $HOME/Payment/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

ADD ./Payment/payment_start.sh /start.sh
RUN chmod +x /start.sh
CMD ["/start.sh"]