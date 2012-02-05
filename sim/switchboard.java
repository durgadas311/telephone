// Copyright (c) 2011,2012 Douglas Miller
// $Id: switchboard.java,v 1.1 2012/02/05 02:22:54 drmiller Exp $

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class switchboard
{
	final String ident = "$Id: switchboard.java,v 1.1 2012/02/05 02:22:54 drmiller Exp $";

	static final Color cabinet = new Color(165, 125, 14);
	static final Color drop = new Color(150, 150, 150);
	static final Color edge = new Color(100, 100, 100);
	static final Color jack = new Color(200, 200, 200);
	static final Color hole = new Color(20, 20, 20);

	public static void main(String[] args) {
		GridBagLayout gridbag = new GridBagLayout();

		JFrame front_end = new JFrame("Kellogg 1915 Telephone Switchboard");
		//java.net.URL url = w600_fe.class.getResource("icons/wang600-48x48.png");
		//Image img = Toolkit.getDefaultToolkit().getImage(url);
		//front_end.setIconImage(img);

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
			li = new Kellogg_Line(x + 1);
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
	final String ident = "$Id: switchboard.java,v 1.1 2012/02/05 02:22:54 drmiller Exp $";
	static final long serialVersionUID = 311000000002L;

	static final Font _font = new Font("Serif", Font.PLAIN, 18);

	private int _num;
	private String _tag;
	private boolean _dropped;

	private void drawDrop(Graphics g, boolean dropped) {
		if (dropped) {
			g.setColor(switchboard.edge);
			g.fillRect(10, 40, 40, 10);
		} else {
			g.setColor(switchboard.drop);
			g.fillRect(10, 10, 40, 40);
			g.setColor(Color.red);
			g.drawString(_tag, 20, 40);
		}
	}

	private void drawJack(Graphics g, boolean plug) {
		g.setColor(switchboard.jack);
		if (plug) g.setColor(Color.yellow);
		g.fillOval(15, 55, 30, 30);
		g.setColor(switchboard.hole);
		g.fillOval(20, 60, 20, 20);
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawDrop(g, _dropped);
		drawJack(g, false);
	}

	public Kellogg_Line(int num) {
		_num = num;
		_tag = Integer.toString(_num);
		_dropped = false;
		setPreferredSize(new Dimension(60, 100));
		setOpaque(false);
		setForeground(Color.black);
		setFont(_font);
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
