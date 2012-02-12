# $Id: Makefile,v 1.6 2012/02/12 21:15:26 drmiller Exp $

INSNAME = tele-sb-sim
INSDIR = ~
INSDEST = $(INSDIR)/$(INSNAME)
TMPDIR = temp
TMPDEST = $(TMPDIR)/$(INSNAME)

PNGS = $(wildcard icons/*-*x*.png)
PNGS := $(filter %32x32.png %48x48.png %128x128.png, $(PNGS))
ICNS = $(subst .png,.icns,$(PNGS))
WICONS = $(subst .png,.ico,$(PNGS))

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

temp_dest: all jar
	mkdir -p $(TMPDEST)
	cp *.jar run-* INSTALL *.tmplt $(TMPDEST)
	chmod +x $(TMPDEST)/run-* $(TMPDEST)/INSTALL
	mkdir -p $(TMPDEST)/icons
	cp icons/*-*x*.png icons/*-*x*.icns $(TMPDEST)/icons

wtemp_dest: all winicons jar
	mkdir -p $(TMPDEST)
	cp *.jar *.bat INSTALL.BAT $(TMPDEST)
	cp README.windows $(TMPDEST)/README.txt
	mkdir -p $(TMPDEST)/icons
	cp icons/*-*x*.ico $(TMPDEST)/icons

package: temp_dest
	tar -czvf tele-sb-sim.tgz -C $(TMPDIR) $(INSNAME)
	rm -rf $(TMPDIR)

winpack: wtemp_dest
	cd $(TMPDIR); rm -f ../tele-sb-sim.zip; zip -r ../tele-sb-sim.zip $(INSNAME)
	rm -rf $(TMPDIR)

install: temp_dest
	rm -rf $(INSDEST)
	mv $(TMPDEST) $(INSDIR)
	rmdir $(TMPDIR)
	cd $(INSDEST); ./INSTALL

icns: $(ICNS)

%.icns: %.png
	sips -s format icns $? --out $@

winicons: $(WICONS)

%.ico: %.png
	sips -s format ico $? --out $@

_FRC_:
