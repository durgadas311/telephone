// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Kellogg_StSpSw extends JPanel
	implements MouseListener
{
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
