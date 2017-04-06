package algorithm;

import java.util.ArrayList;
import java.util.List;

public class DynamicTimeWarping {

	public class element {
		int time;
		double ha;
		double va;
		double angle;
		boolean direct;
		public element(){};
		public element(int t, double h, double v, double a, boolean d) {
			time = t;
			ha = h;
			va = v;
			angle = a;
			direct = d;
		}
	}
	
	public class sequence {
		List<element> track;
		boolean isstd;
		boolean expire;
		int endtime;
		double conf;
		public sequence() {
			track = new ArrayList();
			isstd = false;
			expire = false;
			endtime = -1;
		}
		double value(int t) {
			if (endtime == -1) return 0;
			if (isstd) return 1;
			if (expire) return Math.min(1, 0.01+0.19*conf);
			return Math.min(1, 0.2+0.3*Math.min(1, conf)+0.5*Math.min(1, (250-timesub(t, endtime))/125));
		}
	}
	
	private static int timesub(int latetime, int earlytime) {
		int ans = latetime-earlytime;
		if (ans > 2000) ans -= 2500;
		else if (ans < -2000) ans += 2500;
		return ans;		
	}
	
	static sequence adapt[];
	static sequence std[];
	static List<element> curlist = new ArrayList();
	static int counter = 0;
	static int tnum = 0;
	static int laststepoccurtime = -1;
	
	static public boolean debug = true;
	static public boolean graph = false;
	
	public static int getcounter() {
		return counter;
	}
	
	public static void init() {
		adapt = new sequence[20];
		std = new sequence[10];
		for (int i = 0; i < adapt.length; i++) {
			adapt[i] = new DynamicTimeWarping().new sequence();
		}
		std[0] = new DynamicTimeWarping().new sequence();
		std[0].isstd = true;
		std[0].track.add(new DynamicTimeWarping().new element(0, 8.9637, -0.0169, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(1, 6.8707, 1.5796, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(2, 2.838, 7.5017, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(3, 4.0358, 7.8992, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(4, 6.358, 1.1502, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(5, 5.0705, -2.8746, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(6, 2.6177, -4.522, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(7, 2.8518, -6.6611, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(8, 3.0865, -7.1882, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(9, 4.402, -6.4751, 0, false));
		std[0].track.add(new DynamicTimeWarping().new element(10, 9.0412, 0.8429, 0, false));
		
		std[1] = new DynamicTimeWarping().new sequence();
		std[1].isstd = true;
		std[1].track.add(new DynamicTimeWarping().new element(0, 7.2379, 0.4454, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(1, 8.2323, 2.3181, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(2, 17.5713, 4.9526, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(3, 9.9291, 10.734, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(4, 6.1984, 12.445, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(5, 5.8839, 11.9034, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(6, 15.6011, -1.8112, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(7, 10.5583, -5.9548, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(8, 8.2748, -10.3018, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(9, 5.5973, -8.9366, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(10, 4.7368, -7.6326, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(11, 8.0259, -0.3109, 0, false));
		std[1].track.add(new DynamicTimeWarping().new element(12, 6.0012, 12.6902, 0, false));
		
		std[2] = new DynamicTimeWarping().new sequence();
		std[2].isstd = true;
		std[2].track.add(new DynamicTimeWarping().new element(0, 5.7276, -1.7036, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(1, 5.2547, 0.956, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(2, 3.0648, 3.89, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(3, 2.4586, 3.08, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(4, 3.1545, -0.5856, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(5, 1.4974, -3.0456, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(6, 0.8148, -2.4095, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(7, 1.1025, -0.39, 0, false));
		std[2].track.add(new DynamicTimeWarping().new element(8, 2.831, 0.1822, 0, false));
		
		std[3] = new DynamicTimeWarping().new sequence();
		std[3].isstd = true;
		std[3].track.add(new DynamicTimeWarping().new element(0, 4.2092, 0.6314, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(1, 3.9982, 2.2703, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(2, 2.7603, 1.9919, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(3, 0.982, 1.4858, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(4, 2.3589, 0.8408, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(5, 1.986, 0.0163, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(6, 1.4766, -1.1022, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(7, 3.2428, -1.0287, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(8, 0.7705, -1.8209, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(9, 1.3437, -1.6948, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(10, 1.9788, -1.2766, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(11, 2.2009, -1.3031, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(12, 1.712, -1.6076, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(13, 2.7534, -0.1833, 0, false));
		std[3].track.add(new DynamicTimeWarping().new element(14, 4.3965, 1.4934, 0, false));
		
		std[4] = new DynamicTimeWarping().new sequence();
		std[4].isstd = true;
		std[4].track.add(new DynamicTimeWarping().new element(0, 3.3357, -7.6017, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(1, 3.3607, 1.0318, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(2, 12.274, 21.3011, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(3, 11.2296, 15.3879, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(4, 5.6709, 0.2528, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(5, 4.0492, 1.2035, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(6, 8.2568, -1.9995, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(7, 7.9919, -4.5517, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(8, 4.8107, -6.4712, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(9, 2.2429, -5.3151, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(10, 5.4202, -2.3558, 0, false));
		std[4].track.add(new DynamicTimeWarping().new element(11, 13.1294, 13.297, 0, false));
		
		std[5] = new DynamicTimeWarping().new sequence();
		std[5].isstd = true;
		std[5].track.add(new DynamicTimeWarping().new element(0, 7.6095, -2.77, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(1, 15.988, 12.3015, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(2, 13.4816, 6.4892, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(3, 7.9854, -3.9778, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(4, 17.3418, -16.8234, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(5, 5.9097, -15.6353, 0, false));
		std[5].track.add(new DynamicTimeWarping().new element(6, 15.5552, 5.9436, 0, false));
		
		std[6] = new DynamicTimeWarping().new sequence();
		std[6].isstd = true;
		std[6].track.add(new DynamicTimeWarping().new element(0, 4.1257, -2.271, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(1, 12.4308, 16.3943, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(2, 8.6055, 1.1634, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(3, 8.8311, -10.5137, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(4, 6.8328, -11.9921, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(5, 2.3505, -12.0035, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(6, 3.3346, -12.7622, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(7, 1.347, -10.5286, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(8, 3.72, -8.7796, 0, false));
		std[6].track.add(new DynamicTimeWarping().new element(9, 14.9753, 0.2169, 0, false));
		
		std[7] = new DynamicTimeWarping().new sequence();
		std[7].isstd = true;
		std[7].track.add(new DynamicTimeWarping().new element(0, 13.8793, -2.3995, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(1, 13.4353, 2.4826, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(2, 16.2252, 5.2575, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(3, 7.1291, 11.1469, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(4, 19.074, 7.141, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(5, 25.2411, -4.5142, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(6, 15.5225, -23.3151, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(7, 17.6892, -24.6015, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(8, 16.827, -10.1848, 0, false));
		std[7].track.add(new DynamicTimeWarping().new element(9, 5.8862, 0.2534, 0, false));
		
		std[8] = new DynamicTimeWarping().new sequence();
		std[8].isstd = true;
		std[8].track.add(new DynamicTimeWarping().new element(0, 3.0876, 0.9425, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(1, 5.9025, 1.3831, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(2, 7.9013, 1.4377, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(3, 8.6535, 1.4999, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(4, 6.1001, 3.7508, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(5, 3.7264, 1.7473, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(6, 2.1475, 4.0366, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(7, 2.5569, 4.2626, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(8, 4.2034, 0.2231, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(9, 1.4295, -0.9061, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(10, 0.5707, -1.5007, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(11, 1.5171, -1.828, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(12, 3.6571, -1.3566, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(13, 5.9509, -1.8377, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(14, 7.0043, -3.2461, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(15, 6.768, -3.5307, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(16, 6.5185, -2.8754, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(17, 5.8989, -2.8308, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(18, 5.7276, -1.7036, 0, false));
		std[8].track.add(new DynamicTimeWarping().new element(19, 5.2547, 0.956, 0, false));
		
		std[9] = new DynamicTimeWarping().new sequence();
		std[9].isstd = true;
		std[9].track.add(new DynamicTimeWarping().new element(0, 2.4558, 0.7825, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(1, 3.7776, 1.3636, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(2, 6.282, 1.5996, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(3, 5.0394, 2.8947, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(4, 7.5872, 2.3061, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(5, 1.9665, 1.6408, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(6, 4.9492, 7.3868, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(7, 5.5054, 4.1031, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(8, 4.1853, 0.462, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(9, 4.6559, -4.7049, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(10, 6.329, -8.1635, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(11, 2.1109, -1.9474, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(12, 4.1574, -5.4435, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(13, 4.185, -6.3729, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(14, 3.5634, -3.5708, 0, false));
		std[9].track.add(new DynamicTimeWarping().new element(15, 6.1752, 4.5549, 0, false));
		
	}
	
	public static void reset() {
		for (int i = 0; i < adapt.length; i++) {
			adapt[i].endtime = -1;
			adapt[i].track.clear();
		}
		curlist.clear();
		counter = 0;
		tnum = 0;
		laststepoccurtime = -1;
	}
	
	private static void adaptexpire(int t) {
		for (int i = 0; i < adapt.length; i++) {
			if (adapt[i].endtime != -1 && timesub(t, adapt[i].endtime) > 250) {
				adapt[i].expire = true;
			}
		}
		if (laststepoccurtime != -1 && timesub(t, laststepoccurtime) > 15) {
			laststepoccurtime = -1;
		}
	}
	
	private static double getdist(element e1, element e2) {
		double d1 = 0, d2 = 0, d3 = 0;
		d1 = Math.max(0, Math.abs(e1.va-e2.va)/Math.sqrt(Math.max(0.25, Math.abs(e2.va)/4))-1.5);
		d2 = 0.8*Math.max(0, Math.abs(e1.ha-e2.ha)/Math.sqrt(Math.max(0.25, e2.ha/2))-1.5);
		if (e1.direct && e2.direct) {
			d3 = 0.08*Math.max(0, Math.abs(e1.angle-e2.angle)-5)*Math.max(0, Math.abs(e1.angle-e2.angle)-5);
		}
		return Math.sqrt(d1*d1+d2*d2+d3*d3);
	}
	
	private static double dtwcalc(int index, List<element> list) {
		int l1 = curlist.size()-index;
		int l2 = list.size();
		double result[][] = new double[l1][l2];
		for (int i = index; i < curlist.size(); i++) {
			element e1 = curlist.get(i);
			for (int j = 0; j < list.size(); j++) {
				element e2 = list.get(j);
				double dist = Math.sqrt(getdist(e1, e2));
				if (i == index && j == 0) {
					result[0][0] = dist;
				} else if (i == index) {
					result[0][j] = result[0][j-1]+dist;
				} else if (j == 0) {
					result[i-index][0] = result[i-index-1][0]+dist;
				} else {
					result[i-index][j] = Math.min(Math.min(result[i-index-1][j], result[i-index][j-1]), result[i-index-1][j-1])+dist;
				}
			}
		}
		int t1 = timesub(curlist.get(curlist.size()-1).time, curlist.get(index).time);
		int t2 = timesub(list.get(l2-1).time, list.get(0).time);
		result[l1-1][l2-1] *= result[l1-1][l2-1]/12.0;
		if (Math.abs(t1-t2) <= 1) return result[l1-1][l2-1]/12.0+0.0001;
		else if (Math.abs(t1-t2) <= 2) return result[l1-1][l2-1]/6.0+0.0001;
		else if (Math.abs(t1-t2) <= 3) return result[l1-1][l2-1]/2.0+0.0001;
		else return 999999;
	}
	
	private static double getconf(int curtime, int index) {
		double k = 3.2, m = 0.8;
		double adaptgroup[] = new double[adapt.length];
		double stdgroup[] = new double[std.length];
		double dtws[] = new double[adapt.length+std.length];
		double adaptvaluetotal = 0;
		if (debug) System.out.println();
		for (int i = 0; i < adaptgroup.length; i++) {
			adaptvaluetotal += adapt[i].value(curtime);
			if (adapt[i].endtime != -1 && (adapt[i].expire || timesub(curlist.get(index).time, adapt[i].endtime) >= -1)) {
				adaptgroup[i] = dtwcalc(index, adapt[i].track);
				dtws[std.length+i] = k/adaptgroup[i];
				if (dtws[std.length+i] > m) dtws[std.length+i] = Math.min(1.5, m+adapt[i].value(curtime)*(dtws[std.length+i]-m));
			} else {
				dtws[std.length+i] = 0;
			}
			if (debug) System.out.print(dtws[std.length+i]+" ");
			if (debug && dtws[std.length+i] > 1) System.out.print("!!! ");
		}
		if (debug) System.out.println();
		for (int i = 0; i < std.length; i++) {
			stdgroup[i] = dtwcalc(index, std[i].track);
			dtws[i] = k/stdgroup[i];
			if (dtws[i] > m) dtws[i] = m+(adapt.length-adaptvaluetotal)/adapt.length*(Math.min(1.5, m+0.8*(dtws[i]-m))-m);
			if (debug) System.out.print(dtws[i]+" ");
			if (debug && dtws[i] > 1) System.out.print("??? ");
		}
		if (debug) System.out.println();
		for (int i = 0; i < dtws.length; i++)
			for (int j = i+1; j < dtws.length; j++) {
				if (dtws[i] < dtws[j]) {
					double tmp = dtws[i];
					dtws[i] = dtws[j];
					dtws[j] = tmp;
				}
			}
		if (debug) System.out.println(dtws[0]+" "+dtws[1]+" "+dtws[2]+" "+dtws[3]+" "+dtws[4]+" "+dtws[5]+" "+dtws[6]);
		int validadapt = 0;
		for (int i = 0; i < adapt.length; i++) {
			if (adapt[i].endtime != -1 && !adapt[i].expire) {
				validadapt++;
			}
		}
		double conf = 0;
		int limit = Math.min(8, 1+Math.max(0, validadapt-2)/2);
		for (int j = 0; j <= limit; j++) {
			double confsum = 0;
			for (int l = 0; l <= j; l++) {
				confsum += dtws[l]*(j-l+1)/((j+2)*(j+1)/2);
			}
			confsum *= Math.max(0, 1-0.075*(limit-j));
			if (confsum > conf) {
				conf = confsum;
			}
		}
		double maxa = 0;
		for (int j = index; j < curlist.size(); j++) {
			double va = curlist.get(j).va;
			double ha = curlist.get(j).ha;
			double angle = curlist.get(j).angle;
			if (ha*ha+va*va > maxa*maxa) {
				maxa = Math.sqrt(ha*ha+va*va);
			}
		}
		conf *= Math.min(1, Math.max(0, (maxa-2.0))/2.0);
		if (laststepoccurtime != -1) {
			int ts = timesub(curtime, laststepoccurtime);
			if (ts == 17) conf *= 1.05;
			else if (ts == 16) conf *= 1.1;
			else if (ts == 15) conf *= 1.2;
			else if (ts == 14) conf *= 1.4;
			else if (ts == 13) conf *= 1.5;
			else if (ts == 12) conf *= 1.4;
			else if (ts == 11) conf *= 1.2;
			else if (ts == 9) conf *= 0.95;
			else if (ts == 8) conf *= 0.8;
			else if (ts == 7) conf *= 0.65;
			else if (ts == 6) conf *= 0.4;
			else if (ts <= 5) conf *= 0.2;
			if (conf >= 1.5) conf = 1.5;
		}
		return conf;
	}
	
	public static int add(int t, double h, double v, double a, boolean d) {
		int result = 0;
		tnum++;
		adaptexpire(t);
		curlist.add(new DynamicTimeWarping().new element(t, h, v, a, d));
		while (timesub(t, curlist.get(0).time) > 21) {
			curlist.remove(0);
		}
		if (curlist.size() > 3 && v >= 0 && curlist.get(curlist.size()-2).va < 0) {
			for (int i = curlist.size()-2; i > 0; i--) {
				if (curlist.get(i).va > 1 && curlist.get(i-1).va <= 1 && timesub(t, curlist.get(i-1).time) >= 5) {
					double conf = getconf(t, i-1);
					if (debug) System.out.println(conf+"\n");
					if (conf >= 1) {
						int changeadapt = -1;
						for (int j = 0; j < adapt.length; j++) {
							if (adapt[j].endtime == -1) {
								changeadapt = j;
								break;
							} else if (!adapt[j].expire && timesub(curlist.get(i-1).time, adapt[j].endtime) < -1) {
								changeadapt = j;
								break;
							} else if (changeadapt == -1) {
								changeadapt = j;
							} else if (adapt[j].value(t) < adapt[changeadapt].value(t)) {
								changeadapt = j;
							}
						}
						
						adapt[changeadapt].endtime = t;
						adapt[changeadapt].expire = false;
						adapt[changeadapt].conf = conf;
						adapt[changeadapt].track.clear();
						for (int j = i-1; j < curlist.size(); j++) {
							adapt[changeadapt].track.add(curlist.get(j));
							if (debug) System.out.println(changeadapt+" "+curlist.get(j).ha+" "+curlist.get(j).va+" "+curlist.get(j).angle+" "+curlist.get(j).direct);
						}
						if (debug) System.out.println();
						for (int j = curlist.size()-3; j >= 0; j--) {
							curlist.remove(j);
						}
						result = 1;
						counter++;
						laststepoccurtime = t;
						break;
					} else if (conf >= 0.05) {
						int changeadapt = -1;
						for (int j = 0; j < adapt.length; j++) {
							if (adapt[j].endtime == -1) {
								changeadapt = j;
								break;
							} else if (!adapt[j].expire && timesub(curlist.get(i-1).time, adapt[j].endtime) < -1) {
								changeadapt = j;
								break;
							} else if (changeadapt == -1 && conf > adapt[j].value(t)) {
								changeadapt = j;
							} else if (changeadapt != -1 && adapt[j].value(t) < adapt[changeadapt].value(t) && conf > adapt[j].value(t)) {
								changeadapt = j;
							}
						}
						if (changeadapt != -1) {
							adapt[changeadapt].endtime = t;
							adapt[changeadapt].expire = false;
							adapt[changeadapt].conf = conf;
							adapt[changeadapt].track.clear();
							for (int j = i-1; j < curlist.size(); j++) {
								adapt[changeadapt].track.add(curlist.get(j));
								if (debug) System.out.println(changeadapt+" "+curlist.get(j).ha+" "+curlist.get(j).va+" "+curlist.get(j).angle+" "+curlist.get(j).direct);
							}
							if (debug) System.out.println();
						}
					}
				}
			}
		}
		if (graph) System.out.println(tnum+","+h+","+v+","+a+","+(result*10+30)+","+counter);
		return result;
	}
	
}
