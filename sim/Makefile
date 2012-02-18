# $Id: Makefile,v 1.14 2012/02/18 21:32:11 drmiller Exp $

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
PACKAGES = tele-sb-sim.tgz tele-sb-sim.zip tele-sb-sim.dmg

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
	cp *.jar run-* INSTALL *.tmplt $(TMPDEST)
	chmod +x $(TMPDEST)/run-* $(TMPDEST)/INSTALL
	mkdir -p $(TMPDEST)/icons
	cp icons/*-*x*.png icons/*-*x*.icns $(TMPDEST)/icons

temp_app: all jars
	for p in Switchboard Telephone; do \
		jar=$${p#T}; jar=$${jar#S}; \
		mkdir -p $(TMPDEST)/$${p}.app/Contents/{Resources,MacOS}; \
		cp *$${jar}.jar $(TMPDEST)/$${p}.app/Contents/MacOS; \
		cp icons/$${p}-*x*.icns $(TMPDEST)/$${p}.app/Contents/Resources/; \
		cp run-$${p} $(TMPDEST)/$${p}.app/Contents/MacOS; \
		chmod +x $(TMPDEST)/$${p}.app/Contents/MacOS/run-$${p}; \
		cp $${p}.plist.tmplt $(TMPDEST)/$${p}.app/Contents/Info.plist; \
	done

wtemp_dest: all winicons jars
	mkdir -p $(TMPDEST)
	cp *.jar *.bat INSTALL.BAT $(TMPDEST)
	cp README.windows $(TMPDEST)/README.txt
	mkdir -p $(TMPDEST)/icons
	cp icons/*-*x*.ico $(TMPDEST)/icons

tele-sb-sim.tgz: temp_dest
	tar -czvf tele-sb-sim.tgz -C $(TMPDIR) $(INSNAME)
	rm -rf $(TMPDIR)

tele-sb-sim.dmg: temp_app
	cd $(TMPDEST); genisoimage -r -apple -no-desktop \
		-V "TELE-SB-SIM" -hfs-volid "TELE-SB-SIM" \
		-o ../../tele-sb-sim.dmg .
	rm -rf $(TMPDIR)

tele-sb-sim.zip: wtemp_dest
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
