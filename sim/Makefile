# $Id: Makefile,v 1.4 2012/02/11 16:24:21 drmiller Exp $

all: switchboard.class telephone.class

switchboard.class: switchboard.java
	javac switchboard.java

telephone.class: telephone.java
	javac telephone.java

jar: switchboard.jar 

switchboard.jar: switchboard.class telephone.class Manifest.txt
	jar cfm $@ Manifest.txt *.class

Manifest.txt:
	echo "Main-Class: switchboard" >$@


www: _FRC_
	mkdir -p /http/durgadas.com/tele-sb-sim
	cp www/*.html www/*.png /http/durgadas.com/tele-sb-sim

_FRC_:
