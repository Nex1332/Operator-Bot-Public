FROM jelastic/maven:3.9.5-openjdk-21

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/Operator
ADD ./Operator/pom.xml $HOME/Operator
ADD ./Operator/src $HOME/Operator/src
RUN mvn -f $HOME/Operator/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/Operator/target/Operator-0.0.1.jar