// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

class Kellogg_Line extends JPanel
	implements MouseListener, Runnable
{
	public static final int obj_width = 60;
	public static final int obj_height = 40;

	static final int[] hex_top_x = { 20, 40, 46, 14, 10, 20 };
	static final int[] hex_top_y = {  0,  0,  9, 21, 15,  0 };

	static final int[] hex_bot_x = { 46, 50, 40, 20, 14, 46 };
	static final int[] hex_bot_y = {  9, 15, 31, 31, 21,  9 };

	static final Point _center = new Point(30, 15);

	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Cabinet _cab;
	private Kellogg_Plug _conn_plug;
	private int _num;

	private Socket _subscriber;
	private Thread _thr;
	private BufferedReader _in;
	private OutputStream _out;
	private String _name;
	private int _type;

	public void paint(Graphics g) {
		super.paint(g);
		if (_conn_plug != null) {
			g.setColor(switchboard.plug);
			g.fillOval(14,  0, 32, 30);
			g.setColor(switchboard.plug_lt);
			g.drawArc(16,  2, 28, 26, 110, 50);
			g.setColor(switchboard.cord);
			g.fillOval(23,  8, 14, 14);
		} else {
			g.setColor(switchboard.jack_lt);
			g.fillPolygon(hex_top_x, hex_top_y, 5);
			g.setColor(switchboard.jack_dk);
			g.fillPolygon(hex_bot_x, hex_bot_y, 5);
			g.setColor(switchboard.jack_face);
			g.fillOval(14,  0, 32, 30);
			g.setColor(switchboard.jack_lt);
			g.fillArc(20,  5, 20, 20, -155, 180);
			g.setColor(switchboard.jack_dk);
			g.fillArc(20,  5, 20, 20, 25, 180);
			g.setColor(switchboard.jack_hole);
			if (_conn_plug != null) g.setColor(Color.yellow);
			g.fillOval(22,  7, 16, 16);
		}
	}

	public Kellogg_Line(JPanel parent, Kellogg_Cabinet cab,
			int num, Kellogg_Drop drop) {
		_parent = parent;
		_cab = cab;
		_drop = drop;
		_conn_plug = null;
		_num = num;
		_name = "Unknown";
		_type = switchboard.UNKNOWN;
		setPreferredSize(new Dimension(60, 40));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (_conn_plug != null) {
			_cab.disconnect(this);
		} else {
			_cab.connectLine(this); // or try at least...
		}
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	public int getType() { return _type; }

	public Point getCoords() {
		Point p = _parent.getLocation();
		Point m = getLocation();
		p.x += m.x;
		p.y += m.y;
		p.x += _center.x;
		p.y += _center.y;
		return p;
	}

	public void setPlugged(Kellogg_Plug plug) {
		if (plug != _conn_plug) {
			_conn_plug = plug;
			if (plug != null) {
				_drop.setDropped(false);
			}
			repaint();
		}
	}

	public void ring(boolean on) {
		if (_conn_plug != null && _conn_plug.isRingable()) {
			if (_subscriber != null) {
				// TBD: transmit RING to remote phone...
				try {
					if (on) _out.write("%RING\n".getBytes());
					else _out.write("%RINGOFF\n".getBytes());
				} catch(Exception e) { }
			} else {
				System.err.println("remote "+_num+": RING "+on);
			}
		}
	}

	public boolean isSubscribed() { return (_subscriber != null); }

	public boolean subscribe(Socket s) {
		if (_subscriber != null) return false;
		_subscriber = s;
		try {
			_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			_out = s.getOutputStream();
			String t = new String("%TYPE=switchboard\n");
			_out.write(t.getBytes());
			t = new String("%NAME=" + Integer.toString(_num) + "\n");
			_out.write(t.getBytes());
		} catch(Exception e) {
		}
		_thr = new Thread(this);
		_thr.start();
		return true;
	}

	public int getId() { return _num; }
	public String getName() { return _name; }

	public void speak(String s) {
		try {
			_out.write(s.getBytes());
		} catch(Exception e) {
		}
	}

	private boolean doCommand(String s) {
		if (s.equals("%RING")) {
			if (_conn_plug != null) {
				_conn_plug.ring(true);
			} else {
				_drop.alarmRing(true); // also sets Drop
			}
		} else if (s.equals("%RINGOFF")) {
			if (_conn_plug != null) {
				_conn_plug.ring(false);
			} else {
				_drop.alarmRing(false);
			}
		} else if (s.startsWith("%TYPE=")) {
			s = s.substring(6);
			if (s.startsWith("tele")) {
				_type = switchboard.TELEPHONE;
			} else if (s.startsWith("swit")) {
				_type = switchboard.SWITCHBOARD;
				// possibly move to different line,
				// if using toll lines...
			} else {
				// drop connection and free line...
				return false;
			}
		} else if (s.startsWith("%NAME=")) {
			InetSocketAddress sa = (InetSocketAddress)_subscriber.getRemoteSocketAddress();
			_name = s.substring(6) + "@" + sa.getHostName();
		}
		return true;
	}

	public void run() {
		while (true) {
			String s = null;
			try {
				s = _in.readLine();
			} catch(Exception e) { }
			if (s == null) break;
			if (s.startsWith("%")) {
				if (!doCommand(s)) break;
			} else {
				_cab.post(this, s);
			}
		}
		try {
			_in.close();
			_out.close();
			_subscriber.close();
		} catch(Exception e) { }
		_subscriber = null;
		if (_conn_plug != null) {
			_conn_plug.ring(true); // trigger the drop...
			_conn_plug.ring(false);
		}
	}
}
