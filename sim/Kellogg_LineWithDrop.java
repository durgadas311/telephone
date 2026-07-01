// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;

class Kellogg_LineWithDrop extends JPanel
{
	public static final int obj_width = 60;
	public static final int obj_height =
			Kellogg_Line.obj_height + Kellogg_Drop.obj_height;

	private JPanel _parent;
	private Kellogg_Drop _drop;
	private Kellogg_Line _line;

	public Kellogg_LineWithDrop(JPanel parent, int num,
			Kellogg_Cabinet cab) {
		_parent = parent;
		_drop = new Kellogg_Drop(cab, num, 60, true);
		_line = new Kellogg_Line(this, cab, num, _drop);

		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_drop, s);
		add(_drop);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_line, s);
		add(_line);

		setPreferredSize(new Dimension(60, 100));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}

	public Kellogg_Line getLine() { return _line; }

	public Point getLocation() {
		Point m = super.getLocation();
		Point p = _parent.getLocation();
		p.x += m.x;
		p.y += m.y;
		return p;
	}
}
