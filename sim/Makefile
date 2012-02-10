# $Id: Makefile,v 1.3 2012/02/10 22:53:21 drmiller Exp $

switchboard.class: switchboard.java
	javac switchboard.java

jar: switchboard.jar

switchboard.jar: switchboard.class Manifest.txt
	jar cfm $@ Manifest.txt *.class

Manifest.txt:
	echo "Main-Class: switchboard" >$@


www: _FRC_
	mkdir -p /http/durgadas.com/tele-sb-sim
	cp www/*.html www/*.png /http/durgadas.com/tele-sb-sim

_FRC_:
