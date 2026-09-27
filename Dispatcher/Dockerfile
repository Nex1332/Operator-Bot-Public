FROM jelastic/maven:3.9.5-openjdk-21

ENV HOME=/home/app
RUN mkdir -p $HOME

RUN wget https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && \
    mv ngrok /usr/local/bin/ngrok && \
    chmod +x /usr/local/bin/ngrok && \
    rm ngrok-stable-linux-amd64.zip

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/Dispatcher
ADD ./Dispatcher/pom.xml $HOME/Dispatcher
ADD ./Dispatcher/src $HOME/Dispatcher/src
RUN mvn -f $HOME/Dispatcher/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

ADD ./Dispatcher/dispatcher_start.sh /start.sh
RUN chmod +x /start.sh
CMD ["/start.sh"]