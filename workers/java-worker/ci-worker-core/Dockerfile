# Base worker for java

FROM java:8

MAINTAINER Keiichiro Ono <kono@ucsd.edu>

RUN apt-get update && apt-get install -y build-essential wget git uuid-dev maven
RUN apt-get install -y libtool pkg-config autoconf automake

# Install ZeroMQ v4
WORKDIR /root
RUN wget http://download.zeromq.org/zeromq-4.1.3.tar.gz \
		&& tar xzvf zeromq-4.1.3.tar.gz
WORKDIR /root/zeromq-4.1.3
RUN ./configure --without-libsodium && make && make install && ldconfig

# Java binding
RUN ln -s /usr/bin/libtoolize /usr/bin/libtool

WORKDIR /root
RUN git clone https://github.com/zeromq/jzmq.git
WORKDIR /root/jzmq
RUN ./autogen.sh && ./configure && make && make install
RUN ldconfig

# Build application
RUN mkdir /worker
WORKDIR /worker
COPY . /worker

EXPOSE 5556 5557 5558 5563

# Set path to JRE 8
RUN mvn clean install:install-file -Dfile=./jars/cxio-0.9.0.jar -DgroupId=org.cxio -DartifactId=cxio -Dversion=0.0.1 -Dpackaging=jar
RUN mvn clean compile assembly:single

CMD ["./run.sh"]
