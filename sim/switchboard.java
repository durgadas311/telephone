// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.2 2012/02/05 04:30:27 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.2 2012/02/05 04:30:27 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);
	static final Color drop = new Color(150, 150, 150);
	static final Color drop_lt = new Color(170, 170, 170);
	static final Color drop_dk = new Color(130, 130, 130);
	static final Color edge = new Color(130, 130, 130);
	static final Color edge_lt = new Color(150, 150, 150);
	static final Color edge_dk = new Color(110, 110, 110);
	static final Color jack_bev = new Color(180, 180, 180);
	static final Color jack_face = new Color(240, 240, 240);
	static final Color jack_well = new Color(150, 150, 150);
	static final Color jack_hole = new Color(20, 20, 20);

	static final Font font = new Font("Serif", Font.PLAIN, 18);

	public static void main(String[] args) {
		GridBagLayout gridbag = new GridBagLayout();

		JFrame front_end = new JFrame("Kellogg 1915 Telephone Switchboard");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);
		FontMetrics font_metrics = front_end.getFontMetrics(font);

		front_end.setLayout(gridbag);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		int num_lines = 5;
		int lines_per_row = 10;

		int x;
		Kellogg_Line li;
		for (x = 0; x < num_lines; ++x) {
			li = new Kellogg_Line(x + 1, font_metrics);
			s.gridx = x % lines_per_row;
			s.gridy = x / lines_per_row;
			s.gridwidth = 1;
			s.gridheight = 1;
			gridbag.setConstraints(li, s);
			front_end.add(li);
		}

		front_end.getContentPane().setBackground(cabinet);
		front_end.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		front_end.setSize(1024,640);
		front_end.pack();
		front_end.setVisible(true);
	}
}

class Kellogg_Line extends JPanel
	implements MouseListener
{
	final String ident = "$Id: switchboard.java,v 1.2 2012/02/05 04:30:27 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;

	static final int[] hex_x = { 20, 40, 50, 40, 20, 10, 20 };
	static final int[] hex_y = { 60, 60, 75, 90, 90, 75, 60 };

	static final int[] shutter_x = { 40, 50, 50, 10, 10, 20, 40 };
	static final int[] shutter_y = { 10, 20, 50, 50, 20, 10, 10 };

	static final int[] shutter_dn_x = { 40, 53, 50, 10,  7, 20, 40 };
	static final int[] shutter_dn_y = { 38, 41, 50, 50, 41, 38, 38 };

	private int _num;
	private String _tag;
	private int _tag_x;
	private boolean _dropped;

	private void drawDrop(Graphics g, boolean dropped) {
		if (dropped) {
			g.setColor(switchboard.jack_bev);
			g.fillPolygon(shutter_x, shutter_y, 6);

			g.setColor(switchboard.edge);
			g.fillPolygon(shutter_dn_x, shutter_dn_y, 6);
			g.setColor(switchboard.edge_dk);
			g.drawPolyline(shutter_dn_x, shutter_dn_y, 2);
			g.setColor(switchboard.edge_lt);
			g.drawPolyline(Arrays.copyOfRange(shutter_dn_x, 4, 7),
					Arrays.copyOfRange(shutter_dn_y, 4, 7), 3);
		} else {
			g.setColor(switchboard.drop);
			g.fillPolygon(shutter_x, shutter_y, 6);
			g.setColor(switchboard.drop_dk);
			g.drawPolyline(shutter_x, shutter_y, 4);
			g.setColor(switchboard.drop_lt);
			g.drawPolyline(Arrays.copyOfRange(shutter_x, 3, 7),
					Arrays.copyOfRange(shutter_y, 3, 7), 4);
			g.setColor(Color.red);
			g.drawString(_tag, _tag_x, 40);
		}
	}

	private void drawJack(Graphics g, boolean plug) {
		g.setColor(switchboard.jack_bev);
		g.fillPolygon(hex_x, hex_y, 6);
		g.setColor(switchboard.jack_face);
		g.fillOval(15, 60, 30, 30);
		g.setColor(switchboard.jack_well);
		g.fillOval(20, 65, 20, 20);
		g.setColor(switchboard.jack_hole);
		if (plug) g.setColor(Color.yellow);
		g.fillOval(22, 67, 16, 16);
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawDrop(g, _dropped);
		drawJack(g, false);
	}

	public Kellogg_Line(int num, FontMetrics font_metrics) {
		_num = num;
		_tag = Integer.toString(_num);
		int w = font_metrics.stringWidth(_tag);
		_tag_x = 30 - (w / 2);
		_dropped = false;
		setPreferredSize(new Dimension(60, 100));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		_dropped = !_dropped;
		repaint();
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

}
