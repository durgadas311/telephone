// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.util.Properties;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.*;

class StrombergCarlson_Bell extends JPanel
	implements ActionListener
{
	public static final int obj_width = 100;
	public static final int obj_height = 40;

	private boolean _ring;
	private Clip _ringer;

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

	public StrombergCarlson_Bell(Properties props) {
		setOpaque(false);
		setPreferredSize(new Dimension(obj_width, obj_height));
		// sometimes, this fails because of exclusive audio
		// access. not sure how to share, but need to disable
		// audio in that case.
		try {
			AudioInputStream wav =
				AudioSystem.getAudioInputStream(
					new BufferedInputStream(
						telephone.class.getResourceAsStream(
							"sounds/ring.wav")));
			AudioFormat format = wav.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			_ringer = (Clip)AudioSystem.getLine(info);
			_ringer.open(wav);
			_ringer.setLoopPoints(0, 4500);
			int volume = 50;
			String s = props.getProperty("ringer_volume");
			if (s != null) try {
				volume = Integer.valueOf(s);
				if (volume < 0) volume = 0;
				if (volume > 100) volume = 100;
			} catch (Exception e) { }
			FloatControl vol = null;
			if (_ringer.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				vol = (FloatControl)_ringer.getControl(FloatControl.Type.MASTER_GAIN);
			} else if (_ringer.isControlSupported(FloatControl.Type.VOLUME)) {
				vol = (FloatControl)_ringer.getControl(FloatControl.Type.VOLUME);
			}
			if (vol != null) {
				float min = vol.getMinimum();
				float max = vol.getMaximum();
				float gain = (float)(min + ((max - min) * (volume / 100.0)));
				vol.setValue(gain);
			}

		} catch (Exception e) {
			_ringer = null;
//e.printStackTrace();
//System.exit(1);
		}
//System.err.println("Frames="+_ringer.getFrameLength()+", time="+_ringer.getMicrosecondLength());
// frame_loop = ((double)102000.0 / _ringer.getMicrosecondLength()) * _ringer.getFrameLength();
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void ring(boolean on) {
		if (_ringer != null && _ring != on) {
			if (on) {
				_ringer.setFramePosition(0);
				_ringer.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				_ringer.loop(0);
			}
		}
		_ring = on;
		repaint();
	}
}
