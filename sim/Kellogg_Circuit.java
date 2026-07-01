// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.awt.*;
import javax.swing.*;

class Kellogg_Circuit extends JPanel
{
	public static final int obj_width = 75;
	public static final int obj_height = 10 + 10 +
		2 * Kellogg_Plug.obj_height +
		Kellogg_RingSw.obj_height +
		Kellogg_StSpSw.obj_height;

	private JPanel _parent;
	private Kellogg_StSpSw _listen;
	private Kellogg_RingSw _ring;
	private Kellogg_Plug _call;
	private Kellogg_Plug _ans;
	private Kellogg_Drop _drop_call;
	private Kellogg_Drop _drop_ans;
	private int _num;

	// R.O.Drop  [upper](originator attn signal)
	// R.O.Drop  [lower](target attn signal)
	// Answer    [far]  (operator answers originator)
	// Call      [near] (operator calls target)
	// Ring-back [push] (operator rings originator)
	// Ring      [pull] (operator rings target)
	// Listen    [push] (operator taps-in to circuit)
	public Kellogg_Circuit(JPanel parent, int num, Kellogg_Cabinet cab,
			Kellogg_Drop drp_up, Kellogg_Drop drp_lo,
			Component top) {
		_num = num;
		_parent = parent;
		_drop_ans = drp_up;
		_drop_call = drp_lo;
		_listen = new Kellogg_StSpSw(cab, "Listen");
		_ring = new Kellogg_RingSw(top, cab);
		_call = new Kellogg_Plug(this, cab, _drop_call,
						Kellogg_RingSw.PULL, "Call");
		_ans = new Kellogg_Plug(this, cab, _drop_ans,
						Kellogg_RingSw.PUSH, "Answer");

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
		gridbag.setConstraints(_ans, s);
		add(_ans);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_call, s);
		add(_call);

		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(obj_width, 10));
		pan.setOpaque(false);
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);
		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_ring, s);
		add(_ring);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(obj_width, 10));
		s.gridx = 0;
		s.gridy = 4;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(pan, s);
		add(pan);
		s.gridx = 0;
		s.gridy = 5;
		s.gridwidth = 1;
		s.gridheight = 1;
		gridbag.setConstraints(_listen, s);
		add(_listen);

		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);
		setForeground(Color.black);
		setFont(switchboard.font);
	}

	public Point getLocation() {
		Point p = _parent.getLocation();
		Point m = super.getLocation();
		p.x += m.x;
		p.y += m.y;
		return p;
	}

	public int ringSw() {
		return _ring.getState();
	}

	public boolean listenSw() {
		return _listen.getState();
	}

	public int getId() {
		return _num;
	}

	public void post(Kellogg_Plug plug, String s) {
		if (plug == _call) _ans.postDown(s);
		else if (plug == _ans) _call.postDown(s);
	}
}
