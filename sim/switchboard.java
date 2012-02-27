// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.Timer;
import javax.swing.border.*;
import java.net.*;
import java.io.*;
import javax.swing.text.Caret;
import javax.sound.sampled.*;
import java.awt.Desktop;
import javax.swing.event.*;
import java.util.Properties;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);

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

	static final int UNKNOWN = 0;
	static final int TELEPHONE = 1;
	static final int SWITCHBOARD = 2;

	static final Font font = new Font("Serif", Font.PLAIN, 18);
	static final Font font2 = new Font("Serif", Font.PLAIN, 12);
	static JFrame front_end;

	public static void main(String[] args) {
		front_end = new JFrame("Kellogg 1915 Telephone Switchboard");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);

		Kellogg_Properties prop = new Kellogg_Properties();

		Kellogg_Help help = new Kellogg_Help(front_end);

		Kellogg_Cabinet cab = new Kellogg_Cabinet(prop,
						front_end, help);

		JMenuBar mb = new JMenuBar();
		JMenu mu;
		JMenuItem mi;

		mu = new JMenu("System");
		mb.add(mu);
		mi = new JMenuItem("Setup", KeyEvent.VK_U);
		mi.addActionListener(cab);
		mu.add(mi);
		mi = new JMenuItem("Directory", KeyEvent.VK_D);
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

class Kellogg_Properties extends Properties
{
	static final long serialVersionUID = 311000000014L;
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

class Kellogg_Help extends JComponent
	implements ActionListener, WindowListener, ComponentListener, HyperlinkListener
{
	static final long serialVersionUID = 311000000013L;
	private JFrame _frame;
	private JEditorPane _text;
	private JScrollPane _scroll;
	private int _xoff, _yoff;
	private JMenuItem _help;
	private JMenuItem _about;
	private boolean _help_on;
	private JFrame _main;

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
		_help_on = false;

		java.net.URL url = switchboard.class.getResource("docs/operator.html");
		_frame = new JFrame("Kellogg Switchboard Help");
		_frame.setLayout(new FlowLayout());
		try {
			_text = new JEditorPane(url);
		} catch (Exception ee) {
			switchboard.fatal("Help Setup", ee.getMessage());
		}
		_text.setEditable(false);
		_text.setFont(new Font("Sans-serif", Font.PLAIN, 12));
		int z = _text.getFont().getSize();
		_text.addHyperlinkListener(this);

		_scroll = new JScrollPane(_text);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroll.setPreferredSize(new Dimension(60 * z, 32 * z));

		JMenuBar mb = new JMenuBar();
		JMenu mu;
		mu = new JMenu("Topic");
		mb.add(mu);
		JMenuItem mi;
		mi = new JMenuItem("Operator Manual", KeyEvent.VK_B);
		mi.addActionListener(this);
		mu.add(mi);
		mi = new JMenuItem("About the Simulator", KeyEvent.VK_S);
		mi.addActionListener(this);
		mu.add(mi);
		mi = new JMenuItem("Resources and Links", KeyEvent.VK_L);
		mi.addActionListener(this);
		mu.add(mi);

		_frame.setJMenuBar(mb);
		_frame.add(_scroll);
		_frame.pack();

		_frame.addWindowListener(this);
		_frame.addComponentListener(this);

		Dimension fdim = _frame.getSize();
		Dimension sdim = _scroll.getSize();
		_xoff = fdim.width - sdim.width;
		_yoff = fdim.height - sdim.height;
	}

	public void showAbout() {
		java.net.URL url = switchboard.class.getResource("icons/switchboard.png");
		JLabel lab = new JLabel("<HTML><CENTER>"+
				"Kellogg 1915 Magneto Switchboard<BR>" +
				"Simulator<BR>" +
				"$Revision: 1.54 $ $Date: 2012/02/27 00:39:01 $<BR>" +
				"<BR>" +
				"<IMG SRC=\""+url.toString()+"\">" +
				"<BR>" +
				"Developed by Douglas Miller<BR>" +
				"http://sims.durgadas.com<BR>" +
				"</CENTER></HTML>");
		JOptionPane.showMessageDialog(_main, lab,
			"About: Switchboard Simulator", JOptionPane.PLAIN_MESSAGE);
	}

	public void toggle() {
		setOn(!_help_on);
	}

	private void setOn(boolean on) {
		_help_on = on;
		if (on) {
			_frame.pack();
			_help.setText("Hide Help");
		} else {
			_help.setText("Show Help");
		}
		_frame.setVisible(on);
	}

	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }

	public void windowClosing(WindowEvent e) {
		if (e.getWindow() == _frame) {
			setOn(false);
			return;
		}
	}

	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }

	public void componentResized(ComponentEvent e) {
		if (e.getComponent() == _frame) {
			Dimension fdim = _frame.getSize();
			_scroll.setSize(fdim.width - _xoff, fdim.height - _yoff);
			_scroll.setPreferredSize(_scroll.getSize());
			_frame.setSize(fdim.width, fdim.height); // redundant?
			_frame.setPreferredSize(_frame.getSize());
			return;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem m = (JMenuItem)e.getSource();
			java.net.URL url = null;
			// should use a table to lookup url?
			if (m.getMnemonic() == KeyEvent.VK_B) {
				url = switchboard.class.getResource("docs/operator.html");
			} else if (m.getMnemonic() == KeyEvent.VK_S) {
				url = switchboard.class.getResource("docs/switchboard_sim.html");
			} else if (m.getMnemonic() == KeyEvent.VK_L) {
				url = switchboard.class.getResource("docs/links.html");
			} else {
				System.err.println("help menu " + e.getActionCommand() +
						" not implemented yet");
				return;
			}
			try {
				_text.setPage(url);
			} catch (IOException ee) {
			}
			return;
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent r) {
		if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (r.getURL().getProtocol().compareTo("file") == 0 ||
			    r.getURL().getProtocol().compareTo("jar") == 0) {
				String doc = r.getURL().getFile();
				if (r.getURL().getProtocol().compareTo("jar") == 0) {
					// ugh! must be a better way...
					doc = doc.replaceFirst("/switchboard\\.jar!/","/");
					doc = doc.replaceFirst("file:","");
				}
				try {
					Desktop.getDesktop().open(new File(doc));
				} catch (IOException e) {
					System.err.println("Exception "+e.getMessage()+" trying to open file "+
						r.getURL().getProtocol()+" "+r.getURL().getFile());
				} catch(Exception e) {
					System.err.println("Exception "+e.getMessage()+" trying to open file "+
						r.getURL().getProtocol()+" "+r.getURL().getFile());
				}
			} else {
				try {
					Desktop.getDesktop().browse(r.getURL().toURI());
				} catch (IOException e) {
					System.err.println("Exception trying to follow link "+
						r.getURL().toString());
				} catch(Exception e) {
					System.err.println("Exception trying to follow link "+
						r.getURL().toString());
				}
			}
		}
	}
}

interface Kellogg_SwListener {
	void listener(JComponent sw, boolean state);
}

class Kellogg_Cabinet extends JLayeredPane
		implements Runnable, KeyListener, ActionListener, Kellogg_SwListener
{
	static final long serialVersionUID = 311000000010L;

	public static final int border_size = 2;
	public static final int text_height = 100;

	public static FontMetrics font_metrics;
	public static FontMetrics font2_metrics;

	private int lines_per_row;
	private int max_circs;

	private Kellogg_Plug _sel_plug;

	private Kellogg_Plug[] _conn_plugs;
	private Kellogg_Line[] _conn_lines;

	private Kellogg_Line[] _lines;
	private int _nlines;

	private Kellogg_NightAlarm _night_alarm;

	private int cab_width;
	private ServerSocket _ss;

	private Component _top;
	private String _txt;
	private JTextArea _text;
	private JScrollPane _scroll;
	private int _listen_h;
	private int _no_listen_h;
	private int _listening;

	private Kellogg_Help _help;
	private Kellogg_Properties _prop;

	private JTextField _nc_t;
	private JPanel _nc_f;
	private int _nc;
	private JTextField _nl_t;
	private JPanel _nl_f;
	private int _nl;
	private Checkbox _na_t;
	private JPanel _na_f;
	private boolean _na;
	private Checkbox _tc_t;
	private JPanel _tc_f;
	private boolean _tc;
	private JTextArea _host_t;
	private JPanel _host_f;
	private String[] _hosts;

	private class Kellogg_Cabinet_Overlay extends JPanel
	{
		static final long serialVersionUID = 311000000012L;

		private final int[] tip_x = { 0, 5, 9,14, 0 };
		private final int[] tip_y = { 0,-7,-7, 0, 0 };

		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D)g;
			super.paint(g2d);
			g2d.setColor(switchboard.cord);
			g2d.setStroke(new BasicStroke((float)12.0,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			int i;
			for (i = 0; i < _conn_plugs.length; ++i) {
				if (_conn_plugs[i] != null) {
					Point p = _conn_plugs[i].getCoords();
					Point l = _conn_lines[i].getCoords();
					g2d.drawLine(p.x, p.y, l.x, l.y);
				}
			}
			if (_sel_plug != null) {
				Point p = _sel_plug.getCoords();
				Point l = new Point(p);
				if (p.x < 300) {
					l.x += 20;
				} else {
					l.x -= 20;
				}
				l.y -= 20;
				g2d.fillRect(l.x - 7, l.y - 8, 14, 8);
				g2d.fillOval(l.x - 6, l.y - 6, 12, 12);
				g2d.drawLine(p.x, p.y, l.x, l.y);
				g2d.setStroke(new BasicStroke((float)1.0,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				g2d.setColor(switchboard.jack_face);
				g2d.fillRect(l.x - 7, l.y - 106, 14, 42);
				g2d.fillRect(l.x - 7, l.y - 112, 14, 3);
				Polygon pp = new Polygon(tip_x, tip_y, 4);
				pp.translate(l.x - 7, l.y - 112);
				g2d.fillPolygon(pp);
				g2d.setColor(switchboard.jack_lt);
				g2d.drawLine(l.x - 5, l.y - 105, l.x - 5, l.y - 56);
				g2d.fillRect(l.x - 5, l.y - 112, 2, 2);
				g2d.setColor(switchboard.jack_dk);
				g2d.fillRect(l.x - 5, l.y - 109, 10, 3);
				g2d.drawLine(l.x + 5, l.y - 105, l.x + 5, l.y - 56);
				g2d.fillRect(l.x + 5, l.y - 112, 2, 2);
				g2d.setColor(switchboard.plug);
				g2d.fillRect(l.x - 15, l.y - 64, 30, 56);
				g2d.fillRect(l.x - 7, l.y - 103, 14, 3);
				g2d.setColor(switchboard.plug_lt);
				g2d.drawLine(l.x - 13, l.y - 63, l.x - 13, l.y - 10);
			}
		}

		public Kellogg_Cabinet_Overlay() {
			setOpaque(false);
		}
	}

	public Kellogg_Cabinet(Kellogg_Properties prop,
			Component top, Kellogg_Help help) {
		font_metrics = top.getFontMetrics(switchboard.font);
		font2_metrics = top.getFontMetrics(switchboard.font2);
		_top = top;
		_help = help;
		_prop = prop;
		_nc = _prop.getInteger("switchboard_circuits");
		_nl = _prop.getInteger("switchboard_lines");
		_na = _prop.getBoolean("switchboard_night_alarm");
		_tc = _prop.getBoolean("switchboard_toll_line");

		if (_nl < 5) {
			_nl = 5;
		}
		if (_nl > 5) {
			_nl = ((_nl + 9) / 10) * 10;
		}
		if (_nc <= 0) {
			_nc = (_nl * 40) / 100;
		}
		if (_nc > 8) {
			_nc = 8;
		}

		// establish cabinet geometry
		if (_nl < 20 && _nc <= 4) {
			// "narrow" cabinet
			lines_per_row = 5;
			max_circs = 4;
		} else {
			lines_per_row = 10;
			max_circs = 8;
		}

		if (_na) {
			_night_alarm = new Kellogg_NightAlarm();
			if (_nc == max_circs) --_nc;
		} else {
			_night_alarm = null;
		}

		if (_tc) {
		}

		_txt = new String();
		_text = new JTextArea();
		_text.setEditable(false);
		_text.setFocusable(false);
		_text.setFont(switchboard.font2);
		_text.setBackground(Color.white);
		_text.setLineWrap(true);
		_text.setWrapStyleWord(true);
		Caret caret = new javax.swing.plaf.basic.BasicTextUI.BasicCaret();
		caret.setBlinkRate(500);
		_text.setCaret(caret);
		_text.getCaret().setVisible(true);
		_text.setCaretColor(switchboard.cabinet);
		_scroll = new JScrollPane(_text);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		_scroll.setFocusable(false);
		_scroll.setVisible(false);

		cab_width = max_circs * Kellogg_Circuit.obj_width;
		int rows = (_nl + lines_per_row - 1) / lines_per_row;
		_sel_plug = null;
		_conn_plugs = new Kellogg_Plug[_nc * 2];
		_conn_lines = new Kellogg_Line[_nc * 2];
		//Dimension dim = new Dimension(line_width * lines_per_row, 1);

		_lines = new Kellogg_Line[_nl];
		_nlines = _nl;

		JPanel base = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		base.setLayout(gridbag);
		base.setOpaque(false);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.WEST;

		JPanel upper = new JPanel();
		GridBagLayout ugb = new GridBagLayout();
		upper.setLayout(ugb);
		upper.setOpaque(true);
		upper.setBackground(switchboard.cabinet);
		upper.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					switchboard.well_lt, switchboard.well_dk));

		JPanel lp = makeLinePanel(_nl);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		ugb.setConstraints(lp, s);
		upper.add(lp);

		JPanel circs = new JPanel();
		JPanel ro = makeRODropCircPanel(circs, _nc, top);
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		ugb.setConstraints(ro, s);
		upper.add(ro);

		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 2;
		s.gridheight = 1;
		gridbag.setConstraints(upper, s);
		base.add(upper);

		circs.setOpaque(true);
		circs.setBackground(switchboard.cabinet);
		circs.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					switchboard.well_lt, switchboard.well_dk));
		circs.setPreferredSize(new Dimension(cab_width + 2 * border_size,
					Kellogg_Circuit.obj_height + 2 * border_size));
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 2;
		s.gridheight = 1;
		gridbag.setConstraints(circs, s);
		base.add(circs);

		Kellogg_Magneto mag = new Kellogg_Magneto(this);
		s.gridx = 1;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(mag, s);
		base.add(mag);

		Dimension dim = new Dimension(cab_width + 2 * border_size,
					rows * Kellogg_LineWithDrop.obj_height +
					2 * Kellogg_Drop.obj_height +
					Kellogg_Circuit.obj_height +
					Kellogg_Magneto.obj_height +
					4 * border_size);
		base.setPreferredSize(dim);
		base.setBounds(0, 0, dim.width, dim.height);

		Kellogg_Cabinet_Overlay ovr = new Kellogg_Cabinet_Overlay();
		ovr.setPreferredSize(dim);
		ovr.setBounds(0, 0, dim.width, dim.height);

		add(base, new Integer(10));
		add(ovr, new Integer(20));

		_scroll.setPreferredSize(new Dimension(dim.width, text_height));
		_scroll.setBounds(0, dim.height, dim.width, text_height);
		add(_scroll, new Integer(30));

		setPreferredSize(dim);
		setOpaque(false);
		setForeground(Color.red);

		top.addKeyListener(this);

		_listen_h = 0;
		_no_listen_h = 0;
		_listening = 0;

		_nc_t = new JTextField();
		_nc_t.setPreferredSize(new Dimension(30,20));
		_nc_f = new JPanel();
		_nc_f.add(new JLabel("Num Circuits:"));
		_nc_f.add(_nc_t);

		_nl_t = new JTextField();
		_nl_t.setPreferredSize(new Dimension(30,20));
		_nl_f = new JPanel();
		_nl_f.add(new JLabel("Num Lines:"));
		_nl_f.add(_nl_t);

		_na_t = new Checkbox();
		_na_f = new JPanel();
		_na_f.add(new JLabel("Night Alarm"));
		_na_f.add(_na_t);

		_tc_t = new Checkbox();
		_tc_f = new JPanel();
		_tc_f.add(new JLabel("Toll Line"));
		_tc_f.add(_tc_t);

		String h = _prop.getProperty("switchboard_host");
		if (h.length() == 0) h = ":31100";
		_hosts = h.split("[ \\t\\n]+");
		_host_t = new JTextArea();
		_host_t.setPreferredSize(new Dimension(200, _hosts.length * 20));
		_host_f = new JPanel();
		_host_f.add(new JLabel("Hosts:"));
		_host_f.add(_host_t);

		int x;
		Exception ee = null;
		for (x = 0; x < _hosts.length; ++x) {
			String[] hp = _hosts[x].split(":");
			try {
				int p = Integer.valueOf(hp[1]);
				InetAddress ia;
				if (hp[0].length() == 0 || hp[0].equals("localhost")) {
					ia = InetAddress.getByName(null);
				} else {
					ia = InetAddress.getByName(hp[0]);
				}
				_ss = new ServerSocket(p, 1, ia);
				break;
			} catch(Exception e) {
				_ss = null;
				ee = e;
			}
		}
		if (_ss == null) {
			switchboard.fatal("ServerSocket", ee.getMessage());
		}
		Thread t = new Thread(this);
		t.start();
	}

	private JPanel makeLinePanel(int num) {
		JPanel panel = new JPanel();
		int x;
		int max_row = num / lines_per_row;
		GridBagLayout gridbag = new GridBagLayout();
		panel.setLayout(gridbag);
		panel.setOpaque(false);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		Kellogg_LineWithDrop li;
		for (x = 0; x < num; ++x) {
			li = new Kellogg_LineWithDrop(panel, x + 1, this);
			s.gridx = x % lines_per_row;
			s.gridy = max_row - (x / lines_per_row);
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(li, s);
			panel.add(li);
			_lines[x] = li.getLine();
		}
		return panel;
	}

	private JPanel makeRODropCircPanel(JPanel circs, int num_circs,
						Component top) {
		JPanel panel = new JPanel();
		GridBagLayout pgb = new GridBagLayout();
		GridBagLayout cgb = new GridBagLayout();
		panel.setLayout(pgb);
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(num_circs * Kellogg_Circuit.obj_width,
							2 * Kellogg_Drop.obj_height));
		circs.setLayout(cgb);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.WEST;
		int x;
		for (x = 0; x < num_circs; ++x) {
			Kellogg_Drop drp1 = new Kellogg_Drop(this, x + 1,
						Kellogg_Circuit.obj_width, false);
			s.gridx = x;
			s.gridy = 0;
			s.gridwidth = 1;
			s.gridheight = 1;
			pgb.setConstraints(drp1, s);
			panel.add(drp1);
			Kellogg_Drop drp2 = new Kellogg_Drop(this, x + 1,
						Kellogg_Circuit.obj_width, false);
			s.gridx = x;
			s.gridy = 1;
			s.gridwidth = 1;
			s.gridheight = 1;
			pgb.setConstraints(drp2, s);
			panel.add(drp2);

			Kellogg_Circuit cir = new Kellogg_Circuit(circs, x + 1, this,
						drp1, drp2, top);
			s.gridx = x;
			s.gridy = 0;
			s.gridwidth = 1;
			s.gridheight = 1;
			cgb.setConstraints(cir, s);
			circs.add(cir);
		}
		Dimension gap;
		if (_night_alarm != null) {
			gap = new Dimension(
				cab_width - (num_circs + 1) * Kellogg_Circuit.obj_width,
				Kellogg_Circuit.obj_height);
			s.gridx = max_circs - 1;
			s.gridy = 0;
			s.gridwidth = 1;
			s.gridheight = 1;
			cgb.setConstraints(_night_alarm, s);
			circs.add(_night_alarm);
		} else {
			gap = new Dimension(
				cab_width - num_circs * Kellogg_Circuit.obj_width,
				Kellogg_Circuit.obj_height);
		}
		JPanel pan = new JPanel();
		pan.setPreferredSize(gap);
		pan.setOpaque(false);
		s.gridx = x;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		cgb.setConstraints(pan, s);
		circs.add(pan);
		return panel;
	}

	public void connectLine(Kellogg_Line line) {
		if (_sel_plug == null) return;
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_plugs[i] == _sel_plug) return;
			if (_conn_lines[i] == line) return;
			if (_conn_plugs[i] == null) {
				_conn_plugs[i] = _sel_plug;
				_conn_lines[i] = line;
				_conn_plugs[i].setPlugged(line);
				_conn_lines[i].setPlugged(_sel_plug);
				_sel_plug = null;
				repaint();
				return;
			}
		}
	}

	public void disconnect(Kellogg_Plug plug) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_plugs[i] == plug) {
				_conn_plugs[i].setPlugged(null);
				_conn_lines[i].setPlugged(null);
				_conn_plugs[i] = null;
				_conn_lines[i] = null;
				repaint();
				return;
			}
		}
	}

	public void disconnect(Kellogg_Line line) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_lines[i] == line) {
				_conn_plugs[i].setPlugged(null);
				_conn_lines[i].setPlugged(null);
				_conn_plugs[i] = null;
				_conn_lines[i] = null;
				repaint();
				return;
			}
		}
	}

	public void alarmDrop(boolean coded, boolean on) {
		if (_night_alarm != null) _night_alarm.alarmDrop(coded, on);
	}

	// only called if Coded Drop...
	public void alarmRing(boolean on) {
		if (_night_alarm != null) _night_alarm.alarmRing(on);
	}

	private void goListening(boolean on) {
		Dimension dim = _top.getSize();
		if (_listen_h == 0) {
			_no_listen_h = dim.height;
			_listen_h = _no_listen_h + text_height;
		}
		if (on) {
			if (dim.height < _listen_h) dim.height = _listen_h;
		} else {
			_text.setText("");
			if (dim.height == _listen_h) dim.height = _no_listen_h;
		}
		_top.setSize(dim);
		_scroll.setVisible(on);
		repaint();
	}

	public void listener(JComponent sw, boolean on) {
		if (on) {
			if (_listening == 0) goListening(true);
			++_listening;
		} else {
			--_listening;
			if (_listening == 0) goListening(false);
		}
	}

	// called by a Kellogg_Line when subscriber "says" something...
	public void post(Kellogg_Line line, String s) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_lines[i] == line) {
				if (_conn_plugs[i].isListening()) {
					_text.append(line.getId() + ">> " + s + "\n");
					_text.setCaretPosition(_text.getText().length());
				}
				_conn_plugs[i].postUp(s);
				return;
			}
		}
	}

	// called by a Kellogg_Plug when other subscriber "says" something...
	public void post(Kellogg_Plug plug, String s) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_plugs[i] == plug) {
//				// listening already handled, on the way up...
//				if (_conn_plugs[i].isListening()) {
//					_text.append(line.getId() + ">> " + s + "\n");
//					_text.setCaretPosition(_text.getText().length());
//				}
				_conn_lines[i].speak(s + "\n");
				return;
			}
		}
	}

	public void selectPlug(Kellogg_Plug plug) {
		if (_sel_plug != null) {
			_sel_plug.setSelect(false);
			if (_sel_plug == plug) {
				_sel_plug = null;
				repaint();
				return;
			}
		}
		_sel_plug = plug;
		_sel_plug.setSelect(true);
		repaint();
	}

	public void ring(boolean on) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_lines[i] != null) {
				_conn_lines[i].ring(on);
			}
		}
	}

	private void type(char c) {
		if (c == KeyEvent.VK_BACK_SPACE) {
			int p = _text.getCaretPosition();
			_text.replaceRange("", p - 1, p);
			_txt = _txt.substring(0, _txt.length() - 1);
			return;
		}
		String s = Character.toString(c);
		_text.append(s);
		_txt += s;
		if (c != KeyEvent.VK_ENTER) return;
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_lines[i] != null) {
				if (_conn_plugs[i].isListening()) {
					_conn_lines[i].speak(_txt);
				}
			}
		}
		_txt = new String();
	}

	public void actionPerformed(ActionEvent e) {
		if (!(e.getSource() instanceof JMenuItem)) {
			return;
		}
		JMenuItem m = (JMenuItem)e.getSource();
		if (m.getMnemonic() == KeyEvent.VK_H) {
			_help.toggle();
			return;
		}
		if (m.getMnemonic() == KeyEvent.VK_A) {
			_help.showAbout();
			return;
		}
		if (m.getMnemonic() == KeyEvent.VK_D) {
			int x;
			String dir = "<HTML>\n" +
					"<TABLE><TR><TH>Line</TH><TH>Subscriber</TH></TR><BR>\n";
			for (x = 0; x < _nlines; ++x) {
				if (_lines[x] == null) continue;
				if (!_lines[x].isSubscribed()) continue;

				String n = _lines[x].getName();
				if (_lines[x].getType() == switchboard.SWITCHBOARD) {
					n = "(switchboard) " + n;
				}
				dir += "<TR><TH>" + _lines[x].getId() +
					"</TH><TH>" + n + "</TH></TR>\n";
			}
			dir += "</TABLE></HTML>\n";
			// need to offer more-permanant display...
			JOptionPane.showMessageDialog(_top,
				new JLabel(dir), "Telephone Subscriber Directory",
				JOptionPane.PLAIN_MESSAGE);
			return;
		}
		if (m.getMnemonic() == KeyEvent.VK_U) {
			_nl_t.setText(_prop.getProperty("switchboard_lines"));
			_nc_t.setText(_prop.getProperty("switchboard_circuits"));
			_na_t.setState(_prop.getBoolean("switchboard_night_alarm"));
			_tc_t.setState(_prop.getBoolean("switchboard_toll_line"));
			String h = _prop.getProperty("switchboard_host");
			h = h.replaceAll("[ \\t\\n]+", "\n");
			_host_t.setText(h);
			Object[] dia = { _nl_f, _nc_f, _na_f, _tc_f, _host_f };
			int ret = JOptionPane.showConfirmDialog(this, dia,
				"Set Switchboard Parameters",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return;
			}
			// TBD: change parameters and restart?
			// TBD: do validation?
			String hs = _host_t.getText();
			hs = hs.replaceAll("[ \\t\\n]+", " ");
			_prop.setProperty("switchboard_lines", _nl_t.getText());
			_prop.setProperty("switchboard_circuits", _nc_t.getText());
			_prop.setProperty("switchboard_night_alarm", Boolean.toString(_na_t.getState()));
			_prop.setProperty("switchboard_toll_line", Boolean.toString(_tc_t.getState()));
			_prop.setProperty("switchboard_host", hs);
			_prop.save();
			_hosts = hs.split("[ \\t\\n]+");
			return;
		}
	}

	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		type(c);
	}
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}

	public void run() {
		int x;
		Socket s;
		while (true) {
			try {
				s = _ss.accept();
			} catch(IOException e) {
				break;
			}
			for (x = 0; x < _nlines; ++x) {
				if (_lines[x] == null) continue;
				if (_lines[x].subscribe(s)) break;
			}
			if (x >= _nlines) {
				try { s.close(); } catch(IOException e) { }
			}
		}
		try { _ss.close(); } catch(IOException e) { }
	}
}

class Kellogg_Magneto extends JPanel
	implements MouseListener, ActionListener
{
	static final long serialVersionUID = 311000000011L;
	public static final int obj_width = 100;
	public static final int obj_height = 50;

	private Kellogg_Cabinet _cab;
	private int _angle;
	private int _pos;
	private javax.swing.Timer _timer;

	private static final double[] _sin = {
		-1.0,    -0.9809, -0.9239, -0.8315,
		-0.7071, -0.5556, -0.3827, -0.1951,
		 0.0,     0.1951,  0.3827,  0.5556,
		 0.7071,  0.8315,  0.9239,  0.9808,
		 1.0,     0.9808,  0.9239,  0.8315,
		 0.7071,  0.5556,  0.3827,  0.1951,
		 0.0,    -0.1951, -0.3827, -0.5556,
		-0.7071, -0.8315, -0.9239, -0.9808
        };
//	private static final double[] _cos = {
//		 0.0,     0.1951,  0.3827,  0.5556,
//		 0.7071,  0.8315,  0.9239,  0.9808,
//		 1.0,     0.9808,  0.9239,  0.8315,
//		 0.7071,  0.5556,  0.3827,  0.1951,
//		 0.0,    -0.1951, -0.3827, -0.5556,
//		-0.7071, -0.8315, -0.9239, -0.9808,
//		-1.0,    -0.9808, -0.9239, -0.8315,
//		-0.7071, -0.5556, -0.3827, -0.1951
//	};

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.plug);
		g.fillRect(_pos - 5, 0, 10, 20);
		int x = _pos + (int)Math.round(_sin[_angle] * 40);
		int x1, x2;
		if (x < _pos) {
			x2 = _pos + 5;
			x1 = x - 5;
		} else {
			x2 = x + 5;
			x1 = _pos - 5;
		}
		g.fillRoundRect(x - 5, 25, 10, 20, 3, 3);
		g.fillRect(x1, 20, x2 - x1, 5);
		g.setColor(switchboard.plug_lt);
		g.fillArc(x - 4, 26, 6, 6, 90, 90);
		g.drawLine(x - 4, 28, x - 4, 42);
		g.drawLine(_pos - 4, 2, _pos - 4, 18);
	}

	public Kellogg_Magneto(Kellogg_Cabinet cab) {
		_cab = cab;
		_pos = 50;
		setOpaque(false);
		//setOpaque(true);
		//setBackground(Color.gray);
		setPreferredSize(new Dimension(100, 50));
		addMouseListener(this);
		setFocusable(false);
		_angle = 0;
		_timer = new Timer(50, this);
	}

	private void crank() {
		_angle = (_angle + 2) & 0x1e;
		repaint();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _timer) {
			crank();
		}
	}

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		crank();
		_timer.start();
		_cab.ring(true);
	}
	public void mouseReleased(MouseEvent e) {
		_timer.stop();
		_cab.ring(false);
	}
}

class Kellogg_Drop extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000003L;
	public static final int obj_width = 60;
	public static final int obj_height = 60;

	static final int[] shutter_x = { 40, 50, 50, 10, 10, 20, 40 };
	static final int[] shutter_y = { 10, 20, 50, 50, 20, 10, 10 };
	static final int[] label_r = { 12, 25, 36, 20 };

	static final int[] shutter_dn_x = { 40, 52, 50, 10,  8, 20, 40 };
	static final int[] shutter_dn_y = { 38, 41, 50, 50, 41, 38, 38 };
	static final int[] label_dn_x = { 50, 48, 12, 10 };
	static final int[] label_dn_y = { 43, 48, 48, 43 };

	private int _num;
	private String _tag;
	private int _tag_x;
	private boolean _dropped;
	private boolean _coded;
	private int[] _shutter_x;
	private int[] _shutter_dn_x;
	private int[] _label;
	private int[] _label_dn_x;
	private Kellogg_Cabinet _cab;

	public void paint(Graphics g) {
		super.paint(g);
		if (_dropped) {
			g.setColor(switchboard.jack_bev);
			g.fillPolygon(_shutter_x, shutter_y, 6); // void behind shutter...

			g.setColor(switchboard.edge);
			g.fillPolygon(_shutter_dn_x, shutter_dn_y, 6);
			g.setColor(switchboard.edge_dk);
			g.drawPolyline(_shutter_dn_x, shutter_dn_y, 2);
			g.setColor(switchboard.edge_lt);
			g.drawPolyline(Arrays.copyOfRange(_shutter_dn_x, 4, 7),
					Arrays.copyOfRange(shutter_dn_y, 4, 7), 3);
			g.setColor(switchboard.edge_label);
			g.fillPolygon(_label_dn_x, label_dn_y, 4);
		} else {
			g.setColor(switchboard.drop);
			g.fillPolygon(_shutter_x, shutter_y, 6);
			g.setColor(switchboard.drop_dk);
			g.drawPolyline(_shutter_x, shutter_y, 4);
			g.setColor(switchboard.drop_lt);
			g.drawPolyline(Arrays.copyOfRange(_shutter_x, 3, 7),
					Arrays.copyOfRange(shutter_y, 3, 7), 4);
			g.setColor(switchboard.drop_label);
			g.fillRect(_label[0], _label[1], _label[2], _label[3]);
			g.setColor(switchboard.drop_text);
			g.drawString(_tag, _tag_x, 42);
		}
	}

	public Kellogg_Drop(Kellogg_Cabinet cab, int num, int width, boolean coded) {
		_num = num;
		_cab = cab;
		_coded = coded;
		_tag = Integer.toString(_num);
		int w = Kellogg_Cabinet.font_metrics.stringWidth(_tag);
		int off = (width / 2) - (60 / 2);
		_tag_x = (width / 2) - (w / 2);
		_shutter_x = Arrays.copyOf(shutter_x, 7);
		_shutter_dn_x = Arrays.copyOf(shutter_dn_x, 7);
		_label_dn_x = Arrays.copyOf(label_dn_x, 7);
		_label = Arrays.copyOf(label_r, 4);
		if (off > 0) {
			int i;
			for (i = 0; i < _shutter_x.length; ++i) {
				_shutter_x[i] += off;
			}
			for (i = 0; i < _shutter_dn_x.length; ++i) {
				_shutter_dn_x[i] += off;
			}
			for (i = 0; i < _label_dn_x.length; ++i) {
				_label_dn_x[i] += off;
			}
			_label[0] += off;
		}
		_dropped = false;
		setPreferredSize(new Dimension(width, 60));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		// setDropped(false);
		setDropped(!_dropped); // temp: bring-up
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public void setDropped(boolean drop) {
		if (drop != _dropped) {
			_dropped = drop;
			repaint();
			_cab.alarmDrop(_coded, _dropped);
		}
	}

	public boolean isDropped() {
		return _dropped;
	}

	public void alarmRing(boolean on) {
		if (on) setDropped(on);
		if (_coded) _cab.alarmRing(on);
	}
}

class Kellogg_Line extends JPanel
	implements MouseListener, Runnable
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;
	public static final int obj_width = 60;
	public static final int obj_height = 40;

	static final int[] hex_top_x = { 20, 40, 46, 14, 10, 20 };
	static final int[] hex_top_y = {  0,  0,  9, 21, 15,  0 };

	static final int[] hex_bot_x = { 46, 50, 40, 20, 14, 46 };
	static final int[] hex_bot_y = {  9, 15, 31, 31, 21,  9 };

	static final Point _center = new Point(30, 15);

	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Cabinet _cab;
	private Kellogg_Plug _conn_plug;
	private int _num;

	private Socket _subscriber;
	private Thread _thr;
	private BufferedReader _in;
	private OutputStream _out;
	private String _name;
	private int _type;

	public void paint(Graphics g) {
		super.paint(g);
		if (_conn_plug != null) {
			g.setColor(switchboard.plug);
			g.fillOval(14,  0, 32, 30);
			g.setColor(switchboard.plug_lt);
			g.drawArc(16,  2, 28, 26, 110, 50);
			g.setColor(switchboard.cord);
			g.fillOval(23,  8, 14, 14);
		} else {
			g.setColor(switchboard.jack_lt);
			g.fillPolygon(hex_top_x, hex_top_y, 5);
			g.setColor(switchboard.jack_dk);
			g.fillPolygon(hex_bot_x, hex_bot_y, 5);
			g.setColor(switchboard.jack_face);
			g.fillOval(14,  0, 32, 30);
			g.setColor(switchboard.jack_lt);
			g.fillArc(20,  5, 20, 20, -155, 180);
			g.setColor(switchboard.jack_dk);
			g.fillArc(20,  5, 20, 20, 25, 180);
			g.setColor(switchboard.jack_hole);
			if (_conn_plug != null) g.setColor(Color.yellow);
			g.fillOval(22,  7, 16, 16);
		}
	}

	public Kellogg_Line(JPanel parent, Kellogg_Cabinet cab,
			int num, Kellogg_Drop drop) {
		_parent = parent;
		_cab = cab;
		_drop = drop;
		_conn_plug = null;
		_num = num;
		_name = "Unknown";
		_type = switchboard.UNKNOWN;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (_conn_plug != null) {
			_cab.disconnect(this);
		} else {
			_cab.connectLine(this); // or try at least...
		}
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public int getType() { return _type; }

	public Point getCoords() {
		Point p = _parent.getLocation();
		Point m = getLocation();
		p.x += m.x;
		p.y += m.y;
		p.x += _center.x;
		p.y += _center.y;
		return p;
	}

	public void setPlugged(Kellogg_Plug plug) {
		if (plug != _conn_plug) {
			_conn_plug = plug;
			if (plug != null) {
				_drop.setDropped(false);
			}
			repaint();
		}
	}

	public void ring(boolean on) {
		if (_conn_plug != null && _conn_plug.isRingable()) {
			if (_subscriber != null) {
				// TBD: transmit RING to remote phone...
				try {
					if (on) _out.write("%RING\n".getBytes());
					else _out.write("%RINGOFF\n".getBytes());
				} catch(IOException e) { }
			} else {
				System.err.println("remote "+_num+": RING "+on);
			}
		}
	}

	public boolean isSubscribed() { return (_subscriber != null); }

	public boolean subscribe(Socket s) {
		if (_subscriber != null) return false;
		_subscriber = s;
		try {
			_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			_out = s.getOutputStream();
			String t = new String("%TYPE=switchboard\n");
			_out.write(t.getBytes());
			t = new String("%NAME=" + Integer.toString(_num) + "\n");
			_out.write(t.getBytes());
		} catch(IOException e) {
		}
		_thr = new Thread(this);
		_thr.start();
		return true;
	}

	public int getId() { return _num; }
	public String getName() { return _name; }

	public void speak(String s) {
		try {
			_out.write(s.getBytes());
		} catch(IOException e) {
		}
	}

	private boolean doCommand(String s) {
		if (s.equals("%RING")) {
			if (_conn_plug != null) {
				_conn_plug.ring(true);
			} else {
				_drop.alarmRing(true); // also sets Drop
			}
		} else if (s.equals("%RINGOFF")) {
			if (_conn_plug != null) {
				_conn_plug.ring(false);
			} else {
				_drop.alarmRing(false);
			}
		} else if (s.startsWith("%TYPE=")) {
			s = s.substring(6);
			if (s.startsWith("tele")) {
				_type = switchboard.TELEPHONE;
			} else if (s.startsWith("swit")) {
				_type = switchboard.SWITCHBOARD;
				// possibly move to different line,
				// if using toll lines...
			} else {
				// drop connection and free line...
				return false;
			}
		} else if (s.startsWith("%NAME=")) {
			InetSocketAddress sa = (InetSocketAddress)_subscriber.getRemoteSocketAddress();
			_name = s.substring(6) + "@" + sa.getHostName();
		}
		return true;
	}

	public void run() {
		while (true) {
			String s = null;
			try {
				s = _in.readLine();
			} catch(Exception e) { }
			if (s == null) break;
			if (s.startsWith("%")) {
				if (!doCommand(s)) break;
			} else {
				_cab.post(this, s);
			}
		}
		try {
			_in.close();
			_out.close();
			_subscriber.close();
		} catch(IOException e) { }
		_subscriber = null;
		if (_conn_plug != null) {
			_conn_plug.ring(true); // trigger the drop...
			_conn_plug.ring(false);
		}
	}
}

class Kellogg_LineWithDrop extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000004L;
	public static final int obj_width = 60;
	public static final int obj_height =
			Kellogg_Line.obj_height + Kellogg_Drop.obj_height;

	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Line _line;

	public Kellogg_LineWithDrop(JPanel parent, int num,
			Kellogg_Cabinet cab) {
		_parent = parent;
		_drop = new Kellogg_Drop(cab, num, 60, true);
		_line = new Kellogg_Line(this, cab, num, _drop);

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_drop, s);
		add(_drop);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_line, s);
		add(_line);

		setPreferredSize(new Dimension(60, 100));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}

	public Kellogg_Line getLine() { return _line; }

	public Point getLocation() {
		Point m = super.getLocation();
		Point p = _parent.getLocation();
		p.x += m.x;
		p.y += m.y;
		return p;
	}
}

class Kellogg_Plug extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000005L;
	public static final int obj_width = 75;
	public static final int obj_height = 55;
	static final Point _center = new Point(40, 33);

	private JPanel _parent;
	private Kellogg_Drop _co_drop;
	private boolean _select;
	private Kellogg_Cabinet _cab;
	private Kellogg_Line _conn_line;
	private int _ring_state;
	private Kellogg_Circuit _circ;
	private String _tag;
	private int _tag_x;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.drawString(_tag, _tag_x, 15);
		if (_select || _conn_line != null) {
			g.setColor(switchboard.well_lt);
			g.fillArc(25, 18, 30, 30, -135, 180);
			g.setColor(switchboard.well_dk);
			g.fillArc(25, 18, 30, 30, 45, 180);
			g.setColor(switchboard.well);
			g.fillOval(27, 20, 26, 26);
			g.setColor(switchboard.cord);
			g.fillOval(33, 26, 14, 14);
		} else {
			g.setColor(switchboard.plug);
			g.fillOval(25, 18, 30, 30);
			g.setColor(switchboard.ring);
			g.fillOval(31, 24, 18, 18);
			g.setColor(switchboard.tip_dk);
			g.fillOval(33, 26, 14, 14);
			g.setColor(switchboard.tip);
			g.fillArc(33, 26, 14, 14, 45, 180);
			g.setColor(switchboard.tip_lt);
			g.fillArc(33, 26, 14, 14, 112, 45);
			g.fillOval(38, 31, 4, 4);
		}
	}

	public Kellogg_Plug(JPanel parent, Kellogg_Cabinet cab,
			Kellogg_Drop drop, int ring, String tag) {
		_co_drop = drop;
		_parent = parent;
		_circ = (Kellogg_Circuit)_parent; // for now, the same...
		_cab = cab;
		_conn_line = null;
		_ring_state = ring;

		_tag = tag;
		int w = Kellogg_Cabinet.font2_metrics.stringWidth(_tag);
		_tag_x = _center.x - (w / 2);
		setFont(switchboard.font2);
		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setBackground(Color.blue);
		addMouseListener(this);
		_select = false;
	}

	public void mouseClicked(MouseEvent e) {
		if (_conn_line == null) {
			_cab.selectPlug(this);
		}
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public Point getCoords() {
		Point p = _parent.getLocation();
		Point m = getLocation();
		p.x += m.x;
		p.y += m.y;
		p.x += _center.x;
		p.y += _center.y;
		return p;
	}

	public void setPlugged(Kellogg_Line line) {
		if (_conn_line != line) {
			_conn_line = line;
			_select = false;
			repaint();
		}
	}

	public void setSelect(boolean sel) {
		if (sel != _select) {
			_select = sel;
			repaint();
		}
	}

	public boolean isListening() {
		return (_circ.listenSw());
	}

	public boolean isRingable() {
		return (_circ.ringSw() == _ring_state);
	}

	public void ring(boolean on) {
		if (on) _co_drop.setDropped(true);
	}

	public void postUp(String s) {
		_circ.post(this, s);
	}

	public void postDown(String s) {
		_cab.post(this, s);
	}
}

class Kellogg_RingSw extends JPanel
	implements MouseListener, KeyListener
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000007L;
	public static final int obj_width = 75;
	public static final int obj_height = 66;

	public static final int PUSH = 1;
	public static final int OFF = 0;
	public static final int PULL = -1;

	private static final String _tag1 = "Ring-back";
	private static final int _tag1_x = 10;
	private static final String _tag2 = "Ring";
	private static final int _tag2_x = 25;

	private int _state;
	private Kellogg_Cabinet _cab;

	public void paint(Graphics g) {
		super.paint(g);
		g.drawString(_tag1, _tag1_x, 9);
		g.drawString(_tag2, _tag2_x, 63);
		if (_state == PUSH) {
			// "Up" or "Pushed"... Ring Answer
			g.setColor(switchboard.drop_label);
			g.fillRect(35, 22, 10, 10);
			g.setColor(switchboard.jack_dk);
			g.fillRect(35, 32, 10, 20);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 12, 20, 15);
			g.fillOval(30, 2, 20, 15);
			g.fillRect(30, 10, 20, 10);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 4, 16, 12, 110, 50);
			g.drawLine(32, 10, 32, 21);
		} else if (_state == PULL) {
			// "Down" or "Pulled" - Ring Call
			g.setColor(switchboard.jack_lt);
			g.fillRect(35, 12, 10, 20);
			g.setColor(switchboard.jack_face);
			g.fillRect(35, 32, 10, 10);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 37, 20, 15);
			g.fillOval(30, 47, 20, 15);
			g.fillRect(30, 45, 20, 10);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 39, 16, 12, 135, 25);
			g.drawLine(32, 45, 32, 56);
		} else { // OFF
			g.setColor(switchboard.jack_lt);
			g.fillRect(35, 12, 10, 20);
			g.setColor(switchboard.jack_dk);
			g.fillRect(35, 32, 10, 20);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 22, 20, 20);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 24, 16, 16, 110, 50);
		}
	}

	public Kellogg_RingSw(Component top, Kellogg_Cabinet cab) {
		_cab = cab;
		_state = OFF;
		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font2);
		addMouseListener(this);
		top.addKeyListener(this);
	}

	public void setState(int b) {
		int n = _state;
		if (b == MouseEvent.BUTTON1) { // up... PUSH
			n += 1;
			if (n > PUSH) n = PUSH;
		} else if (b == MouseEvent.BUTTON3) {
			n -= 1;
			if (n < PULL) n = PULL;
		}
		if (_state != n) {
			_state = n;
			repaint();
		}
	}

	public int getState() { return _state; }

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		int b = e.getButton(); // 1=Left, 3=Right
		setState(b);
	}
	public void mouseReleased(MouseEvent e) {
		boolean stick = ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0);
		if (_state != 0 && !stick) {
			_state = 0;
			repaint();
		}
	}

	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) {
		if (_state != 0 && e.getKeyCode() == KeyEvent.VK_SHIFT) {
			// this must also stop the ringing... but don't change
			// state until after...
			_cab.ring(false);
			_state = 0;
			repaint();
		}
	}
}

class Kellogg_StSpSw extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000006L;
	public static final int obj_width = 75;
	public static final int obj_height = 64;

	private boolean _state;
	private Kellogg_SwListener _cab;
	private String _tag;
	private int _tag_x;

	public void paint(Graphics g) {
		super.paint(g);
		g.drawString(_tag, _tag_x, 9);
		if (_state) {
			g.setColor(switchboard.drop_label);
			g.fillRect(35, 22, 10, 10);
			g.setColor(switchboard.jack_dk);
			g.fillRect(35, 32, 10, 20);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 12, 20, 15);
			g.fillOval(30, 2, 20, 15);
			g.fillRect(30, 10, 20, 10);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 4, 16, 12, 110, 50);
			g.drawLine(32, 10, 32, 21);
		} else {
			g.setColor(switchboard.jack_lt);
			g.fillRect(35, 12, 10, 20);
			g.setColor(switchboard.jack_dk);
			g.fillRect(35, 32, 10, 20);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 22, 20, 20);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 24, 16, 16, 110, 50);
		}
	}

	public Kellogg_StSpSw(Kellogg_SwListener cab, String tag) {
		_cab = cab;
		_tag = tag;
		int w = Kellogg_Cabinet.font2_metrics.stringWidth(_tag);
		_tag_x = (obj_width - w) / 2;
		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font2);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		setState(!_state);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public boolean getState() { return _state; }

	public void setState(boolean on) {
		if (_state != on) {
			_state = on;
			if (_cab != null) _cab.listener(this, _state);
			repaint();
		}
	}
}

class Kellogg_Circuit extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000008L;
	public static final int obj_width = 75;
	public static final int obj_height = 10 + 10 +
		2 * Kellogg_Plug.obj_height +
		Kellogg_RingSw.obj_height +
		Kellogg_StSpSw.obj_height;

	private JPanel _parent;
	private Kellogg_StSpSw _listen;
	private Kellogg_RingSw _ring;
	private Kellogg_Plug _call;
	private Kellogg_Plug _ans;
	private Kellogg_Drop _drop_call;
	private Kellogg_Drop _drop_ans;
	private int _num;

	// R.O.Drop  [upper](originator attn signal)
	// R.O.Drop  [lower](target attn signal)
	// Answer    [far]  (operator answers originator)
	// Call      [near] (operator calls target)
	// Ring-back [push] (operator rings originator)
	// Ring      [pull] (operator rings target)
	// Listen    [push] (operator taps-in to circuit)
	public Kellogg_Circuit(JPanel parent, int num, Kellogg_Cabinet cab,
			Kellogg_Drop drp_up, Kellogg_Drop drp_lo,
			Component top) {
		_num = num;
		_parent = parent;
		_drop_ans = drp_up;
		_drop_call = drp_lo;
		_listen = new Kellogg_StSpSw(cab, "Listen");
		_ring = new Kellogg_RingSw(top, cab);
		_call = new Kellogg_Plug(this, cab, _drop_call,
						Kellogg_RingSw.PULL, "Call");
		_ans = new Kellogg_Plug(this, cab, _drop_ans,
						Kellogg_RingSw.PUSH, "Answer");

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ans, s);
		add(_ans);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_call, s);
		add(_call);

		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(obj_width, 10));
		pan.setOpaque(false);
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ring, s);
		add(_ring);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(obj_width, 10));
		s.gridx = 0;
		s.gridy = 4;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);
		s.gridx = 0;
		s.gridy = 5;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_listen, s);
		add(_listen);

		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}

	public Point getLocation() {
		Point p = _parent.getLocation();
		Point m = super.getLocation();
		p.x += m.x;
		p.y += m.y;
		return p;
	}

	public int ringSw() {
		return _ring.getState();
	}

	public boolean listenSw() {
		return _listen.getState();
	}

	public int getId() {
		return _num;
	}

	public void post(Kellogg_Plug plug, String s) {
		if (plug == _call) _ans.postDown(s);
		else if (plug == _ans) _call.postDown(s);
	}
}

class Kellogg_NightAlarm extends JPanel
	implements Kellogg_SwListener
{
	final String ident = "$Id: switchboard.java,v 1.54 2012/02/27 00:39:01 drmiller Exp $";
	static final long serialVersionUID = 311000000048L;
	public static final int obj_width = Kellogg_Circuit.obj_width;
	public static final int obj_height = Kellogg_Circuit.obj_height;

	private boolean _alarm;
	private Clip _ringer;

	private Kellogg_StSpSw _na_sw;
	private Kellogg_StSpSw _cd_sw;

	private class Alarmer {
		private int _count;

		public Alarmer() {
			_count = 0;
		}

		public void reset() { _count = 0; }

		public boolean count() { return (_count != 0); }

		public void alarmer(boolean on) {
			if (on) {
				++_count;
			} else {
				--_count;
			}
		}
	}

	private Alarmer _const_drop;
	private Alarmer _coded_drop;
	private Alarmer _coded_ring;

	public void paint(Graphics g) {
		super.paint(g);
//		String s = Integer.toString(_alarming);
//		g.drawString(s, 10, 90);
		if (_alarm) {
			g.setColor(Color.red);
			g.fillOval(10, 10, 55, 55);
		}
	}

	public Kellogg_NightAlarm() {
		_alarm = false;
		_const_drop = new Alarmer();
		_coded_drop = new Alarmer();
		_coded_ring = new Alarmer();

		_na_sw = new Kellogg_StSpSw(this, "N.A.");
		_cd_sw = new Kellogg_StSpSw(this, "Const.");
		_na_sw.setState(false);
		_cd_sw.setState(false);

		GridBagLayout gb = new GridBagLayout();
		setLayout(gb);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(obj_width,
					10 + 2 * Kellogg_Plug.obj_height));
		pan.setOpaque(false);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(pan, s);
		add(pan);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(_na_sw, s);
		add(_na_sw);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(obj_width, 10));
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(pan, s);
		add(pan);

		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(_cd_sw, s);
		add(_cd_sw);

		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);

		// sometimes, this fails because of exclusive audio
		// access. not sure how to share, but need to disable
		// audio in that case.
		try {
			_ringer = AudioSystem.getClip();
			AudioInputStream wav =
				AudioSystem.getAudioInputStream(
					switchboard.class.getResourceAsStream(
						"sounds/ring.wav"));
			_ringer.open(wav);
			_ringer.setLoopPoints(0, 4500);
		} catch (Exception e) {
			_ringer = null;
		}
	}

	private void alarmChanged() {
		boolean alarming;
		if (_na_sw.getState()) {
			alarming = (_const_drop.count() ||
				_cd_sw.getState() && _coded_drop.count() ||
				!_cd_sw.getState() && _coded_ring.count());
		} else {
			alarming = false;
		}
		if (alarming != _alarm) {
			_alarm = alarming;
			if (_alarm) {
				_alarm = true;
				_ringer.setFramePosition(0);
				_ringer.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				_alarm = false;
				_ringer.loop(0);
			}
			repaint();
		}
	}

	public void listener(JComponent sw, boolean state) {
		alarmChanged();
	}

	public void alarmDrop(boolean coded, boolean on) {
		if (coded) {
			_coded_drop.alarmer(on);
		} else {
			_const_drop.alarmer(on);
		}
		alarmChanged();
	}

	public void alarmRing(boolean on) {
		_coded_ring.alarmer(on);
		alarmChanged();
	}
}
