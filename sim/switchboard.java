// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";

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

	static final Font font = new Font("Serif", Font.PLAIN, 18);

	public static void main(String[] args) {

		JFrame front_end = new JFrame("Kellogg 1915 Telephone Switchboard");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);
		FontMetrics font_metrics = front_end.getFontMetrics(font);

		int num_lines = 20;
		int num_circs = 4;

		Kellogg_Cabinet cab = new Kellogg_Cabinet(num_lines, num_circs, font_metrics);
		front_end.add(cab);

		front_end.getContentPane().setBackground(cabinet);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}
}

class Kellogg_Cabinet extends JPanel
{
	static final long serialVersionUID = 311000000010L;

	private Kellogg_Plug _sel_plug;

	private Kellogg_Plug[] _conn_plugs;
	private Kellogg_Line[] _conn_lines;

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
	}

	public Kellogg_Cabinet(int num_lines, int num_circs,
			FontMetrics font_metrics) {
		_sel_plug = null;
		_conn_plugs = new Kellogg_Plug[num_circs * 2];
		_conn_lines = new Kellogg_Line[num_circs * 2];

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
		s.anchor = GridBagConstraints.WEST;

		int last_y = 0;

		Kellogg_LinePanel lp = new Kellogg_LinePanel(num_lines, this, font_metrics);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 8;
		s.gridheight = 1;
		gridbag.setConstraints(lp, s);
		add(lp);

		++last_y;
		JPanel pan = new JPanel();
		pan.setOpaque(true);
		pan.setBackground(Color.black);
		pan.setPreferredSize(new Dimension(60 * Kellogg_LinePanel.lines_per_row, 1));
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 8;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);

		last_y = 2;
		int x;
		for (x = 0; x < num_circs; ++x) {
			Kellogg_Drop drp1 = new Kellogg_Drop(x + 1, 75, font_metrics);
			s.gridx = x;
			s.gridy = last_y;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(drp1, s);
			add(drp1);
			Kellogg_Drop drp2 = new Kellogg_Drop(x + 1, 75, font_metrics);
			s.gridx = x;
			s.gridy = last_y + 1;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(drp2, s);
			add(drp2);
			Kellogg_Circuit cir = new Kellogg_Circuit(x + 1, this,
						drp1, drp2, font_metrics);
			s.gridx = x;
			s.gridy = last_y + 3;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(cir, s);
			add(cir);
		}
		last_y += 2;

		pan = new JPanel();
		pan.setOpaque(true);
		pan.setBackground(Color.black);
		pan.setPreferredSize(new Dimension(60 * Kellogg_LinePanel.lines_per_row, 1));
		s.gridx = 0;
		s.gridy = last_y;
		s.gridwidth = 8;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);

		last_y += 2;
		setOpaque(false);
		setForeground(Color.red);
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
				_sel_plug = null;
				_conn_plugs[i].setPlugged(true);
				_conn_lines[i].setPlugged(true);
				repaint();
				return;
			}
		}
	}

	public void disconnect(Kellogg_Plug plug) {
		int i;
		for (i = 0; i < _conn_plugs.length; ++i) {
			if (_conn_plugs[i] == plug) {
				_conn_plugs[i].setPlugged(false);
				_conn_lines[i].setPlugged(false);
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
				_conn_plugs[i].setPlugged(false);
				_conn_lines[i].setPlugged(false);
				_conn_plugs[i] = null;
				_conn_lines[i] = null;
				repaint();
				return;
			}
		}
	}

	public void selectPlug(Kellogg_Plug plug) {
		if (_sel_plug != null) {
			_sel_plug.setSelect(false);
			if (_sel_plug == plug) {
				_sel_plug = null;
				return;
			}
		}
		_sel_plug = plug;
		_sel_plug.setSelect(true);
	}
}

class Kellogg_LinePanel extends JPanel
{
	static final long serialVersionUID = 311000000009L;

	public static final int lines_per_row = 10;

	public Kellogg_LinePanel(int num, Kellogg_Cabinet cab, FontMetrics font_metrics) {
		int x;
		int max_row = num / lines_per_row;
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
		Kellogg_LineWithDrop li;
		for (x = 0; x < num; ++x) {
			li = new Kellogg_LineWithDrop(this, x + 1, cab, font_metrics);
			s.gridx = x % lines_per_row;
			s.gridy = max_row - (x / lines_per_row);
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(li, s);
			add(li);
		}
	}
}

class Kellogg_Drop extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000003L;

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
	private int[] _shutter_x;
	private int[] _shutter_dn_x;
	private int[] _label;
	private int[] _label_dn_x;

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

	public Kellogg_Drop(int num, int width, FontMetrics font_metrics) {
		_num = num;
		_tag = Integer.toString(_num);
		int w = font_metrics.stringWidth(_tag);
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
		setPreferredSize(new Dimension(60, 60));
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
		}
	}

	public boolean isDropped() {
		return _dropped;
	}
}

class Kellogg_Line extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;

	static final int[] hex_top_x = { 20, 40, 46, 14, 10, 20 };
	static final int[] hex_top_y = {  0,  0,  9, 21, 15,  0 };

	static final int[] hex_bot_x = { 46, 50, 40, 20, 14, 46 };
	static final int[] hex_bot_y = {  9, 15, 31, 31, 21,  9 };

	static final Point _center = new Point(30, 15);

	private boolean _plugged;
	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Cabinet _cab;

	public void paint(Graphics g) {
		super.paint(g);
		if (_plugged) {
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
			if (_plugged) g.setColor(Color.yellow);
			g.fillOval(22,  7, 16, 16);
		}
	}

	public Kellogg_Line(JPanel parent, Kellogg_Cabinet cab, Kellogg_Drop drop) {
		_parent = parent;
		_cab = cab;
		_drop = drop;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		//setPlugged(!_plugged); // temp: bring-up
		if (_plugged) {
			_cab.disconnect(this);
		} else {
			_cab.connectLine(this); // or try at least...
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

	public void setPlugged(boolean plug) {
		if (plug != _plugged) {
			_plugged = plug;
			if (_plugged) {
				_drop.setDropped(false);
			}
			repaint();
		}
	}
}

class Kellogg_LineWithDrop extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000004L;

	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Line _line;

	public Kellogg_LineWithDrop(JPanel parent, int num,
			Kellogg_Cabinet cab, FontMetrics font_metrics) {
		_parent = parent;
		_drop = new Kellogg_Drop(num, 60, font_metrics);
		_line = new Kellogg_Line(this, cab, _drop);

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
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000005L;
	static final Point _center = new Point(40, 18);

	private JPanel _parent;
	private Kellogg_Drop _co_drop;
	private boolean _select;
	private boolean _plugged;
	private Kellogg_Cabinet _cab;

	public void paint(Graphics g) {
		super.paint(g);
		if (_select) {
			g.setColor(Color.yellow);
			g.fillOval(22,  0, 36, 36);
		}
		if (_plugged) {
			g.setColor(switchboard.well_lt);
			g.fillArc(25,  3, 30, 30, -135, 180);
			g.setColor(switchboard.well_dk);
			g.fillArc(25,  3, 30, 30, 45, 180);
			g.setColor(switchboard.well);
			g.fillOval(27,  5, 26, 26);
			g.setColor(switchboard.cord);
			g.fillOval(33, 11, 14, 14);
		} else {
			g.setColor(switchboard.plug);
			g.fillOval(25,  3, 30, 30);
			g.setColor(switchboard.ring);
			g.fillOval(31,  9, 18, 18);
			g.setColor(switchboard.tip_dk);
			g.fillOval(33, 11, 14, 14);
			g.setColor(switchboard.tip);
			g.fillArc(33, 11, 14, 14, 45, 180);
			g.setColor(switchboard.tip_lt);
			g.fillArc(33, 11, 14, 14, 112, 45);
			g.fillOval(38, 16, 4, 4);
		}
	}

	public Kellogg_Plug(JPanel parent, Kellogg_Cabinet cab, Kellogg_Drop drop) {
		_co_drop = drop;
		_parent = parent;
		_cab = cab;
		setPreferredSize(new Dimension(75, 40));
		setOpaque(false);
		setForeground(Color.black);
		setBackground(Color.blue);
		setFont(switchboard.font);
		addMouseListener(this);
		_select = false;
		_plugged = false;
	}

	public void mouseClicked(MouseEvent e) {
		if (!_plugged) {
			_cab.selectPlug(this);
		}
		if (_co_drop == null) return; // damn warnings
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

	public void setPlugged(boolean plug) {
		if (plug != _plugged) {
			_plugged = plug;
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
}

class Kellogg_RingSw extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000007L;

	private int _state;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_lt);
		g.fillRect(35, 0, 10, 20);
		g.setColor(switchboard.jack_dk);
		g.fillRect(35, 20, 10, 20);
		g.setColor(switchboard.jack_hole);
		if (_state > 0) {
			// Ring Answer
			g.fillOval(30, 0, 20, 20);
		} else if (_state < 0) {
			// Ring Call
			g.fillOval(30, 20, 20, 20);
		} else { // Off
			g.fillOval(30, 10, 20, 20);
		}
	}

	public Kellogg_RingSw() {
		_state = 0;
		setPreferredSize(new Dimension(75, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void setState(int b) {
		int n = _state;
		if (b == MouseEvent.BUTTON1) { // up...
			n += 1;
			if (n > 1) n = 1;
		} else if (b == MouseEvent.BUTTON3) {
			n -= 1;
			if (n < -1) n = -1;
		}
		if (_state != n) {
			_state = n;
			repaint();
		}
	}

	public void mouseClicked(MouseEvent e) {
		int b = e.getButton(); // 1=Left, 3=Right
		setState(b);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

class Kellogg_ListenSw extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000006L;

	private boolean _state;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_lt);
		g.fillRect(35, 0, 10, 20);
		g.setColor(switchboard.jack_dk);
		g.fillRect(35, 20, 10, 20);
		g.setColor(switchboard.jack_hole);
		if (_state) {
			g.fillOval(30, 0, 20, 20);
		} else {
			g.fillOval(30, 10, 20, 20);
		}
	}

	public Kellogg_ListenSw() {
		setPreferredSize(new Dimension(75, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		setState(!_state);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public void setState(boolean on) {
		if (_state != on) {
			_state = on;
			repaint();
		}
	}
}

class Kellogg_Circuit extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.13 2012/02/06 17:58:14 drmiller Exp $";
	static final long serialVersionUID = 311000000008L;

	private Kellogg_ListenSw _listen;
	private Kellogg_RingSw _ring;
	private Kellogg_Plug _call;
	private Kellogg_Plug _ans;
	private Kellogg_Drop _drop_call;
	private Kellogg_Drop _drop_ans;

	// R.O.Drop  [upper](originator attn signal)
	// R.O.Drop  [lower](target attn signal)
	// Answer    [far]  (operator answers originator)
 	// Call      [near] (operator calls target)
	// Ring-back [push] (operator rings originator)
	// Ring      [pull] (operator rings target)
	// Listen    [push] (operator taps-in to circuit)
	public Kellogg_Circuit(int num, Kellogg_Cabinet cab,
			Kellogg_Drop drp_up, Kellogg_Drop drp_lo,
			FontMetrics font_metrics) {
		if (font_metrics.stringWidth("damn warnings") < 0) return;
		_drop_ans = drp_up;
		_drop_call = drp_lo;
		_listen = new Kellogg_ListenSw();
		_ring = new Kellogg_RingSw();
		_call = new Kellogg_Plug(this, cab, _drop_call);
		_ans = new Kellogg_Plug(this, cab, _drop_ans);

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

		JLabel lab = new JLabel("Answer");
		lab.setFont(switchboard.font);
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ans, s);
		add(_ans);

		lab = new JLabel("Call");
		lab.setFont(switchboard.font);
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_call, s);
		add(_call);

		lab = new JLabel("<HTML><FONT SIZE=-2>Ring-back</FONT></HTML>");
		lab.setFont(switchboard.font);
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		s.gridx = 0;
		s.gridy = 4;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);
		s.gridx = 0;
		s.gridy = 5;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ring, s);
		add(_ring);
		lab = new JLabel("Ring");
		lab.setFont(switchboard.font);
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		s.gridx = 0;
		s.gridy = 6;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);

		lab = new JLabel("Listen");
		lab.setFont(switchboard.font);
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		s.gridx = 0;
		s.gridy = 7;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);
		s.gridx = 0;
		s.gridy = 8;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_listen, s);
		add(_listen);

		lab = new JLabel(Integer.toString(num));
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		lab.setFont(switchboard.font);
		s.gridx = 0;
		s.gridy = 9;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);

		setPreferredSize(new Dimension(75, 300));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}
}
