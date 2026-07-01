// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Kellogg_Plug extends JPanel
	implements MouseListener
{
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
