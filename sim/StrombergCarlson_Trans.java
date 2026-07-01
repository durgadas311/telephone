// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;

class StrombergCarlson_Trans extends JPanel
{
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
