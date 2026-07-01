// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;

class StrombergCarlson_Shelf extends JPanel
{
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
