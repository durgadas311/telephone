// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Kellogg_Magneto extends JPanel
	implements MouseListener, ActionListener
{
	public static final int obj_width = 100;
	public static final int obj_height = 50;

	private Kellogg_Cabinet _cab;
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
		g.setColor(switchboard.plug);
		g.fillRect(_pos - 5, 0, 10, 20);
		int x = _pos + (int)Math.round(_sin[_angle] * 40);
		int x1, x2;
		if (x < _pos) {
			x2 = _pos + 5;
			x1 = x - 5;
		} else {
			x2 = x + 5;
			x1 = _pos - 5;
		}
		g.fillRoundRect(x - 5, 25, 10, 20, 3, 3);
		g.fillRect(x1, 20, x2 - x1, 5);
		g.setColor(switchboard.plug_lt);
		g.fillArc(x - 4, 26, 6, 6, 90, 90);
		g.drawLine(x - 4, 28, x - 4, 42);
		g.drawLine(_pos - 4, 2, _pos - 4, 18);
	}

	public Kellogg_Magneto(Kellogg_Cabinet cab) {
		_cab = cab;
		_pos = 50;
		setOpaque(false);
		//setOpaque(true);
		//setBackground(Color.gray);
		setPreferredSize(new Dimension(100, 50));
		addMouseListener(this);
		setFocusable(false);
		_angle = 0;
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
