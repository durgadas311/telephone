// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.border.*;

class Kellogg_Cabinet extends JLayeredPane
		implements Runnable, KeyListener, ActionListener, Kellogg_SwListener
{
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
	//private int _hosts_ix;
	//private int _hosts_try;

	private class Kellogg_Cabinet_Overlay extends JPanel
	{
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
			super();
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
			_night_alarm = new Kellogg_NightAlarm(prop);
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

		add(base, Integer.valueOf(10), 10);
		add(ovr, Integer.valueOf(20), 20);

		_scroll.setPreferredSize(new Dimension(dim.width, text_height));
		_scroll.setBounds(0, dim.height, dim.width, text_height);
		add(_scroll, Integer.valueOf(30), 30);

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
		if (h.length() == 0) h = "localhost:31100";
		_hosts = h.split("[ \\t\\n]+");
		_host_t = new JTextArea();
		_host_t.setPreferredSize(new Dimension(200, _hosts.length * 20));
		_host_f = new JPanel();
		_host_f.add(new JLabel("Hosts:"));
		_host_f.add(_host_t);

		int x;
		Exception ee = null;
		String em = "";
		int p = 31100;
		for (x = 0; x < _hosts.length; ++x) {
			String[] hp = _hosts[x].split(":");
			try {
				p = 31100;
				if (hp.length > 1) {
					p = Integer.valueOf(hp[1]);
				}
				InetAddress ia;
				if (hp[0].length() == 0 || hp[0].equals("localhost")) {
					ia = InetAddress.getLocalHost();
				} else {
					ia = InetAddress.getByName(hp[0]);
				}
				_ss = new ServerSocket(p, 1, ia);
				//_hosts_ix = x;
				//_hosts_try = (x + 1) % _hosts.length;
				break;
			} catch(Exception e) {
				_ss = null;
				ee = e;
				if (p < 3000) em = "<BR>Is port " + p + " restricted?";
			}
		}
		if (_ss == null) {
			switchboard.fatal("ServerSocket",
				"<HTML>" + ee.getMessage() + em + "</HTML>");
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
			// can't do this until we also translate _hosts_ix...
			//_hosts = hs.split("[ \\t\\n]+");
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
			} catch(Exception e) {
				break;
			}
			// if this is a switchboard, and we already
			// have one connected, need to reject. maybe
			// need to connect switchboards on different port
			// and shutdown server socket when have one.
			for (x = 0; x < _nlines; ++x) {
				if (_lines[x] == null) continue;
				if (_lines[x].subscribe(s)) break;
			}
			if (x >= _nlines) {
				try { s.close(); } catch(Exception e) { }
			}
		}
		try { _ss.close(); } catch(Exception e) { }
	}
}
