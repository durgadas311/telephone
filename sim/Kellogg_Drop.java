// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.util.Arrays;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Kellogg_Drop extends JPanel
	implements MouseListener
{
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
