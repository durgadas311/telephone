// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Kellogg_RingSw extends JPanel
	implements MouseListener, KeyListener
{
	public static final int obj_width = 75;
	public static final int obj_height = 66;

	public static final int PUSH = 1;
	public static final int OFF = 0;
	public static final int PULL = -1;

	private static final String _tag1 = "Ring-back";
	private static final int _tag1_x = 10;
	private static final String _tag2 = "Ring";
	private static final int _tag2_x = 25;

	private int _state;
	private Kellogg_Cabinet _cab;

	public void paint(Graphics g) {
		super.paint(g);
		g.drawString(_tag1, _tag1_x, 9);
		g.drawString(_tag2, _tag2_x, 63);
		if (_state == PUSH) {
			// "Up" or "Pushed"... Ring Answer
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
		} else if (_state == PULL) {
			// "Down" or "Pulled" - Ring Call
			g.setColor(switchboard.jack_lt);
			g.fillRect(35, 12, 10, 20);
			g.setColor(switchboard.jack_face);
			g.fillRect(35, 32, 10, 10);
			g.setColor(switchboard.jack_hole);
			g.fillOval(30, 37, 20, 15);
			g.fillOval(30, 47, 20, 15);
			g.fillRect(30, 45, 20, 10);
			g.setColor(switchboard.jack_lt);
			g.drawArc(32, 39, 16, 12, 135, 25);
			g.drawLine(32, 45, 32, 56);
		} else { // OFF
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

	public Kellogg_RingSw(Component top, Kellogg_Cabinet cab) {
		_cab = cab;
		_state = OFF;
		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font2);
		addMouseListener(this);
		top.addKeyListener(this);
	}

	public void setState(int b) {
		int n = _state;
		if (b == MouseEvent.BUTTON1) { // up... PUSH
			n += 1;
			if (n > PUSH) n = PUSH;
		} else if (b == MouseEvent.BUTTON3) {
			n -= 1;
			if (n < PULL) n = PULL;
		}
		if (_state != n) {
			_state = n;
			repaint();
		}
	}

	public int getState() { return _state; }

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {
		int b = e.getButton(); // 1=Left, 3=Right
		setState(b);
	}
	public void mouseReleased(MouseEvent e) {
		boolean stick = ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0);
		if (_state != 0 && !stick) {
			_state = 0;
			repaint();
		}
	}

	public void keyTyped(KeyEvent e) { }
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) {
		if (_state != 0 && e.getKeyCode() == KeyEvent.VK_SHIFT) {
			// this must also stop the ringing... but don't change
			// state until after...
			_cab.ring(false);
			_state = 0;
			repaint();
		}
	}
}
