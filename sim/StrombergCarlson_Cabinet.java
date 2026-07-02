// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.text.Caret;
import javax.swing.border.*;

class StrombergCarlson_Cabinet extends JPanel
		implements Runnable, KeyListener, ActionListener
{
	public static final int border_size = 2;
	public static final int text_height = 100;

	public static FontMetrics font_metrics;

	private StrombergCarlson_Rec _rec;
	private StrombergCarlson_Bell _bell;
	private StrombergCarlson_Shelf _shelf;
	private StrombergCarlson_NamePlate _plate;
	private Component _top;

	private Socket _s;
	private BufferedReader _in;
	private OutputStream _out;

	private boolean _off_hook;
	private String _txt;
	private JTextArea _text;
	private JScrollPane _scroll;
	private int _off_hook_h;
	private int _on_hook_h;

	private StrombergCarlson_Help _help;
	private StrombergCarlson_Properties _prop;

	private JTextField _host_t;
	private JPanel _host_f;
	private String[] _hosts;

	public StrombergCarlson_Cabinet(Component top, StrombergCarlson_Properties prop,
							StrombergCarlson_Help help) {
		font_metrics = top.getFontMetrics(telephone.font);
		_top = top;
		_help = help;
		_prop = prop;
		_s = null;

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		setOpaque(false);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		JPanel upper = new JPanel();
		GridBagLayout ugb = new GridBagLayout();
		upper.setLayout(ugb);
		upper.setOpaque(true);
		upper.setBackground(telephone.cabinet);
		upper.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					telephone.well_lt, telephone.well_dk));
		Dimension dim;
		dim = new Dimension(140 +
				2 * border_size,
				315);
//				StrombergCarlson_Shelf.obj_width +
//				2 * border_size,
//				StrombergCarlson_Bell.obj_height +
//				StrombergCarlson_Trans.obj_height +
//				StrombergCarlson_Shelf.obj_height +
//				10 + 30 + 40 + 10 + 2 *border_size);
		upper.setPreferredSize(dim);
		int row = 0;
		int cab_gh = 3;

		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 5));
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		_bell = new StrombergCarlson_Bell(_prop);
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.NORTH;
		ugb.setConstraints(_bell, s);
		upper.add(_bell);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 40));
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		StrombergCarlson_Trans tran = new StrombergCarlson_Trans();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(tran, s);
		upper.add(tran);

		_plate = new StrombergCarlson_NamePlate();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(_plate, s);
		upper.add(_plate);

		_shelf = new StrombergCarlson_Shelf();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.SOUTH;
		ugb.setConstraints(_shelf, s);
		upper.add(_shelf);

		s.gridx = 1;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = cab_gh;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(upper, s);
		add(upper);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		_rec = new StrombergCarlson_Rec(this);
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 2;
		s.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(_rec, s);
		add(_rec);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 2;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 30));
		s.gridx = 2;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		StrombergCarlson_Magneto mag = new StrombergCarlson_Magneto(this);
		s.gridx = 2;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(mag, s);
		add(mag);

		Dimension dim2 = new Dimension(dim);
		dim2.width += StrombergCarlson_Rec.obj_width +
				StrombergCarlson_Magneto.obj_width;
		setPreferredSize(dim2);
		_on_hook_h = 0;
		_off_hook_h = 0;

		_off_hook = false;
		_txt = new String();
		_text = new JTextArea();
		_text.setEditable(false);
		_text.setFocusable(false);
		_text.setFont(telephone.font2);
		_text.setBackground(Color.white);
		_text.setLineWrap(true);
		_text.setWrapStyleWord(true);
		Caret caret = new javax.swing.plaf.basic.BasicTextUI.BasicCaret();
		caret.setBlinkRate(500);
		_text.setCaret(caret);
		_text.getCaret().setVisible(true);
		_text.setCaretColor(telephone.cabinet);
		_scroll = new JScrollPane(_text);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		_scroll.setPreferredSize(new Dimension(dim2.width, text_height));
		_scroll.setFocusable(false);
		_scroll.setVisible(false);
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 3;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(_scroll, s);
		add(_scroll);
 
		String h = _prop.getProperty("switchboard_host");
		if (h.length() == 0) h = "localhost:31100";
		_hosts = h.split("[ \\t\\n]+");

		_host_t = new JTextField();
		_host_t.setPreferredSize(new Dimension(200, _hosts.length * 20));
		_host_f = new JPanel();
		_host_f.add(new JLabel("Hosts:"));
		_host_f.add(_host_t);

		Thread t = new Thread(this);
		t.start();
	}
 
	public void actionPerformed(ActionEvent e) {
		if (!(e.getSource() instanceof JMenuItem)) {
			return;
		}
		JMenuItem m = (JMenuItem)e.getSource();
		if (m.getMnemonic() == KeyEvent.VK_H) {
			_help.setOn(true);
			return;
		}
		if (m.getMnemonic() == KeyEvent.VK_A) {
			_help.showAbout();
			return;
		}
		if (m.getMnemonic() == KeyEvent.VK_U) {
			String h = _prop.getProperty("switchboard_host");
			h = h.replaceAll("[ \\t\\n]+", "\n");
			_host_t.setText(h);
			Object[] dia = { _host_f };
			int ret = JOptionPane.showConfirmDialog(this, dia,
				"Set Switchboard Parameters",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return;
			}
			// TBD: change parameters and restart?
			String hs = _host_t.getText();
			hs = hs.replaceAll("[ \\t\\n]+", " ");
			_prop.setProperty("switchboard_host", hs);
			_prop.save();
			_hosts = hs.split("[ \\t\\n]+");
			return;
		}
	}

	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		type(c);
		if (c == KeyEvent.VK_ENTER) {
			try {
				_out.write(typed().getBytes());
			} catch (Exception ee) {
			}
		}
	}

	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }

	public void ring(boolean on) {
		try {
			if (on) {
				_out.write("%RING\n".getBytes());
			} else {
				_out.write("%RINGOFF\n".getBytes());
			}
		} catch (Exception ee) {
		}
		// _bell.ring(); // ring locally, too?
	}

	public void goOffHook(boolean off_hook) {
		if (_off_hook != off_hook) {
			_off_hook = off_hook;
			Dimension dim = _top.getSize();
			if (_off_hook_h == 0) {
				_on_hook_h = dim.height;
				_off_hook_h = _on_hook_h + text_height;
			}
			if (_off_hook) {
				if (dim.height < _off_hook_h) dim.height = _off_hook_h;
			} else {
				_text.setText("");
				if (dim.height == _off_hook_h) dim.height = _on_hook_h;
			}
			_top.setSize(dim);
			_scroll.setVisible(_off_hook);
			repaint();
		}
	}

	private void doCommand(String s) {
		if (s.equals("%RING")) {
			_bell.ring(true);
		} else if (s.equals("%RINGOFF")) {
			_bell.ring(false);
		} else if (s.startsWith("%NAME=")) {
			_plate.setName(s.substring(6));
		}
	}

	public void run() {
		int hx = 0;
		while (true) {
			if (_s == null) {
				String[] hp = _hosts[hx].split(":");
				try {
					int p = 31100;
					if (hp.length > 1) {
						p = Integer.valueOf(hp[1]);
					}
					InetAddress ia;
					if (hp[0].length() == 0 || hp[0].equals("localhost")) {
						ia = InetAddress.getLocalHost();
					} else {
						ia = InetAddress.getByName(hp[0]);
					}
					_s = new Socket(ia, p);
				} catch (Exception ee) {
					_s = null;
					hx = (hx + 1) % _hosts.length;
				}
				if (_s == null) {
					_plate.setName("waiting");
					try {
						Thread.sleep(5);
					} catch (InterruptedException ee) {
					}
				} else {
					try {
						_in = new BufferedReader(new InputStreamReader(_s.getInputStream()));
						_out = _s.getOutputStream();
						String s = "%TYPE=telephone\n";
						_out.write(s.getBytes());
						s = "%NAME=" +
							System.getProperty("user.name") +
							"\n";
						_out.write(s.getBytes());
					} catch (Exception ee) {
					}
				}
			} else {
				String s = null;
				try {
					s = _in.readLine();
				} catch(Exception e) { }
				if (s == null) {
					try {
						_in.close();
						_out.close();
						_s.close();
					} catch(Exception e) { }
					_s = null;
					continue;
				}
				if (s.startsWith("%")) {
					doCommand(s);
				} else {
					post(s);
				}
			}
		}
	}

	public void post(String s) {
		if (!_off_hook) return;
		_text.append(">> " + s + "\n");
		_text.setCaretPosition(_text.getText().length());
	}

	public void type(char c) {
		if (!_off_hook) return;
		if (c == KeyEvent.VK_BACK_SPACE) {
			int p = _text.getCaretPosition();
			_text.replaceRange("", p - 1, p);
			_txt = _txt.substring(0, _txt.length() - 1);
			return;
		}
		String s = Character.toString(c);
		_text.append(s);
		_txt += s;
	}

	public String typed() {
		if (!_off_hook) return null;
		String s = _txt;
		_txt = new String();
		return s;
	}
}
