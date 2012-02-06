# $Id: Makefile,v 1.2 2012/02/06 15:09:17 drmiller Exp $

switchboard.class: switchboard.java
	javac switchboard.java

jar: switchboard.jar

switchboard.jar: switchboard.class Manifest.txt
	jar cfm $@ Manifest.txt *.class

Manifest.txt:
	echo "Main-Class: switchboard" >$@

