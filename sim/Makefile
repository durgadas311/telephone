# $Id: Makefile,v 1.5 2012/02/12 20:16:27 drmiller Exp $

all: switchboard.class telephone.class

switchboard.class: switchboard.java
	javac switchboard.java

telephone.class: telephone.java
	javac telephone.java

jar: switchboard.jar telephone.jar

switchboard.jar: switchboard.class sb_manifest.txt
	jar -c -f $@ -m sb_manifest.txt switchboard.class Kellogg*.class

sb_manifest.txt:
	echo "Main-Class: switchboard" >$@

telephone.jar: telephone.class t_manifest.txt
	jar -c -f $@ -m t_manifest.txt telephone.class StrombergCarlson*.class

t_manifest.txt:
	echo "Main-Class: telephone" >$@


www: _FRC_
	mkdir -p /http/durgadas.com/tele-sb-sim
	cp www/*.html www/*.png /http/durgadas.com/tele-sb-sim

_FRC_:
