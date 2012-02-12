// Copyright (c) 2011,2012 Douglas Miller
// $Id: telephone.java,v 1.5 2012/02/12 16:06:26 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.net.*;
import java.io.*;

public class telephone
{
	final String ident = "$Id: telephone.java,v 1.5 2012/02/12 16:06:26 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);
	static final Color cabinet_lt = new Color(185, 145, 34);
	static final Color cabinet_dk = new Color(155, 115, 4);

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
	static final Color plug_sun = new Color(80, 80, 80);
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

		front_end.addKeyListener(cab);
		front_end.getContentPane().setBackground(Color.gray);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}
}

class StrombergCarlson_Cabinet extends JPanel
		implements Runnable, KeyListener
{
	static final long serialVersionUID = 311000000010L;

	public static final int border_size = 2;
	public static final int text_height = 100;

	public static FontMetrics font_metrics;

	private StrombergCarlson_Rec _rec;
	private StrombergCarlson_Bell _bell;
	private StrombergCarlson_Shelf _shelf;
	private StrombergCarlson_NamePlate _plate;
	private Component _top;

	private Socket _s;
	private InputStream _in;
	private OutputStream _out;
	private byte[] _buf;

	private boolean _off_hook;
	private String _txt;
	private JTextArea _text;
	private JScrollPane _scroll;
	private int _off_hook_h;
	private int _on_hook_h;

	public StrombergCarlson_Cabinet(Socket so,
			Component top,
			FontMetrics fm) {
		font_metrics = fm;
		_top = top;
		_s = so;
		try {
			_in = so.getInputStream();
			_out = so.getOutputStream();
		} catch (IOException ee) {
		}
		if (top == null) return; // damn warnings

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		setOpaque(false);
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
		Dimension dim;
		dim = new Dimension(140 +
				2 * border_size,
				315);
//				StrombergCarlson_Shelf.obj_width +
//				2 * border_size,
//				StrombergCarlson_Bell.obj_height +
//				StrombergCarlson_Trans.obj_height +
//				StrombergCarlson_Shelf.obj_height +
//				10 + 30 + 40 + 10 + 2 *border_size);
		upper.setPreferredSize(dim);
		int row = 0;
		int cab_gh = 3;

		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 5));
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		_bell = new StrombergCarlson_Bell();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.NORTH;
		ugb.setConstraints(_bell, s);
		upper.add(_bell);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 40));
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(pan, s);
		upper.add(pan);

		StrombergCarlson_Trans tran = new StrombergCarlson_Trans();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(tran, s);
		upper.add(tran);

		_plate = new StrombergCarlson_NamePlate();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		ugb.setConstraints(_plate, s);
		upper.add(_plate);

		_shelf = new StrombergCarlson_Shelf();
		s.gridx = 0;
		s.gridy = row++;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.SOUTH;
		ugb.setConstraints(_shelf, s);
		upper.add(_shelf);

		s.gridx = 1;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = cab_gh;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(upper, s);
		add(upper);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		_rec = new StrombergCarlson_Rec(this);
		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 2;
		s.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(_rec, s);
		add(_rec);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 20));
		s.gridx = 2;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(20, 30));
		s.gridx = 2;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pan, s);
		add(pan);

		StrombergCarlson_Magneto mag = new StrombergCarlson_Magneto(this);
		s.gridx = 2;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(mag, s);
		add(mag);

		Dimension dim2 = new Dimension(dim);
		dim2.width += StrombergCarlson_Rec.obj_width +
				StrombergCarlson_Magneto.obj_width;
		setPreferredSize(dim2);
		_on_hook_h = 0;
		_off_hook_h = 0;

		_off_hook = false;
		_txt = new String();
		_text = new JTextArea();
		_text.setEditable(false);
		_text.setFocusable(false);
		_text.setFont(telephone.font2);
		_text.setBackground(Color.white);
		_text.setLineWrap(true);
		_text.setWrapStyleWord(true);
		_text.getCaret().setVisible(true);
		_text.setCaretColor(telephone.cabinet);
		//_text.setText("Chat Here");
		_scroll = new JScrollPane(_text);
		_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		_scroll.setPreferredSize(new Dimension(dim2.width, text_height));
		_scroll.setFocusable(false);
		_scroll.setVisible(false);
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 3;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(_scroll, s);
		add(_scroll);

		_buf = new byte[128];

		Thread t = new Thread(this);
		t.start();
	}

	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		type(c);
		if (c == KeyEvent.VK_ENTER) {
			try {
				_out.write(typed().getBytes());
			} catch (IOException ee) {
			}
		}
	}

	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }

	public void ring(boolean on) {
		if (!on) return;
		try {
			_out.write("%RING\n".getBytes());
		} catch (IOException ee) {
		}
		// _bell.ring(); // ring locally, too?
	}

	public void goOffHook(boolean off_hook) {
		if (_off_hook != off_hook) {
			_off_hook = off_hook;
			Dimension dim = _top.getSize();
			if (_off_hook_h == 0) {
				_on_hook_h = dim.height;
				_off_hook_h = _on_hook_h + text_height;
			}
			if (_off_hook) {
				if (dim.height < _off_hook_h) dim.height = _off_hook_h;
			} else {
				if (dim.height == _off_hook_h) dim.height = _on_hook_h;
			}
			_top.setSize(dim);
			_scroll.setVisible(_off_hook);
			repaint();
		}
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
			} else if (s.startsWith("%NAME=")) {
				_plate.setName(s.substring(6));
			} else {
				post(s);
			}
		}
		try {
			_in.close();
			_out.close();
			_s.close();
		} catch(IOException e) { }
	}

	public void post(String s) {
		if (!_off_hook) return;
		_text.append(">> " + s);
		_text.setCaretPosition(_text.getText().length());
	}

	public void type(char c) {
		if (!_off_hook) return;
		String s = Character.toString(c);
		_text.append(s);
		_txt += s;
	}

	public String typed() {
		if (!_off_hook) return null;
		String s = _txt;
		_txt = new String();
		return s;
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

class StrombergCarlson_Shelf extends JPanel
{
	static final long serialVersionUID = 311000000010L;
	public static final int obj_width = 130;
	public static final int obj_height = 100;

	private static final int[] shelf_x = { 5, 125, 130,  0, 5 };
	private static final int[] shelf_y = { 0,   0,  35, 35, 0 };

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(telephone.cabinet);
		g.fillRect(0, 0, obj_width, obj_height);
		g.setColor(telephone.well_lt);
		g.fillPolygon(shelf_x, shelf_y, 4);
		g.setColor(telephone.well_dk);
		g.fillRect(60, 35, 10, 35);
		g.setColor(telephone.cabinet_dk);
		g.fillRoundRect(0, 35, 130, 10, 4, 4);
		g.drawLine(shelf_x[1], shelf_y[1], shelf_x[2] - 1, shelf_y[2]);
		g.setColor(telephone.cabinet_lt);
		g.drawLine(shelf_x[3], shelf_y[3], shelf_x[4], shelf_y[4]);
	}

	public StrombergCarlson_Shelf() {
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
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
		g2d.fillOval(4, 0, 40, 40);
		g2d.fillOval(45, 10, 10, 20);
		g2d.fillOval(56, 0, 40, 40);
		g2d.setColor(telephone.jack_lt);
		g2d.drawArc(14, 10, 20, 20, 112, 45);
		g2d.drawArc(66, 10, 20, 20, 112, 45);
		g2d.setColor(Color.white);
		g2d.fillOval(16, 12, 16, 16);
		g2d.fillOval(68, 12, 16, 16);
		g2d.drawArc(47, 12, 6, 16, 90, 45);
		g2d.setColor(telephone.jack_dk);
		g2d.fillOval(21, 17, 6, 6);
		g2d.fillOval(73, 17, 6, 6);
		if (_ring) {
			g2d.setColor(Color.red);
			g2d.setStroke(new BasicStroke((float)5.0,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			g2d.drawOval(7, 3, 35, 35);
			g2d.drawOval(59, 3, 35, 35);
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
		g.fillOval(8, 8, 44, 44);
		g.setColor(telephone.plug_sun);
		g.fillArc(11, 11, 38, 38, 292, 45);
		g.setColor(telephone.plug_lt);
		g.fillOval(23, 23, 14, 14);
		g.drawArc(9, 9, 42, 42, 112, 45);
		g.drawArc(9, 9, 42, 42, 292, 45);
	}

	public StrombergCarlson_Trans() {
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
	}
}

class StrombergCarlson_NamePlate extends JPanel
{
	static final long serialVersionUID = 311000000010L;
	public static final int obj_width = 100;
	public static final int obj_height = 65;

	private static final int[] plate_x = { 20, 80, 80, 20, 20 };
	private static final int[] plate_y = { 20, 20, 40, 40, 20 };

	private String _tag;
	private int _tag_x;
	private int _tag_y;

	public void paint(Graphics g) {
		super.paint(g);
		if (_tag != null) {
			g.setColor(telephone.well_lt);
			g.drawLine(plate_x[0], plate_y[0], plate_x[1], plate_y[1]);
			g.drawLine(plate_x[3], plate_y[3], plate_x[4], plate_y[4]);
			g.setColor(telephone.well_dk);
			g.drawLine(plate_x[1], plate_y[1], plate_x[2], plate_y[2]);
			g.drawLine(plate_x[2], plate_y[2], plate_x[3], plate_y[3]);
			g.drawString(_tag, _tag_x, _tag_y);
		}
	}

	public StrombergCarlson_NamePlate() {
		_tag = null;
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
		setFont(telephone.font);
	}

	public void setName(String s) {
		_tag = s.replaceAll("\n", "");
		int w = StrombergCarlson_Cabinet.font_metrics.stringWidth(_tag);
		_tag_x = (obj_width - w) / 2;
		_tag_y = plate_y[2] - StrombergCarlson_Cabinet.font_metrics.getDescent();
		repaint();
	}
}
