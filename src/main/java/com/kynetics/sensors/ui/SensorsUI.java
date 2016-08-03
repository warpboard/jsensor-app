/**
 * Copyright 2014 - Kynetics, LLC
 */
package com.kynetics.sensors.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.kynetics.utils.Log;

/**
 * @author Diego Rondini
 *
 */
public class SensorsUI extends JFrame {

	public SensorsUI() {
		initUI();
	}

	public void updateValue(final SensorType sensorType, final String value) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateValue(sensorType, value);
				}
			});
		}
		String print = " : " + value;
		switch (sensorType) {
		case PRESSURE:
			pressureValue.setText(print);
			break;
		case TEMPERATURE:
			temperatureValue.setText(print);
			break;
		case HUMIDITY:
			humidityValue.setText(print);
			break;
		case AMBIENT_LIGHT:
			ambientLightValue.setText(print);
			break;
		default:
			break;
		}
	}

	private void initUI() {
		// Get pane
		Container pane = getContentPane();
		setFont();
		pane.add(createSensorsPanel(), BorderLayout.CENTER);
		pane.add(createLogoPanel(), BorderLayout.SOUTH);

		setTitle("Sensors");
		setExtendedState(MAXIMIZED_BOTH);
		//setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void setFont() {
		UIManager.put("Label.font", liberationFontRes);
	}

	private void createLabels() {
		pressureLabel = new JLabel("Pressure", SwingConstants.RIGHT);
		pressureLabel.setForeground(Color.PINK);
		temperatureLabel = new JLabel("Temperature", SwingConstants.RIGHT);
		temperatureLabel.setForeground(Color.PINK);
		humidityLabel = new JLabel("Humidity", SwingConstants.RIGHT);
		humidityLabel.setForeground(Color.PINK);
		ambientLightLabel = new JLabel("Ambient Light", SwingConstants.RIGHT);
		ambientLightLabel.setForeground(Color.PINK);

		pressureValue = new JLabel(" : 0");
		pressureValue.setForeground(Color.GREEN);
		temperatureValue = new JLabel(" : 0");
		temperatureValue.setForeground(Color.GREEN);
		humidityValue = new JLabel(" : 0");
		humidityValue.setForeground(Color.GREEN);
		ambientLightValue = new JLabel(" : 0");
		ambientLightValue.setForeground(Color.GREEN);
	}

	private JPanel createSensorsPanel() {
		GridLayout gridLayout = new GridLayout(4, 2);
		JPanel sensorsPanel = new JPanel();
		createLabels();
		sensorsPanel.setLayout(gridLayout);
		sensorsPanel.add(pressureLabel, 0);
		sensorsPanel.add(pressureValue, 1);
		sensorsPanel.add(temperatureLabel, 2);
		sensorsPanel.add(temperatureValue, 3);
		sensorsPanel.add(humidityLabel, 4);
		sensorsPanel.add(humidityValue, 5);
		sensorsPanel.add(ambientLightLabel, 6);
		sensorsPanel.add(ambientLightValue, 7);
		sensorsPanel.setBackground(Color.BLACK);
		return sensorsPanel;
	}

	private JPanel createLogoPanel() {
		GridLayout javaLogoLayout = new GridLayout(1, 1);
		JPanel javaLogoPanel = new JPanel();
		javaLogoPanel.setLayout(javaLogoLayout);
		javaLogoPanel.setPreferredSize(new Dimension(240, 60));
		JLabel javaLogoLabel = new JLabel("Logo not found");
		try {
			BufferedImage javaLogo = ImageIO.read(this.getClass().getResource("/java-logo.jpg"));
			javaLogoLabel = new JLabel(new ImageIcon(javaLogo));
		} catch (IOException | RuntimeException e1) {
			Log.e(TAG, e1.getMessage());
		}
		javaLogoPanel.add(javaLogoLabel);
		javaLogoPanel.setBackground(Color.BLACK);
		return javaLogoPanel;

	}

	private JLabel pressureLabel;
	private JLabel temperatureLabel;
	private JLabel humidityLabel;
	private JLabel ambientLightLabel;
	private JLabel pressureValue;
	private JLabel temperatureValue;
	private JLabel humidityValue;
	private JLabel ambientLightValue;

	private static final String TAG = SensorsUI.class.getSimpleName();
	private static final FontUIResource liberationFontRes = new FontUIResource("Liberation", Font.PLAIN, 17);
}
