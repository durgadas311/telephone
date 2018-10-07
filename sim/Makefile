# $Id: Makefile,v 1.20 2012/02/19 22:55:04 drmiller Exp $

include Makefile.ship

SHELL=/bin/bash

INSNAME = tele-sb-sim
INSDIR = ~
INSDEST = $(INSDIR)/$(INSNAME)
TMPDIR = temp
TMPDEST = $(TMPDIR)/$(INSNAME)

PNGS = $(wildcard icons/*-*x*.png)
PNGS := $(filter %32x32.png %48x48.png %128x128.png, $(PNGS))
ICNS = $(subst .png,.icns,$(PNGS))
WICONS = $(subst .png,.ico,$(PNGS))

PACKAGES = $(INSNAME).tgz $(INSNAME).exe $(INSNAME).dmg
SRCTAR = $(INSNAME)-src.tgz

clobber: clean
	rm -f *.class $(JARS) $(PACKAGES) $(MANIFESTS)

packages: $(PACKAGES)

temp_src:
	mkdir -p $(TMPDEST)
	cp *.java README LICENSE $(TMPDEST)
	cp Makefile.ship $(TMPDEST)/Makefile
	mkdir -p $(TMPDEST)/{icons,docs,sounds}
	cp icons/*.{png,ico,icns} $(TMPDEST)/icons
	cp docs/*.{html,png} $(TMPDEST)/docs
	cp sounds/*.wav $(TMPDEST)/sounds

ship: tar jars # was: packages
	rsync -Wuv $(JARS) $(SRCTAR) durgadas.com:/http/durgadas.com/sebhc/tele-sb-sim/

tar: temp_src
	tar -czvf $(SRCTAR) -C ${TMPDIR} $(INSNAME)
	rm -rf $(TMPDEST)

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

$(INSNAME).tgz: temp_dest
	tar -czvf $(INSNAME).tgz -C $(TMPDIR) $(INSNAME)
	rm -rf $(TMPDIR)

$(INSNAME).dmg: all icns jars
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
	hcopy -r Uninstall-$(INSNAME).sh :Uninstall-$(INSNAME)
	hcopy -r README.osx :README.html
	humount $@

$(INSNAME).exe: all winicons jars $(INSNAME).nsi
	makensis $(INSNAME).nsi

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
