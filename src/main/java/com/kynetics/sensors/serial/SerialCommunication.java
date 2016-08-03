/**
 * Copyright 2014 - Kynetics, LLC
 */
package com.kynetics.sensors.serial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.kynetics.sensors.ui.SensorType;
import com.kynetics.sensors.ui.SensorsUI;
import com.kynetics.utils.Log;

/**
 * @author Diego Rondini
 *
 */
public class SerialCommunication {

	public static SerialCommunication getInstance() {
		return INSTANCE;
	}

	public void connect(String portName, SensorsUI sensorsUi) throws Exception {
		this.sensorsUIRef = new WeakReference<SensorsUI>(sensorsUi);
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(SerialCommunication.class.getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				//serialPort.setDTR(true);
				//serialPort.setRTS(true);
				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				consumerExService.submit(new SerialConsumer());
				readExService.submit(new SerialReader(in));
				writeExService.submit(new SerialWriter(out));

			} else {
				Log.e(TAG, "Only serial ports are handled.");
			}
		}
	}

	public void enqueueMessage(String ledSetPoints) {
		try {
			writeSharedQueue.put(ledSetPoints);
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	private class SerialReader implements Runnable {
		private final InputStream in;
		private final BufferedReader br;

		public SerialReader(InputStream in) {
			this.in = in;
			this.br = new BufferedReader(new InputStreamReader(in));
		}

		@Override
		public void run() {
			String lastLine;
			try {
				while (!Thread.currentThread().isInterrupted()) {
					lastLine = br.readLine();
					Log.d(TAG, lastLine);
					readSharedQueue.put(lastLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class SerialConsumer implements Runnable {

		private JsonParser jsonParser = new JsonParser();
		private static final String PRESSURE_NAME = "Pressure";
		private static final String HUMIDITY_NAME = "Humidity";
		private static final String TEMPERATURE_NAME = "Temperature";
		private static final String AMBIENT_LIGHT_NAME = "Ambient_Light";

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					String line = readSharedQueue.take();
					if (line != null && !"".equals(line)) {
						try {
							JsonElement jsonElem = jsonParser.parse(line);
							if (jsonElem.isJsonObject()) {
								JsonObject jsonObj = jsonElem.getAsJsonObject();
								if (sensorsUIRef != null) {
									SensorsUI sensorsUI = sensorsUIRef.get();
									if (sensorsUI != null) {
										if (jsonObj.has(PRESSURE_NAME)) {
											sensorsUI.updateValue(SensorType.PRESSURE, jsonObj.get(PRESSURE_NAME).getAsString());
										} else if (jsonObj.has(TEMPERATURE_NAME)) {
											sensorsUI.updateValue(SensorType.TEMPERATURE, jsonObj.get(TEMPERATURE_NAME).getAsString());
										} else if (jsonObj.has(HUMIDITY_NAME)) {
											sensorsUI.updateValue(SensorType.HUMIDITY, jsonObj.get(HUMIDITY_NAME).getAsString());
										} else if (jsonObj.has(AMBIENT_LIGHT_NAME)) {
											sensorsUI.updateValue(SensorType.AMBIENT_LIGHT, jsonObj.get(AMBIENT_LIGHT_NAME).getAsString());
										}
									}
								}
								if (jsonObj.has(PRESSURE_NAME) || jsonObj.has(TEMPERATURE_NAME) || jsonObj.has(HUMIDITY_NAME) || jsonObj.has(AMBIENT_LIGHT_NAME)) {
									Log.i(TAG, "Valid JSON: " + line);
									//################################
									//SendData.INSTANCE.sendData(line);
									//################################
								}
							}
						} catch (JsonParseException ex) {
							Log.w(TAG, "Invalid JSON: " + line);
							Log.w(TAG, ex.getMessage());
						}
					}
				}
			} catch (InterruptedException e) {
				Log.w(TAG, e.getMessage());
			}
		}

	}

	private class SerialWriter implements Runnable {
		private final OutputStream out;
		private final BufferedWriter bw;

		private SerialWriter(OutputStream out) {
			this.out = out;
			this.bw = new BufferedWriter(new OutputStreamWriter(out));
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					String line = writeSharedQueue.take();
					Log.i(TAG, "Writing to serial:" + line);
					bw.write(line);
					bw.newLine();
					bw.flush();
					Thread.sleep(50);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private SerialCommunication() {
	}

	private WeakReference<SensorsUI> sensorsUIRef;
	private final BlockingQueue<String> readSharedQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<String> writeSharedQueue = new LinkedBlockingQueue<>();
	private static final ExecutorService readExService = Executors.newSingleThreadExecutor();
	private static final ExecutorService consumerExService = Executors.newSingleThreadExecutor();
	private static final ExecutorService writeExService = Executors.newSingleThreadExecutor();
	private static final SerialCommunication INSTANCE = new SerialCommunication();
	private static final String TAG = SerialCommunication.class.getSimpleName();
}
