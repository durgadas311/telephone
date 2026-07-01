#

MANIFESTS = sb_manifest.txt t_manifest.txt
JARS = switchboard.jar telephone.jar

PH_CLASS = telephone.class \
	StrombergCarlson_Bell.class \
	StrombergCarlson_Cabinet.class \
	StrombergCarlson_Help.class \
	StrombergCarlson_Magneto.class \
	StrombergCarlson_NamePlate.class \
	StrombergCarlson_Properties.class \
	StrombergCarlson_Rec.class \
	StrombergCarlson_Shelf.class \
	StrombergCarlson_Trans.class

SB_CLASS = switchboard.class \
	Kellogg_Cabinet.class \
	Kellogg_Circuit.class \
	Kellogg_Drop.class \
	Kellogg_Help.class \
	Kellogg_Line.class \
	Kellogg_LineWithDrop.class \
	Kellogg_Magneto.class \
	Kellogg_NightAlarm.class \
	Kellogg_Plug.class \
	Kellogg_Properties.class \
	Kellogg_RingSw.class \
	Kellogg_StSpSw.class \
	Kellogg_SwListener.class

SBX_CLASS = 'Kellogg_Cabinet$$Kellogg_Cabinet_Overlay.class' \
	'Kellogg_NightAlarm$$Alarmer.class'

all: $(SB_CLASS) $(PH_CLASS)

%.class: %.java
	javac $?

clean:

clobber: clean
	rm -f *.class $(JARS) $(MANIFESTS)

jar: $(JARS)

switchboard.jar: $(SB_CLASS) sb_manifest.txt
	jar cfm $@ sb_manifest.txt $(SB_CLASS) $(SBX_CLASS) \
		docs/*.html docs/*.png icons/*.png sounds/*.wav

sb_manifest.txt:
	echo "Main-Class: switchboard" >$@

telephone.jar: $(PH_CLASS) t_manifest.txt
	jar cfm $@ t_manifest.txt $(PH_CLASS) \
		docs/*.html docs/*.png icons/*.png sounds/*.wav

t_manifest.txt:
	echo "Main-Class: telephone" >$@

www: _FRC_
	rsync -Wuv www/*.html www/*.png durgadas.com:/var/www/html/sims/tele-sb-sim
	rsync -Wuv docs/*.html docs/*.png durgadas.com:/var/www/html/sims/tele-sb-sim/docs

ship: $(JARS)
	cp ${JARS} ../
