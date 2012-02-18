Name "Switchboard Simulator"
OutFile "tele-sb-sim.exe"
Section "Switchboard Simulator"
	ReadEnvStr $0 HOMEDRIVE
	ReadEnvStr $1 HOMEPATH
	StrCpy $INSTDIR "$0$1\tele-sb-sim"
	SetOutPath $INSTDIR
	CreateDirectory $INSTDIR
	File "switchboard.jar"
	File "Switchboard.bat"
	File "telephone.jar"
	File "Telephone.bat"
	File /r "icons\*.ico"
	CreateShortCut "$DESKTOP\Switchboard.lnk" "$INSTDIR\Switchboard.bat" "" \
		"$INSTDIR\Switchboard-48x48.ico" 0 SW_SHOWMINIMIZED
	CreateShortCut "$DESKTOP\Telephone.lnk" "$INSTDIR\Telephone.bat" "" \
		"$INSTDIR\Telephone-48x48.ico" 0 SW_SHOWMINIMIZED
	WriteUninstaller $INSTDIR\uninstall.exe
SectionEnd
Section "Uninstall"
	Delete $DESKTOP\Switchboard.lnk
	Delete $DESKTOP\Telephone.lnk
	Delete $INSTDIR\*.jar
	Delete $INSTDIR\*.bat
	Delete $INSTDIR\*.ico
	Delete $INSTDIR\uninstall.exe
	RMDir $INSTDIR
SectionEnd
