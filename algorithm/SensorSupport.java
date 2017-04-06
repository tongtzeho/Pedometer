package algorithm;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.TextView;

public class SensorSupport {

	public static void test(Activity curact, TextView tx1) {
		SensorManager sm = (SensorManager)curact.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
		tx1.setText("经检测该手机有" + allSensors.size() + "个传感器，他们分别是：\n");
		for (Sensor s : allSensors) {
			String tempString = "\n" + "  设备名称：" + s.getName() + "\n" + "  设备版本：" + s.getVersion() + "\n" + "  供应商："
							+ s.getVendor() + "\n";

			switch (s.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
					tx1.setText(tx1.getText().toString() + s.getType() + " 加速度传感器accelerometer" + tempString);
					break;
			case Sensor.TYPE_GYROSCOPE:
					tx1.setText(tx1.getText().toString() + s.getType() + " 陀螺仪传感器gyroscope" + tempString);
					break;
			case Sensor.TYPE_LIGHT:
					tx1.setText(tx1.getText().toString() + s.getType() + " 环境光线传感器light" + tempString);
					break;
			case Sensor.TYPE_MAGNETIC_FIELD:
					tx1.setText(tx1.getText().toString() + s.getType() + " 电磁场传感器magnetic field" + tempString);
					break;
			case Sensor.TYPE_ORIENTATION:
					tx1.setText(tx1.getText().toString() + s.getType() + " 方向传感器orientation" + tempString);
					break;
			case Sensor.TYPE_PRESSURE:
					tx1.setText(tx1.getText().toString() + s.getType() + " 压力传感器pressure" + tempString);
					break;
			case Sensor.TYPE_PROXIMITY:
					tx1.setText(tx1.getText().toString() + s.getType() + " 距离传感器proximity" + tempString);
					break;
			case Sensor.TYPE_TEMPERATURE:
					tx1.setText(tx1.getText().toString() + s.getType() + " 温度传感器temperature" + tempString);
					break;
			case Sensor.TYPE_STEP_COUNTER:
					tx1.setText(tx1.getText().toString() + s.getType() + " 计步器传感器step counter" + tempString);
					break;
			case Sensor.TYPE_STEP_DETECTOR:
					tx1.setText(tx1.getText().toString() + s.getType() + " 步探测器step detecter" + tempString);
					break;
			default:
					tx1.setText(tx1.getText().toString() + s.getType() + " 未知传感器" + tempString);
					break;
			}
		}
	}

}
