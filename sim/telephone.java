// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.net.*;
import java.io.*;
import javax.swing.text.Caret;
import javax.sound.sampled.*;
import java.awt.Desktop;
import javax.swing.event.*;
import java.util.Properties;

public class telephone
{
	static final Color cabinet = new Color(165, 125, 14);
	static final Color cabinet_lt = new Color(185, 145, 34);
	static final Color cabinet_dk = new Color(155, 115, 4);

	static final Color drop = new Color(100, 100, 100);
	static final Color drop_lt = new Color(130, 130, 130);
	static final Color drop_dk = new Color(90, 90, 90);
	static final Color drop_void = new Color(200, 200, 200);
	static final Color drop_label = new Color(150, 150, 150);
	static final Color drop_text = new Color(255, 255, 255);

	static final Color edge = new Color(90, 90, 90);
	static final Color edge_lt = new Color(120, 120, 120);
	static final Color edge_dk = new Color(80, 80, 80);
	static final Color edge_label = new Color(110, 110, 110);

	static final Color jack_bev = new Color(180, 180, 180);
	static final Color jack_face = new Color(220, 220, 220);
	static final Color jack_dk = new Color(170, 170, 170);
	static final Color jack_lt = new Color(255, 255, 255);
	static final Color jack_well = new Color(150, 150, 150);
	static final Color jack_hole = new Color(20, 20, 20);

	static final Color plug = new Color(0, 0, 0);
	static final Color plug_sun = new Color(80, 80, 80);
	static final Color plug_lt = new Color(190, 190, 190);
	static final Color plug_dk = new Color(0, 0, 0);

	static final Color ring = new Color(190, 190, 190);
	static final Color tip = new Color(220, 220, 220);
	static final Color tip_lt = new Color(255, 255, 255);
	static final Color tip_dk = new Color(170, 170, 170);

	static final Color well = new Color(100, 60, 0);
	static final Color well_lt = new Color(175, 135, 24);
	static final Color well_dk = new Color(130, 90, 0);
	//static final Color cabinet = new Color(165, 125, 14);

	static final Color cord = new Color(151, 111, 0);

	static final Font font = new Font("Serif", Font.PLAIN, 18);
	static final Font font2 = new Font("Serif", Font.PLAIN, 12);
	static JFrame front_end;

	public static void main(String[] args) {

		front_end = new JFrame("Stromberg-Carlson 1915 Telephone");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);

		StrombergCarlson_Properties prop = new StrombergCarlson_Properties();

		StrombergCarlson_Help help = new StrombergCarlson_Help(front_end);

		StrombergCarlson_Cabinet cab = new StrombergCarlson_Cabinet(
						front_end, prop, help);

		JMenuBar mb = new JMenuBar();
		JMenu mu;
		JMenuItem mi;

		mu = new JMenu("System");
		mb.add(mu);
		mi = new JMenuItem("Setup", KeyEvent.VK_U);
		mi.addActionListener(cab);
		mu.add(mi);

		mu = new JMenu("Help");
		mb.add(mu);
		mi = help.getMenuItemHelp();
		mi.addActionListener(cab);
		mu.add(mi);
		mi = help.getMenuItemAbout();
		mi.addActionListener(cab);
		mu.add(mi);

		front_end.setJMenuBar(mb);

		front_end.add(cab);

		front_end.addKeyListener(cab);
		front_end.getContentPane().setBackground(Color.gray);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}

	static public void fatal(String op, String err) {
		JOptionPane.showMessageDialog(front_end,
			new JLabel(err),
			op + " Error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	static public void warning(String op, String err) {
		JOptionPane.showMessageDialog(front_end,
			new JLabel(err),
			op + " Warning", JOptionPane.WARNING_MESSAGE);
	}
}
