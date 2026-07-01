// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.util.Properties;
import java.io.*;

class Kellogg_Properties extends Properties
{
	private String _cfg;

	public Kellogg_Properties() {
		_cfg = System.getProperty("user.home") + "/.tele-sb-sim.rc";
		try {
			FileInputStream cfg = new FileInputStream(_cfg);
			load(cfg);
			cfg.close();
		} catch (Exception e) {
			//switchboard.warning("Load Setup", e.getMessage());
			// set defaults
			setProperty("switchboard_lines", "");
			setProperty("switchboard_circuits", "");
			setProperty("switchboard_night_alarm", "");
			setProperty("switchboard_toll_line", "");
			setProperty("switchboard_host", "");
			// save, and force existence of file?
		}
	}

	public int getInteger(String prop) {
		try {
			return Integer.valueOf(getProperty(prop));
		} catch (Exception e) {
			return 0;
		}
	}

	public boolean getBoolean(String prop) {
		try {
			return Boolean.valueOf(getProperty(prop));
		} catch (Exception e) {
			return false;
		}
	}

	public void save() {
		try {
			FileOutputStream cfg = new FileOutputStream(_cfg);
			store(cfg, "Saved by switchboard");
			cfg.close();
		} catch (Exception e) {
			switchboard.warning("Save Setup", e.getMessage());
		}
	}
}
