/**
 * Copyright 2014-2016 - Kynetics, LLC
 */
package com.kynetics.sensors;

import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kynetics.sensors.serial.SerialCommunication;
import com.kynetics.sensors.ui.SensorsUI;
import com.kynetics.utils.Log;

/**
 * @author Diego Rondini, Nicola La Gloria
 *
 */
public class SensorsApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			final String devicePath = args[0];
			Log.i(TAG, "Serial port specified : " + devicePath);
			try {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						final SensorsUI sensorsUi = new SensorsUI();
						sensorsUi.setVisible(true);
						exService.submit(new Runnable() {
							
							@Override
							public void run() {
								try {
									SerialCommunication.getInstance().connect(devicePath, sensorsUi);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						});
					}
					
					private ExecutorService exService = Executors.newSingleThreadExecutor();
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Only one parameter, serial port path, should be specified");
		}

	}

	private static final String TAG = SensorsApp.class.getSimpleName();

}
