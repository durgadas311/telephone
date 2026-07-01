// Copyright (c) 2011,2026 Douglas Miller <durgadas311@gmail.com>

import java.util.Properties;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.sound.sampled.*;

class Kellogg_NightAlarm extends JPanel
	implements Kellogg_SwListener
{
	public static final int obj_width = Kellogg_Circuit.obj_width;
	public static final int obj_height = Kellogg_Circuit.obj_height;

	private boolean _alarm;
	private Clip _ringer;

	private Kellogg_StSpSw _na_sw;
	private Kellogg_StSpSw _cd_sw;

	private class Alarmer {
		private int _count;

		public Alarmer() {
			_count = 0;
		}

		//public void reset() { _count = 0; }

		public boolean count() { return (_count != 0); }

		public void alarmer(boolean on) {
			if (on) {
				++_count;
			} else {
				--_count;
			}
		}
	}

	private Alarmer _const_drop;
	private Alarmer _coded_drop;
	private Alarmer _coded_ring;

	public void paint(Graphics g) {
		super.paint(g);
//		String s = Integer.toString(_alarming);
//		g.drawString(s, 10, 90);
		if (_alarm) {
			g.setColor(Color.red);
			g.fillOval(10, 10, 55, 55);
		}
	}

	public Kellogg_NightAlarm(Properties props) {
		_alarm = false;
		_const_drop = new Alarmer();
		_coded_drop = new Alarmer();
		_coded_ring = new Alarmer();

		_na_sw = new Kellogg_StSpSw(this, "N.A.");
		_cd_sw = new Kellogg_StSpSw(this, "Const.");
		_na_sw.setState(false);
		_cd_sw.setState(false);

		GridBagLayout gb = new GridBagLayout();
		setLayout(gb);
		GridBagConstraints s = new GridBagConstraints();
		s.fill = GridBagConstraints.NONE;
		s.gridx = 0;
		s.gridy = 0;
		s.weightx = 0;
		s.weighty = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		s.anchor = GridBagConstraints.CENTER;

		JPanel pan = new JPanel();
		pan.setPreferredSize(new Dimension(obj_width,
					10 + 2 * Kellogg_Plug.obj_height));
		pan.setOpaque(false);
		s.gridx = 0;
		s.gridy = 0;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(pan, s);
		add(pan);

		s.gridx = 0;
		s.gridy = 1;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(_na_sw, s);
		add(_na_sw);

		pan = new JPanel();
		pan.setOpaque(false);
		pan.setPreferredSize(new Dimension(obj_width, 10));
		s.gridx = 0;
		s.gridy = 2;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(pan, s);
		add(pan);

		s.gridx = 0;
		s.gridy = 3;
		s.gridwidth = 1;
		s.gridheight = 1;
		gb.setConstraints(_cd_sw, s);
		add(_cd_sw);

		setPreferredSize(new Dimension(obj_width, obj_height));
		setOpaque(false);

		// sometimes, this fails because of exclusive audio
		// access. not sure how to share, but need to disable
		// audio in that case.
		try {
			AudioInputStream wav =
				AudioSystem.getAudioInputStream(
					new BufferedInputStream(
						switchboard.class.getResourceAsStream(
							"sounds/ring.wav")));
			AudioFormat format = wav.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			_ringer = (Clip)AudioSystem.getLine(info);
			_ringer.open(wav);
			_ringer.setLoopPoints(0, 4500);
			int volume = 50;
			String p = props.getProperty("nightalarm_volume");
			if (p != null) try {
				volume = Integer.valueOf(p);
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
		}
	}

	private void alarmChanged() {
		boolean alarming;
		if (_na_sw.getState()) {
			alarming = (_const_drop.count() ||
				_cd_sw.getState() && _coded_drop.count() ||
				!_cd_sw.getState() && _coded_ring.count());
		} else {
			alarming = false;
		}
		if (alarming != _alarm) {
			_alarm = alarming;
			if (_alarm) {
				_alarm = true;
				_ringer.setFramePosition(0);
				_ringer.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				_alarm = false;
				_ringer.loop(0);
			}
			repaint();
		}
	}

	public void listener(JComponent sw, boolean state) {
		alarmChanged();
	}

	public void alarmDrop(boolean coded, boolean on) {
		if (coded) {
			_coded_drop.alarmer(on);
		} else {
			_const_drop.alarmer(on);
		}
		alarmChanged();
	}

	public void alarmRing(boolean on) {
		_coded_ring.alarmer(on);
		alarmChanged();
	}
}
