#!/bin/sh
# $Id: Uninstall-tele-sb-sim.sh,v 1.1 2012/02/18 23:40:15 drmiller Exp $

# paths might contain blanks!
cd "${HOME}"

for title in Switchboard Telephone; do
	if [[ -d "${HOME}/Desktop/${title}.app" ]]; then
		echo "Removing ${title} App..."
		rm -rf "${HOME}/Desktop/${title}.app"
	fi
done
