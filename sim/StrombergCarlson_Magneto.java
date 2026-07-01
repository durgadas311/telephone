// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class StrombergCarlson_Magneto extends JPanel
	implements MouseListener, ActionListener
{
	public static final int obj_width = 50;
	public static final int obj_height = 100;

	private StrombergCarlson_Cabinet _cab;
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
		g.setColor(telephone.plug);
		g.fillRect(0, _pos - 5, 20, 10);
		int y = _pos + (int)Math.round(_sin[_angle] * 40);
		int y1, y2;
		if (y < _pos) {
			y2 = _pos + 5;
			y1 = y - 5;
		} else {
			y2 = y + 5;
			y1 = _pos - 5;
		}
		g.fillRoundRect(25, y - 5, 20, 10, 3, 3);
		g.fillRect(20, y1, 5, y2 - y1);
		g.setColor(telephone.plug_lt);
		g.fillArc(26, y - 4, 6, 6, 90, 90);
		g.drawLine(28, y - 4, 42, y - 4);
		g.drawLine(2, _pos - 4, 18, _pos - 4);
	}

	public StrombergCarlson_Magneto(StrombergCarlson_Cabinet cab) {
		_cab = cab;
		_pos = 50;
		setOpaque(false);
		//setOpaque(true);
		//setBackground(Color.gray);
		setPreferredSize(new Dimension(obj_width, obj_height));
		addMouseListener(this);
		setFocusable(false);
		_angle = 16;
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
