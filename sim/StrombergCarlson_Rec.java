// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class StrombergCarlson_Rec extends JPanel
	implements MouseListener
{
	public static final int obj_width = 50;
	public static final int obj_height = 85;

	private boolean _off_hook;
	private StrombergCarlson_Cabinet _cab;
	private static final int[] rec_x = { 13, 21, 39, 47, 44, 16, 13 };
	private static final int[] rec_y = { 71, 64, 64, 71, 85, 85, 71 };

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paint(g2d);
		if (_off_hook) {
			g2d.setColor(telephone.plug);
			g2d.fillOval(10, 45, 40, 40);
			g2d.setColor(telephone.plug_lt);
			g2d.drawArc(15, 50, 30, 30, 90, 90);
			g2d.drawOval(20, 55, 20, 20);
			g2d.fillOval(25, 60, 10, 10);
			g2d.setColor(telephone.jack_face);
			g2d.setStroke(new BasicStroke((float)5.0,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			g2d.fillOval(13, 0, 10, 10);
			g2d.drawLine(17, 5, 50, 13);
			g2d.setColor(telephone.jack_dk);
			g2d.drawLine(36, 10, 45, 12);
		} else {
			g2d.setColor(telephone.plug);
			g2d.fillArc(18, 0, 24, 24, 45, 90);
			g2d.fillRoundRect(19, 5, 22, 5, 3, 3);
			g2d.fillRect(21, 10, 18, 61);
			g2d.fillPolygon(rec_x, rec_y, 6);
			g2d.fillRoundRect(10, 73, 39, 8, 4, 4);
			g2d.setColor(telephone.plug_lt);
			g2d.drawArc(20, 2, 20, 20, 95, 45);
			g2d.drawLine(21, 6, 30, 6);
			g2d.drawLine(23, 11, 23, 63);
			g2d.drawLine(23, 64, 16, 71);
			g2d.drawLine(12, 74, 30, 74);
			g2d.setColor(telephone.jack_face);
			g2d.setStroke(new BasicStroke((float)5.0,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			g2d.fillOval(10, 7, 10, 10);
			g2d.drawLine(15, 13, 50, 13);
			g2d.setColor(telephone.jack_dk);
			g2d.drawLine(35, 13, 45, 13);
		}
	}

	public StrombergCarlson_Rec(StrombergCarlson_Cabinet cab) {
		_off_hook = false;
		_cab = cab;
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
		addMouseListener(this);
	}

	public boolean offHook() { return _off_hook; }

	public void mouseClicked(MouseEvent e) {
		_off_hook = !_off_hook;
		_cab.goOffHook(_off_hook);
		repaint();
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}
