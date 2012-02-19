# $Id: Makefile,v 1.18 2012/02/19 22:17:02 drmiller Exp $

INSNAME = tele-sb-sim
INSDIR = ~
INSDEST = $(INSDIR)/$(INSNAME)
TMPDIR = temp
TMPDEST = $(TMPDIR)/$(INSNAME)

PNGS = $(wildcard icons/*-*x*.png)
PNGS := $(filter %32x32.png %48x48.png %128x128.png, $(PNGS))
ICNS = $(subst .png,.icns,$(PNGS))
WICONS = $(subst .png,.ico,$(PNGS))

MANIFESTS = sb_manifest.txt t_manifest.txt
JARS = switchboard.jar telephone.jar
PACKAGES = tele-sb-sim.tgz tele-sb-sim.exe tele-sb-sim.dmg

all: switchboard.class telephone.class

clean:

clobber: clean
	rm -f *.class $(JARS) $(PACKAGES) $(MANIFESTS)

switchboard.class: switchboard.java
	javac switchboard.java

telephone.class: telephone.java
	javac telephone.java

jars: $(JARS)

packages: $(PACKAGES)

switchboard.jar: switchboard.class sb_manifest.txt
	jar cfm $@ sb_manifest.txt switchboard.class Kellogg*.class \
		docs/*.html docs/*.png icons/*.png sounds/*.wav

sb_manifest.txt:
	echo "Main-Class: switchboard" >$@

telephone.jar: telephone.class t_manifest.txt
	jar cfm $@ t_manifest.txt telephone.class StrombergCarlson*.class \
		docs/*.html docs/*.png icons/*.png sounds/*.wav

t_manifest.txt:
	echo "Main-Class: telephone" >$@


www: _FRC_
	mkdir -p /http/durgadas.com/sims/tele-sb-sim/docs
	cp www/*.html www/*.png /http/durgadas.com/sims/tele-sb-sim
	cp docs/*.html docs/*.png /http/durgadas.com/sims/tele-sb-sim/docs

temp_dest: all jars
	mkdir -p $(TMPDEST)
	cp *.jar run-* INSTALL UNINSTALL *.desktop.tmplt $(TMPDEST)
	chmod +x $(TMPDEST)/run-* $(TMPDEST)/INSTALL $(TMPDEST)/UNINSTALL
	mkdir -p $(TMPDEST)/icons
	cp icons/*-*x*.png $(TMPDEST)/icons

tele-sb-sim.tgz: temp_dest
	tar -czvf tele-sb-sim.tgz -C $(TMPDIR) $(INSNAME)
	rm -rf $(TMPDIR)

tele-sb-sim.dmg: all icns jars
	rm -f $@
	dd if=/dev/zero of=$@ bs=64k count=16
	hformat -l TELE-SB-SIM $@
	hmount $@
	for p in Switchboard Telephone; do \
		jar=$${p#T}; jar=$${jar#S}; \
		hmkdir $${p}.app; \
		hcd $${p}.app; \
		hmkdir Contents; \
		hcd Contents; \
		hcopy -r $${p}.plist.tmplt :Info.plist; \
		hmkdir Resources MacOS; \
		hcopy -r *$${jar}.jar :MacOS; \
		hcopy -r run-$${p} :MacOS; \
		hcopy -r icons/$${p}-*x*.icns :Resources; \
		hcd; \
	done
	hcopy -r Uninstall-tele-sb-sim.sh :Uninstall-tele-sb-sim
	hcopy -r README.osx :README.html
	humount $@

tele-sb-sim.exe: all winicons jars tele-sb-sim.nsi
	makensis tele-sb-sim.nsi

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
