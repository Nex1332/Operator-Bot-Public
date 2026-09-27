FROM jelastic/maven:3.9.5-openjdk-21

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/Task
ADD ./Task/pom.xml $HOME/Task
ADD ./Task/src $HOME/Task/src
RUN mvn -f $HOME/Task/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/Task/target/Task-0.0.1.jar