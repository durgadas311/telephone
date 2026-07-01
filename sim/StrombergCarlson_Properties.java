// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.util.Properties;
import java.awt.*;
import java.io.*;
import javax.swing.*;

class StrombergCarlson_Properties extends Properties
{
	private String _cfg;

	public StrombergCarlson_Properties() {
		_cfg = System.getProperty("user.home") + "/.tele-sb-sim.rc";
		try {
			FileInputStream cfg = new FileInputStream(_cfg);
			load(cfg);
			cfg.close();
		} catch (Exception e) {
			//telephone.warning("Load Setup", e.getMessage());
			// set defaults
			setProperty("switchboard_host", "");
			// save, and force existence of file?
		}
	}

	public void save() {
		try {
			FileOutputStream cfg = new FileOutputStream(_cfg);
			store(cfg, "Saved by telephone");
			cfg.close();
		} catch (Exception e) {
			telephone.warning("Save Setup", e.getMessage());
		}
	}
}
