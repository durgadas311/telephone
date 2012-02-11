// Copyright (c) 2011,2012 Douglas Miller
// $Id: telephone.java,v 1.1 2012/02/11 16:24:21 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.net.*;
import java.io.*;

public class telephone
{
	final String ident = "$Id: telephone.java,v 1.1 2012/02/11 16:24:21 drmiller Exp $";

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
	static final Font font2 = new Font("Serif", Font.PLAIN, 12);

	public static void main(String[] args) {

		JFrame front_end = new JFrame("Stromberg-Carlson 1915 Telephone");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);
		FontMetrics font_metrics = front_end.getFontMetrics(font);

		String host = "localhost";
		String port = "31100";
		Socket sock = null;

		try {
			sock = new Socket(host, Integer.parseInt(port));
		} catch (IOException ee) {
			System.err.println("Unable to open socket to telephone!");
			System.exit(1);
		}

		StrombergCarlson_Cabinet cab = new StrombergCarlson_Cabinet(sock,
						front_end, font_metrics);
		front_end.add(cab);

		front_end.getContentPane().setBackground(Color.gray);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}
}

class StrombergCarlson_Cabinet extends JPanel // JLayeredPane
		implements Runnable
{
	static final long serialVersionUID = 311000000010L;

	public static final int border_size = 2;

	private StrombergCarlson_Rec _rec;
	private StrombergCarlson_Bell _bell;
	private StrombergCarlson_Shelf _shelf;

	private Socket _s;
	private InputStream _in;
	private OutputStream _out;
	private byte[] _buf;

	public StrombergCarlson_Cabinet(Socket so,
			Component top,
			FontMetrics font_metrics) {

		_s = so;
		try {
			_in = so.getInputStream();
			_out = so.getOutputStream();
		} catch (IOException ee) {
		}
		if (top == null) return; // damn warnings
		if (font_metrics == null) return; // damn warnings

//		JPanel base = new JPanel();
JPanel base = this;
		GridBagLayout gridbag = new GridBagLayout();
		base.setLayout(gridbag);
		base.setOpaque(false);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		JPanel upper = new JPanel();
		GridBagLayout ugb = new GridBagLayout();
		upper.setLayout(ugb);
		upper.setOpaque(true);
		upper.setBackground(telephone.cabinet);
		upper.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					telephone.well_lt, telephone.well_dk));
upper.setPreferredSize(new Dimension(110, 210));

		_bell = new StrombergCarlson_Bell();
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.NORTH;
		ugb.setConstraints(_bell, s);
		upper.add(_bell);

		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		StrombergCarlson_Trans tran = new StrombergCarlson_Trans();
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(tran, s);
		upper.add(tran);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		_shelf = new StrombergCarlson_Shelf();
		s.gridx = 0;
		s.gridy = 4;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.SOUTH;
		ugb.setConstraints(_shelf, s);
		upper.add(_shelf);

		s.anchor = GridBagConstraints.CENTER;
		s.gridx = 1;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(upper, s);
		base.add(upper);

		_rec = new StrombergCarlson_Rec(_shelf);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_rec, s);
		base.add(_rec);

		StrombergCarlson_Magneto mag = new StrombergCarlson_Magneto(this);
		s.gridx = 2;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(mag, s);
		base.add(mag);

//		Dimension dim = new Dimension(StrombergCarlson_Shelf.obj_width +
//					StrombergCarlson_Rec.obj_width +
//					StrombergCarlson_Magneto.obj_width,
//					600);
Dimension dim = new Dimension(250, 250);
		base.setPreferredSize(dim);

//		add(base, new Integer(10));

//		setPreferredSize(dim);
//		setOpaque(false);
//		setForeground(Color.red);

		_buf = new byte[128];

		Thread t = new Thread(this);
		t.start();
	}

	public void ring(boolean on) {
		if (!on) return;
		try {
			_out.write("%RING\n".getBytes());
		} catch (IOException ee) {
		}
		// _bell.ring(); // ring locally, too?
	}

	public void run() {
		int n;
		while (true) {
			try {
				n = _in.read(_buf);
			} catch(IOException e) {
				n = -1;
			}
			if (n < 0) break;
			String s = new String(_buf, 0, n);
			if (s.equals("%RING\n")) {
				_bell.ring();
			} else {
System.err.println("Got: \""+s+"\"");
				_shelf.post(s);
			}
		}
		try {
			_in.close();
			_out.close();
			_s.close();
		} catch(IOException e) { }
	}
}

class StrombergCarlson_Magneto extends JPanel
	implements MouseListener, ActionListener
{
	static final long serialVersionUID = 311000000011L;
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

class StrombergCarlson_Rec extends JPanel
	implements MouseListener
{
	static final long serialVersionUID = 311000000005L;
	public static final int obj_width = 60;
	public static final int obj_height = 100;

	private boolean _off_hook;
	private StrombergCarlson_Shelf _shelf;

	public void paint(Graphics g) {
		super.paint(g);
		if (_off_hook) {
			g.setColor(telephone.plug);
			g.fillOval(0, 40, 60, 60);
			g.setColor(telephone.plug_lt);
			g.drawArc(5, 45, 50, 50, 90, 90);
			g.drawOval(10, 50, 40, 40);
			g.fillOval(20, 60, 20, 20);
		} else {
			g.setColor(telephone.plug);
			g.fillRect(20, 0, 20, 100);
		}
	}

	public StrombergCarlson_Rec(StrombergCarlson_Shelf shelf) {
		_off_hook = false;
		_shelf = shelf;
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
		addMouseListener(this);
	}

	public boolean offHook() { return _off_hook; }

	public void mouseClicked(MouseEvent e) {
		_off_hook = !_off_hook;
		_shelf.goOffHook(_off_hook);
		repaint();
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}

class StrombergCarlson_Shelf extends JPanel
{
	static final long serialVersionUID = 311000000010L;
	public static final int obj_width = 100;
	public static final int obj_height = 50;

	private boolean _off_hook;
	private JEditorPane _text;
	private JScrollPane _scroll;

	public void paint(Graphics g) {
		super.paint(g);
		if (_off_hook) {
			return;
		} else {
			g.setColor(telephone.well_lt);
			g.fillRect(0, 0, obj_width, obj_height);
		}
	}

	public StrombergCarlson_Shelf() {
		_off_hook = false;
		_text = new JEditorPane();
		_text.setEditable(false);
		_text.setFont(telephone.font2);
		_scroll = new JScrollPane(_text);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroll.setPreferredSize(new Dimension(obj_width, obj_height));
		_scroll.setVisible(false);
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
	}

	public void goOffHook(boolean off_hook) {
		if (_off_hook != off_hook) {
			_off_hook = off_hook;
			_scroll.setVisible(off_hook);
			repaint();
		}
	}

	public void post(String s) {
		if (_off_hook) {
			_text.setText(s);
		}
	}
}

class StrombergCarlson_Bell extends JPanel
	implements ActionListener
{
	static final long serialVersionUID = 311000000010L;
	public static final int obj_width = 100;
	public static final int obj_height = 40;

	private boolean _ring;
	private javax.swing.Timer _timer;

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		super.paint(g2d);
		g2d.setColor(telephone.jack_face);
		g2d.fillOval(0, 0, 40, 40);
		g2d.fillOval(45, 10, 10, 20);
		g2d.fillOval(60, 0, 40, 40);
		if (_ring) {
			g2d.setColor(Color.red);
			g2d.setStroke(new BasicStroke((float)5.0,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			g2d.drawOval(3, 3, 35, 35);
			g2d.drawOval(63, 3, 35, 35);
		}
	}

	public StrombergCarlson_Bell() {
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
		_timer = new Timer(1000, this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _timer) {
			_timer.stop();
			_ring = false;
			repaint();
		}
	}

	public void ring() {
		_ring = true;
		_timer.start();
		repaint();
	}
}

class StrombergCarlson_Trans extends JPanel
{
	static final long serialVersionUID = 311000000010L;
	public static final int obj_width = 60;
	public static final int obj_height = 60;

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(telephone.jack_face);
		g.fillOval(0, 0, 60, 60);
		g.setColor(telephone.plug);
		g.fillOval(10, 10, 40, 40);
		g.setColor(telephone.plug_lt);
		g.fillOval(20, 20, 20, 20);
	}

	public StrombergCarlson_Trans() {
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
	}
}
