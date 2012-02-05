// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.3 2012/02/05 15:12:55 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.3 2012/02/05 15:12:55 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);
	static final Color drop = new Color(150, 150, 150);
	static final Color drop_lt = new Color(170, 170, 170);
	static final Color drop_dk = new Color(130, 130, 130);
	static final Color edge = new Color(130, 130, 130);
	static final Color edge_lt = new Color(150, 150, 150);
	static final Color edge_dk = new Color(110, 110, 110);
	static final Color jack_bev = new Color(180, 180, 180);
	static final Color jack_face = new Color(240, 240, 240);
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

		int x;
		Kellogg_LineWithDrop li;
		for (x = 0; x < num_lines; ++x) {
			li = new Kellogg_LineWithDrop(x + 1, font_metrics);
			s.gridx = x % lines_per_row;
			s.gridy = x / lines_per_row;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(li, s);
			front_end.add(li);
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
	final String ident = "$Id: switchboard.java,v 1.3 2012/02/05 15:12:55 drmiller Exp $";
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
	final String ident = "$Id: switchboard.java,v 1.3 2012/02/05 15:12:55 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;

	static final int[] hex_x = { 20, 40, 50, 40, 20, 10, 20 };
	static final int[] hex_y = {  0,  0, 15, 30, 30, 15,  0 };

	private boolean _plugged;
	private JPanel _parent;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(switchboard.jack_bev);
		g.fillPolygon(hex_x, hex_y, 6);
		g.setColor(switchboard.jack_face);
		g.fillOval(15,  0, 30, 30);
		g.setColor(switchboard.jack_well);
		g.fillOval(20,  5, 20, 20);
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
	final String ident = "$Id: switchboard.java,v 1.3 2012/02/05 15:12:55 drmiller Exp $";
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
