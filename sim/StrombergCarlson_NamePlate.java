// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;

class StrombergCarlson_NamePlate extends JPanel
{
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
