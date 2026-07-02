// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

class Kellogg_Help
{
	private JFrame _main;
	private JFrame _frame;
	private JMenuItem _help;
	private JMenuItem _about;

	public JMenuItem getMenuItemHelp() {
		return _help;
	}

	public JMenuItem getMenuItemAbout() {
		return _about;
	}

	public Kellogg_Help(JFrame frame) {
		_main = frame;
		_help = new JMenuItem("Show Help", KeyEvent.VK_H);;
		_about = new JMenuItem("About", KeyEvent.VK_A);

		java.net.URL url = switchboard.class.getResource("docs/operator.html");
		_frame = new GenericHelp("Kellogg Switchboard Help", url);
	}

	public void showAbout() {
		java.net.URL url = switchboard.class.getResource("icons/switchboard.png");
		JLabel lab = new JLabel("<HTML><CENTER>"+
				"Kellogg 1915 Magneto Switchboard<BR>" +
				"Simulator<BR>" +
				"<BR>" +
				"<IMG SRC=\""+url.toString()+"\">" +
				"<BR>" +
				"Developed by Douglas Miller<BR>" +
				"http://sims.durgadas.com<BR>" +
				"</CENTER></HTML>");
		JOptionPane.showMessageDialog(_main, lab,
			"About: Switchboard Simulator", JOptionPane.PLAIN_MESSAGE);
	}

	public void setOn(boolean on) {
		_frame.setVisible(on);
	}
}
