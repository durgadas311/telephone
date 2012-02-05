// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);
	static final Color drop = new Color(150, 150, 150);
	static final Color drop_lt = new Color(170, 170, 170);
	static final Color drop_dk = new Color(130, 130, 130);
	static final Color edge = new Color(130, 130, 130);
	static final Color edge_lt = new Color(150, 150, 150);
	static final Color edge_dk = new Color(110, 110, 110);
	static final Color jack_bev = new Color(180, 180, 180);
	static final Color jack_face = new Color(220, 220, 220);
	static final Color jack_dk = new Color(170, 170, 170);
	static final Color jack_lt = new Color(255, 255, 255);
	static final Color jack_well = new Color(150, 150, 150);
	static final Color jack_hole = new Color(20, 20, 20);

	static final Font font = new Font("Serif", Font.PLAIN, 18);

	public static void main(String[] args) {
		GridBagLayout gridbag = new GridBagLayout();

		JFrame front_end = new JFrame("Kellogg 1915 Telephone Switchboard");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);
		FontMetrics font_metrics = front_end.getFontMetrics(font);

		front_end.setLayout(gridbag);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		int num_lines = 5;
		int lines_per_row = 10;
		int num_circs = 2;

		int max_x = 0;
		int last_y = 0;

		int x;
		Kellogg_LineWithDrop li;
		for (x = 0; x < num_lines; ++x) {
			li = new Kellogg_LineWithDrop(x + 1, font_metrics);
			s.gridx = x % lines_per_row;
			s.gridy = x / lines_per_row;
			if (s.gridy > last_y) last_y = s.gridy;
			if (s.gridx > max_x) max_x = s.gridx;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(li, s);
			front_end.add(li);
		}

		++last_y;
		JPanel pan = new JPanel();
		pan.setOpaque(true);
		pan.setBackground(Color.black);
		pan.setPreferredSize(new Dimension(60 * (max_x + 1), 1));
			s.gridx = 0;
			s.gridy = last_y;
			s.gridwidth = max_x + 1;
			s.gridheight = 1;
			gridbag.setConstraints(pan, s);
			front_end.add(pan);

		++last_y;
		for (x = 0; x < num_circs; ++x) {
			Kellogg_Circuit cir = new Kellogg_Circuit(x + 1, font_metrics);
			s.gridx = x;
			s.gridy = last_y;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(cir, s);
			front_end.add(cir);
		}

		front_end.getContentPane().setBackground(cabinet);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}
}

class Kellogg_Drop extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000003L;

	static final int[] shutter_x = { 40, 50, 50, 10, 10, 20, 40 };
	static final int[] shutter_y = { 10, 20, 50, 50, 20, 10, 10 };

	static final int[] shutter_dn_x = { 40, 52, 50, 10,  8, 20, 40 };
	static final int[] shutter_dn_y = { 38, 41, 50, 50, 41, 38, 38 };

	private int _num;
	private String _tag;
	private int _tag_x;
	private boolean _dropped;

	public void paint(Graphics g) {
		super.paint(g);
		if (_dropped) {
			g.setColor(switchboard.jack_bev);
			g.fillPolygon(shutter_x, shutter_y, 6);

			g.setColor(switchboard.edge);
			g.fillPolygon(shutter_dn_x, shutter_dn_y, 6);
			g.setColor(switchboard.edge_dk);
			g.drawPolyline(shutter_dn_x, shutter_dn_y, 2);
			g.setColor(switchboard.edge_lt);
			g.drawPolyline(Arrays.copyOfRange(shutter_dn_x, 4, 7),
					Arrays.copyOfRange(shutter_dn_y, 4, 7), 3);
		} else {
			g.setColor(switchboard.drop);
			g.fillPolygon(shutter_x, shutter_y, 6);
			g.setColor(switchboard.drop_dk);
			g.drawPolyline(shutter_x, shutter_y, 4);
			g.setColor(switchboard.drop_lt);
			g.drawPolyline(Arrays.copyOfRange(shutter_x, 3, 7),
					Arrays.copyOfRange(shutter_y, 3, 7), 4);
			g.setColor(Color.red);
			g.drawString(_tag, _tag_x, 40);
		}
	}

	public Kellogg_Drop(int num, FontMetrics font_metrics) {
		_num = num;
		_tag = Integer.toString(_num);
		int w = font_metrics.stringWidth(_tag);
		_tag_x = 30 - (w / 2);
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
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;

	static final int[] hex_top_x = { 20, 40, 46, 14, 10, 20 };
	static final int[] hex_top_y = {  0,  0,  9, 21, 15,  0 };

	static final int[] hex_bot_x = { 46, 50, 40, 20, 14, 46 };
	static final int[] hex_bot_y = {  9, 15, 31, 31, 21,  9 };

	private boolean _plugged;
	private JPanel _parent;

	public void paint(Graphics g) {
		super.paint(g);
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
		if (_plugged) {
			Point p,m;
			p = _parent.getLocation();
			m = getLocation();
			p.x += m.x;
			p.y += m.y;
			Dimension d = getSize();
			p.x += (d.width / 2);
			p.y += (d.height / 2);
			System.err.println("click "+p);
		}
	}

	public Kellogg_Line(JPanel parent) {
		_parent = parent;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		setPlugged(!_plugged); // temp: bring-up
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public void setPlugged(boolean plug) {
		if (plug != _plugged) {
			_plugged = plug;
			repaint();
		}
	}
}

class Kellogg_LineWithDrop extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000004L;

	private Kellogg_Drop _drop;
	private Kellogg_Line _line;

	public Kellogg_LineWithDrop(int num, FontMetrics font_metrics) {
		_drop = new Kellogg_Drop(num, font_metrics);
		_line = new Kellogg_Line(this);

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
}

class Kellogg_Plug extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000005L;

	private JPanel _parent;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_face);
		g.fillOval(15,  0, 30, 30);
	}

	public Kellogg_Plug(JPanel parent) {
		_parent = parent;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		Point p;
		p = _parent.getLocation();
		System.err.println("click "+p);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

class Kellogg_RingSw extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000007L;

	private JPanel _parent;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_hole);
		g.fillOval(15,  0, 30, 30);
	}

	public Kellogg_RingSw(JPanel parent) {
		_parent = parent;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		Point p;
		p = _parent.getLocation();
		System.err.println("click "+p);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

class Kellogg_ListenSw extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000006L;

	private JPanel _parent;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_hole);
		g.fillOval(15,  0, 30, 30);
	}

	public Kellogg_ListenSw(JPanel parent) {
		_parent = parent;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		Point p;
		p = _parent.getLocation();
		System.err.println("click "+p);
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

class Kellogg_Circuit extends JPanel
{
	final String ident = "$Id: switchboard.java,v 1.5 2012/02/05 16:15:17 drmiller Exp $";
	static final long serialVersionUID = 311000000008L;

	private Kellogg_ListenSw _listen;
	private Kellogg_RingSw _ring;
	private Kellogg_Plug _call;
	private Kellogg_Plug _ans;

	public Kellogg_Circuit(int num, FontMetrics font_metrics) {
		if (font_metrics.stringWidth("damn warnings") < 0) return;
		_listen = new Kellogg_ListenSw(this);
		_ring = new Kellogg_RingSw(this);
		_call = new Kellogg_Plug(this);
		_ans = new Kellogg_Plug(this);

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
		gridbag.setConstraints(_call, s);
		add(_call);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ans, s);
		add(_ans);

		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ring, s);
		add(_ring);

		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_listen, s);
		add(_listen);

		JLabel lab = new JLabel(Integer.toString(num));
		lab.setForeground(Color.black);
		lab.setOpaque(false);
		lab.setFont(switchboard.font);
		s.gridx = 0;
		s.gridy = 4;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(lab, s);
		add(lab);

		setPreferredSize(new Dimension(60, 200));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}
}
