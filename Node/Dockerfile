FROM jelastic/maven:3.9.5-openjdk-21

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/Node
ADD ./Node/pom.xml $HOME/Node
ADD ./Node/src $HOME/Node/src
RUN mvn -f $HOME/Node/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

ADD ./Node/node_start.sh /start.sh
RUN chmod +x /start.sh
CMD ["/start.sh"]