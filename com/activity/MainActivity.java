package com.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.activity.R;

import algorithm.AccelerationXYZ;
import algorithm.CountDeepV;
import algorithm.DynamicTimeWarping;
import algorithm.StepCycle;
//import algorithm.DynamicTimeWarping;
import algorithm.SensorSupport;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	SensorManager sm = null;
	static WakeLock wakeLock = null;
	
	int algorithmnumber = 4;
	
	Long lastsensortime;
	int sensortesttime = 0;
	Integer stepcount = 0;
	int stepcountbase = -10;
	double horiacce = 0;
	double veracce = 0;
	
	double ax = 0;
	double ay = 0;
	double az = 0;
	double a = 0;
	double gx = 0;
	double gy = 0;
	double gz = 0;
	double g = 0;
	double oy = 0;
	double oz = 0;
	double angle = 0;
	
	File outputfile = null;
	FileOutputStream fos = null;
	Writer out = null;
	
	@SuppressLint("CutPasteId")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		acquireWakeLock();
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.mainactivity);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
		if (algorithmnumber == 4) {
			sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_GAME);
		}	  
		//final TextView tx1 = (TextView) findViewById(R.id.TextView01);
		//SensorSupport.test(this, tx1);
		if (algorithmnumber == 0 || algorithmnumber == 2) {
			DynamicTimeWarping.init();
		} else if (algorithmnumber == 1 || algorithmnumber == 2) {
			CountDeepV.reset((int)(Calendar.getInstance().getTimeInMillis()%100000)/40);
		} else if (algorithmnumber == 3) {
			StepCycle.init();
		}
		final Button startbutton = (Button)this.findViewById(R.id.Start);
		startbutton.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				if (algorithmnumber == 0 || algorithmnumber == 2) {
					DynamicTimeWarping.reset();
				} else if (algorithmnumber == 1 || algorithmnumber == 2) {
					CountDeepV.reset((int)(Calendar.getInstance().getTimeInMillis()%100000)/40);
				} else if (algorithmnumber == 3) {
					StepCycle.reset();
				}
				stepcountbase = -10;
				stepcount = 0;
				if (outputfile == null) {
					Long filename = Calendar.getInstance().getTimeInMillis();
					outputfile = new File(Environment.getExternalStorageDirectory()+"//SensorTest//"+filename.toString()+".csv");
					try {
						outputfile.createNewFile();
						fos = new FileOutputStream(outputfile, false);
						out = new OutputStreamWriter(fos, "UTF-8");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		final Button stopbutton = (Button)this.findViewById(R.id.Stop);
		stopbutton.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				if (outputfile != null) {
					try {
						out.close();
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outputfile = null;
				}
			}
		});
		
		final Button exitbutton = (Button)this.findViewById(R.id.Exit);
		exitbutton.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				if (outputfile != null) {
					try {
						out.close();
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outputfile = null;
				}
				finish();
				System.exit(0);
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();	
	  // register this class as a listener for the orientation and accelerometer sensors
	}
	
	@Override
	protected void onStop() {
		// unregister listener
		super.onStop();
	}   

	@Override
	protected void onDestroy() {
		sm.unregisterListener(this);
		releaseWakeLock();
		super.onDestroy();
	}
	
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void onSensorChanged(SensorEvent arg0) {
		final TextView tx1 = (TextView) findViewById(R.id.TextView01);
		final TextView tx2 = (TextView) findViewById(R.id.TextView02);
		final TextView tx3 = (TextView) findViewById(R.id.TextView03);
		final TextView tx4 = (TextView) findViewById(R.id.TextView04);
		Sensor sensor = arg0.sensor;
		if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			ax = arg0.values[0];
			ay = arg0.values[1];
			az = arg0.values[2];
			a = Math.sqrt(ax*ax+ay*ay+az*az);
		} else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
			gx = arg0.values[0];
			gy = arg0.values[1];
			gz = arg0.values[2];
			g = Math.sqrt(gx*gx+gy*gy+gz*gz);
			angle = Math.asin(gz/g)*180/Math.PI;
		} else if (algorithmnumber == 4 && sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
			int tmp = (int)arg0.values[0];
			if (stepcountbase == -10) {
				stepcountbase = tmp;
			}
			stepcount = tmp-stepcountbase;
		}
		tx1.setText("加速度传感器：\nax = "+ax+"\nay = "+ay+"\naz = "+az+"\na = "+a+"\n");
		tx2.setText("重力传感器：\ngx = "+gx+"\ngy = "+gy+"\ngz = "+gz+"\ng = "+g+"\n");
		double args[] = AccelerationXYZ.calculate(ax, ay, az, gx, gy, gz);
		tx3.setText("水平方向加速度：\n"+args[4]+"\n竖直方向加速度：\n"+args[3]+"\n合加速度：\n"+Math.sqrt(args[3]*args[3]+args[4]*args[4])+"\n");
		if (sensortesttime == 0) {
			lastsensortime = Calendar.getInstance().getTimeInMillis()%100000;
			sensortesttime = 1;
			horiacce = args[4];
			veracce = args[3];
		} else {
			Long cursensortime = Calendar.getInstance().getTimeInMillis()%100000;
			if (lastsensortime/40 == cursensortime/40) {
				sensortesttime++;
				horiacce += args[4];
				veracce += args[3];
			} else {
				if (out != null) {
					try {
						Long output1 = lastsensortime/40;
						String output2 = new DecimalFormat("0.0000").format(horiacce/sensortesttime);
						String output3 = new DecimalFormat("0.0000").format(veracce/sensortesttime);
						String output4 = new DecimalFormat("0.00").format(angle);
						out.write(output1.toString()+","+output2+","+output3+","+output4+","+stepcount+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (algorithmnumber == 0 || algorithmnumber == 2) {
					DynamicTimeWarping.add((int)(lastsensortime/40), Double.parseDouble(new DecimalFormat("0.0000").format(horiacce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.0000").format(veracce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.00").format(angle)), true);
				} else if (algorithmnumber == 1 || algorithmnumber == 2) {
					CountDeepV.add((int)(lastsensortime/40), Double.parseDouble(new DecimalFormat("0.0000").format(horiacce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.0000").format(veracce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.00").format(angle)));
				} else if (algorithmnumber == 3) {
					StepCycle.add((int)(lastsensortime/40), Double.parseDouble(new DecimalFormat("0.0000").format(horiacce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.0000").format(veracce/sensortesttime)), Double.parseDouble(new DecimalFormat("0.00").format(angle)));
				}
				//
				lastsensortime = cursensortime;
				sensortesttime = 1;
				horiacce = args[4];
				veracce = args[3];
			}
		}
		String str = "";
		if (algorithmnumber == 0 || algorithmnumber == 2) {
			str = ((Integer)DynamicTimeWarping.getcounter()).toString();
		} else if (algorithmnumber == 1) {
			str = ((Integer)CountDeepV.getresult()).toString();
		} else if (algorithmnumber == 3) {
			str = ((Integer)StepCycle.getcounter()).toString();
		} else if (algorithmnumber == 4) {
			str = stepcount.toString();
		}
		tx4.setText("偏转角度："+angle+"°\n算法："+str);
		//tx4.setText(CountDeepV.getVnum());
	}
	
	private void acquireWakeLock() {
		//System.out.println("acquire wake lock");
		if (null == wakeLock)  
		{
			PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);  
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, this.getClass().getCanonicalName());  
			if (null != wakeLock) {
				wakeLock.acquire();
			}  
		}  
	}  
	
	private void releaseWakeLock() {
		//System.out.println("release wake lock");
		if (null != wakeLock) {  
			wakeLock.release();
			wakeLock = null;
		}  
	}
	
}
