package algorithm;

import java.util.ArrayList;
import java.util.List;

import algorithm.CountDeepV.timeelement;
import algorithm.CountDeepV.stepelement;

public class CountDeepV {

	public class timeelement {
		int time;
		double ha;
		double va;
		double angle;
		public timeelement(){}
		public timeelement(int t, double h, double v, double a) {
			time = t;
			ha = h;
			va = v;
			angle = a;
		}
	}
	
	public class stepelement {
		double verpeakmin;
		double verpeakmax;
		double horipeakmin;
		double horipeakmax;
		double totalpeakmax;
		double anglepeakmin;
		double anglepeakmax;
		int steplong;
		int stepdis;
		double p;
		int starttime;
		int endtime;
		int foot;
		double replace;
		public stepelement() {
			verpeakmin = 10000;
			verpeakmax = -10000;
			horipeakmin = 10000;
			horipeakmax = -1;
			totalpeakmax = -1;
			anglepeakmin = 200;
			anglepeakmax = -200;
			steplong = -1;
			stepdis = -1;
			p = 0;
			starttime = -1;
			endtime = -1;
			foot = 0;
			replace = 0;
		}
		public void copy(stepelement se) {
			verpeakmin = se.verpeakmin;
			verpeakmax = se.verpeakmax;
			horipeakmin = se.horipeakmin;
			horipeakmax = se.horipeakmax;
			totalpeakmax = se.totalpeakmax;
			anglepeakmin = se.anglepeakmin;
			anglepeakmax = se.anglepeakmax;
			steplong = se.steplong;
			stepdis = se.stepdis;
			p = se.p;
			starttime = se.starttime;
			endtime = se.endtime;
			foot = se.foot;
			replace = se.replace;
		}
	}
	
	private static double verpeakmin[] = {-4, -4}, verpeakmax[] = {6, 6};
	private static double horipeakmin[] = {2, 2}, horipeakmax[] = {6, 6};
	private static double anglepeakmin[] = {-10, -10}, anglepeakmax[] = {20, 20};
	private static double steplong[] = {10, 10};
	private static double stepdis[] = {14, 14};
	private static double totalpeakmax[] = {9, 9};
	private static double conf[] = {1, 1, 1, 1};
	private static List<timeelement> timedatalist = new ArrayList();
	private static int result = 0;
	private static List<stepelement> stepdatalist[] = new ArrayList[2];
	private static int resettime;
	private static int id = 0;
	
	private static double psure = 0.7;
	
	public static void reset(int t) {
		timedatalist.clear();
		if (stepdatalist[0] == null) stepdatalist[0] = new ArrayList();
		else stepdatalist[0].clear();
		if (stepdatalist[1] == null) stepdatalist[1] = new ArrayList();
		else stepdatalist[1].clear();
		result = 0;
		stepdis[0] = stepdis[1] = 14;
		steplong[0] = steplong[1] = 12;
		anglepeakmin[0] = anglepeakmin[1] = -10;
		anglepeakmax[0] = anglepeakmax[1] = 20;
		totalpeakmax[0] = totalpeakmax[1] = 9;
		horipeakmin[0] = horipeakmin[0] = 2;
		horipeakmax[0] = horipeakmax[1] = 6;
		verpeakmin[0] = verpeakmin[1] = -4;
		verpeakmax[0] = verpeakmax[1] = 4;
		conf[0] = conf[1] = conf[2] = conf[3] = 1;
		resettime = t;
		id = 0;
	}
	
	public static int getresult() {
		return result;
	}
	
	private static int timesub(int latetime, int earlytime) {
		int ans = latetime-earlytime;
		if (ans > 2000) ans -= 2500;
		else if (ans < -2000) ans += 2500;
		return ans;		
	}
	
	private static double calcp1(stepelement se, double vpmin, double vpmax) {
		double p1 = 0;
		if (se.verpeakmin >= -1 || se.verpeakmax <= 1) return 0;
		double p11, p12;
		if (se.verpeakmin <= 0.75*vpmin && se.verpeakmax >= 2*vpmin) p11 = 1;
		else if (se.verpeakmin > 0.75*vpmin) p11 = 1-((vpmin*0.75-se.verpeakmin)/(vpmin*0.75))*((vpmin*0.75-se.verpeakmin)/(vpmin*0.75))*2;
		else if (se.verpeakmin < 2*vpmin) p11 = 1-((se.verpeakmin-2*vpmin)/(2*vpmin))*((se.verpeakmax-2*vpmin)/(2*vpmin))*2;
		else p11 = 0;
		if (p11 < 0) p11 = 0;
		if (se.verpeakmax <= 2*vpmax && se.verpeakmax >= 0.75*vpmax) p12 = 1;
		else if (se.verpeakmax > 2*vpmax) p12 = 1-((se.verpeakmax-2*vpmax)/(2*vpmax))*((se.verpeakmax-2*vpmax)/(2*vpmax))*2;
		else if (se.verpeakmin < 0.75*vpmax) p12 = 1-((vpmax*0.75-se.verpeakmax)/(vpmax*0.75))*((vpmax*0.75-se.verpeakmax)/(vpmax*0.75))*2;
		else p12 = 0;
		if (p12 < 0) p12 = 0;
		p1 = Math.sqrt(p11)*p12;
		return p1;
	}
	
	private static double calcp2(stepelement se, double hpmin, double hpmax) {
		double p2 = 0;
		if (se.horipeakmax < 0 || se.horipeakmin < 0) return 0;
		double p21, p22;
		if (se.horipeakmin <= hpmin*2) p21 = 1;
		else p21 = 1-(se.horipeakmin-hpmin*2)*(se.horipeakmin-hpmin*2)/4;
		if (p21 < 0) p21 = 0;
		if (se.horipeakmax <= hpmax*2 && se.horipeakmax >= hpmax*0.5) p22 = 1;
		else if (se.horipeakmax > hpmax*2) p22 = 1-((se.horipeakmax-hpmax*2)/(hpmax*2))*((se.horipeakmax-hpmax*2)/(hpmax*2))*2;
		else if (se.horipeakmax < hpmax*0.75) p22 = 1-((hpmax*0.75-se.horipeakmax)/(hpmax*0.75))*((hpmax*0.75-se.horipeakmax)/(hpmax*0.75))*2;
		else p22 = 0;
		if (p22 < 0) p22 = 0;
		p2 = p21*p22;
		return p2;
	}
	
	private static double calcp3(stepelement se, double amin, double amax, double co) {
		double p3 = 0;
		if (se.anglepeakmax-se.anglepeakmin > 60) return 0;
		else if (amin == 200 && amax == -200) {
			p3 = 1-0.01*(se.anglepeakmax-se.anglepeakmin-20);
			if (p3 < 0) p3 = 0;
			if (p3 > 1) p3 = 1;
			return p3;
		}
		double p31, p32;
		if (Math.abs(se.anglepeakmin-amin) <= 15) p31 = 1;
		else p31 = 1-(Math.abs(se.anglepeakmin-amin)-15)*(Math.abs(se.anglepeakmin-amin)-15)/500;
		if (p31 < 0) p31 = 0;
		if (Math.abs(se.anglepeakmax-amax) <= 15) p32 = 1;
		else p32 = 1-(Math.abs(se.anglepeakmax-amax)-15)*(Math.abs(se.anglepeakmax-amax)-15)/500;
		if (p32 < 0) p32 = 0;
		if (co == 0) p3 = 1;
		else p3 = Math.pow(p31*p32, co);
		return p3;
	}
	
	private static double calcp4(stepelement se, double sl, double co) {
		double p4 = 0;
		if (se.steplong < 4 || se.steplong > 20) return 0;
		if (Math.abs(se.steplong-sl) <= 3) p4 = 1;
		else p4 = 1-(Math.abs(se.steplong-sl)-3)*(Math.abs(se.steplong-sl)-3)/50;
		if (p4 < 0) p4 = 0;
		p4 = Math.pow(p4, co);
		return p4;
	}
	
	/*private static double calcp5(stepelement se, List<stepelement> sdl0, List<stepelement> sdl1) {
		double p5 = 0;
		int t0 = 20, t1 = 20;
		double p0 = 1, p1 = 1;
		if (!sdl0.isEmpty()) {
			for (int i = sdl0.size()-1; i >= sdl0.size()-4 && i >= 0; i--) {
				if (timesub(se.endtime, sdl0.get(i).endtime) >= 0 && sdl0.get(i).p > psure) {
					t0 = timesub(se.endtime, sdl0.get(i).endtime);
					p0 = sdl0.get(i).p;
					break;
				}
			}
		}
		if (!sdl1.isEmpty()) {
			for (int i = sdl1.size()-1; i >= sdl1.size()-4 && i >= 0; i--) {
				if (timesub(se.starttime, sdl1.get(i).endtime) >= 0 && sdl1.get(i).p > psure) {
					t1 = timesub(se.starttime, sdl1.get(i).endtime);
					p1 = sdl1.get(i).p;
					break;
				}
			}
		}
		if (t0 >= 20) p5 = 1;
		else {
			if (t0 < t1) p5 = t0/20.0;
			else p5 = 1;
		}
		if (p5 < 0) p5 = 0;
		else if (p5 > 1) p5 = 1;
		return p5;
	}*/
	
	private static double calcp5(stepelement se, List<stepelement> sdl0, List<stepelement> sdl1) {
		double p5 = 0;
		int t0 = 20, t1 = 20;
		double p0 = 1, p1 = 1;
		if (!sdl0.isEmpty()) {
			for (int i = sdl0.size()-1; i >= sdl0.size()-4 && i >= 0; i--) {
				if (timesub(se.starttime, sdl0.get(i).endtime) >= 0) {
					t0 = timesub(se.starttime, sdl0.get(i).endtime);
					p0 = sdl0.get(i).p;
					break;
				}
			}
		}
		if (!sdl1.isEmpty()) {
			for (int i = sdl1.size()-1; i >= sdl1.size()-4 && i >= 0; i--) {
				if (timesub(se.starttime, sdl1.get(i).endtime) >= 0) {
					t1 = timesub(se.starttime, sdl1.get(i).endtime);
					p1 = sdl1.get(i).p;
					break;
				}
			}
		}
		if (t0 >= 15) p5 = 1;
		else {
			if (t0 < t1) p5 = (0.9+(t0-15)*0.05)*p0*p1;
			else p5 = 1;
		}
		if (p5 < 0) p5 = 0;
		else if (p5 > 1) p5 = 1;
		return p5;
	}
	
	private static double calcp6(stepelement se, double sd) {
		double p6 = 0;
		if (se.stepdis == -1) return 0.5;
		if (Math.abs(se.stepdis-sd) <= 2) p6 = 0.3-Math.abs(se.stepdis-sd)*Math.abs(se.stepdis-sd)*0.075;
		else if (se.stepdis-sd < -4) p6 = (se.stepdis-sd+4)*0.48/se.stepdis;
		if (p6 > 0.3) p6 = 0.3;
		else if (p6 < -0.3) p6 = -0.3;
		p6 = p6/3*5+0.5;
		return p6;
	}
	
	private static double calcp7(stepelement se, double tmax) {
		double p7 = 0;
		if (se.totalpeakmax <= 0) return 0;
		if (se.totalpeakmax >= tmax*0.75 && se.totalpeakmax <= tmax*2) return 1;
		else if (se.totalpeakmax > tmax*2) {
			p7 = 1-((se.totalpeakmax-tmax*2)/(tmax*2))*((se.totalpeakmax-tmax*2)/(tmax*2))*2;
		} else {
			p7 = 1-((se.totalpeakmax-tmax*0.75)/(tmax*0.75))*((se.totalpeakmax-tmax*0.75)/(tmax*0.75))*2;
		}
		return p7;
	}
	
	private static int getstepdis(stepelement se, List<stepelement> sdl0, List<stepelement> sdl1) {
		int t0 = -1, t1 = -1;
		double p0 = 0, p1 = 0;
		if (!sdl0.isEmpty()) {
			for (int i = sdl0.size()-1; i >= 0 && i >= sdl0.size()-4; i--) {
				if (timesub(se.starttime, sdl0.get(i).endtime) >= 0 && sdl0.get(i).p > psure) {
					p0 = sdl0.get(i).p;
					t0 = timesub(se.endtime, sdl0.get(i).endtime);
					break;
				}
			}
		}
		if (!sdl1.isEmpty()) {
			for (int i = sdl1.size()-1; i >= 0 && i >= sdl1.size()-4; i--) {
				if (timesub(se.starttime, sdl1.get(i).endtime) >= 0 && sdl1.get(i).p > psure) {
					p1 = sdl1.get(i).p;
					t1 = timesub(se.endtime, sdl1.get(i).endtime);
					break;
				}
			}
		}
		if (p0 == 0 && p1 == 0) return -1;
		else if (t0 == -1 || p0 == 0) return t1;
		else if (t1 == -1 || p1 == 0) return t0;
		else return Math.min(t0, t1);
	}
	
	private static void adapt(int t) {
		while (!stepdatalist[0].isEmpty() && timesub(t, stepdatalist[0].get(0).endtime) > 125) {
			stepdatalist[0].remove(0);
		}
		while (!stepdatalist[1].isEmpty() && timesub(t, stepdatalist[1].get(0).endtime) > 125) {
			stepdatalist[1].remove(0);
		}
		for (int foot = 0; foot <= 1; foot++) {
			double total[] = new double[9];
			double weightsum = 0;
			double stepdisweightsum = 0;
			int valid = 0;
			for (stepelement se:stepdatalist[foot]) {
				if (timesub(t, se.endtime) >= 4) {
					total[0] += se.verpeakmin*se.p/timesub(t, se.endtime);
					total[1] += se.verpeakmax*se.p/timesub(t, se.endtime);
					total[2] += se.horipeakmin*se.p/timesub(t, se.endtime);
					total[3] += se.horipeakmax*se.p/timesub(t, se.endtime);
					total[4] += se.anglepeakmin*se.p/timesub(t, se.endtime);
					total[5] += se.anglepeakmax*se.p/timesub(t, se.endtime);
					total[6] += se.steplong*se.p/timesub(t, se.endtime);
					total[8] += se.totalpeakmax*se.p/timesub(t, se.endtime);
					weightsum += se.p/timesub(t, se.endtime);
					if (se.stepdis >= 6 && se.stepdis <= 20 && se.p > psure) {
						total[7] += se.stepdis*se.p/timesub(t, se.endtime);
						stepdisweightsum += se.p/timesub(t, se.endtime);
					}
					valid++;
				}
			}
			if (timesub(t, resettime) <= 125) {
				total[0] += -4.0/timesub(t, resettime);
				total[1] += 6.0/timesub(t, resettime);
				total[2] += 2.0/timesub(t, resettime);
				total[3] += 6.0/timesub(t, resettime);
				total[6] += 10.0/timesub(t, resettime);
				total[7] += 14.0/timesub(t, resettime);
				total[8] += 9.0/timesub(t, resettime);
				weightsum += 1.0/timesub(t, resettime);
				stepdisweightsum += 1.0/timesub(t, resettime);
			}
			if (weightsum > 0) {
				verpeakmin[foot] = (total[0]-4*weightsum/4)/(weightsum*1.25);
				verpeakmax[foot] = (total[1]+6*weightsum/4)/(weightsum*1.25);
				horipeakmin[foot] = (total[2]+2*weightsum/4)/(weightsum*1.25);
				horipeakmax[foot] = (total[3]+6*weightsum/4)/(weightsum*1.25);
				anglepeakmin[foot] = total[4]/weightsum;
				anglepeakmax[foot] = total[5]/weightsum;
				steplong[foot] = total[6]/weightsum;
				totalpeakmax[foot] = (total[8]+9*weightsum/4)/(weightsum*1.25);
				if (stepdisweightsum > 0) {
					stepdis[foot] = total[7]/stepdisweightsum;
					if (stepdisweightsum < 0.07) conf[foot+2] = stepdisweightsum/0.07;
					else conf[foot+2] = 1;
				} else {
					stepdis[foot] = 14;
					conf[foot+2] = 1;
				}
				if (valid < 3) {
					anglepeakmin[foot] = 200;
					anglepeakmax[foot] = -200;
				}
				if (weightsum < 0.07) conf[foot] = weightsum/0.07;
				else conf[foot] = 1;
			} else {
				verpeakmin[foot] = -4;
				verpeakmax[foot] = 6;
				horipeakmin[foot] = 2;
				horipeakmax[foot] = 6;
				totalpeakmax[foot] = 9;
				anglepeakmin[foot] = 200;
				anglepeakmax[foot] = -200;
				steplong[foot] = 10;
				stepdis[foot] = 14;
				conf[foot] = conf[foot+2] = 1;
			}
			if (verpeakmin[foot] < -10) verpeakmin[foot] = -10;
			if (verpeakmax[foot] > 20) verpeakmax[foot] = 20;
			if (horipeakmin[foot] > 6) horipeakmin[foot] = 6;
			if (horipeakmax[foot] < 4) horipeakmax[foot] = 4;
			if (horipeakmax[foot] > 20) horipeakmax[foot] = 20;
			if (totalpeakmax[foot] > 30) totalpeakmax[foot] = 30;
			if (horipeakmin[foot] > horipeakmax[foot]) horipeakmin[foot] = horipeakmax[foot];
		}
	}
	
	public static double add(int t, double h, double v, double a) {
		double possi = 0;
		double p01 = 0, p02 = 0, p03 = 0, p04 = 0, p05 = 0, p06 = 0, p07 = 0, p11 = 0, p12 = 0, p13 = 0, p14 = 0, p15 = 0, p16 = 0, p17 = 0;
		double pb1 = 0, pb2 = 0, pb3 = 0, pb4 = 0, pb5 = 0, pb6 = 0, pb7 = 0;
		int newstep = 0;
		timeelement newele = new CountDeepV().new timeelement(t, h, v, a);
		stepelement bestse = new CountDeepV().new stepelement();
		timedatalist.add(newele);
		while (timesub(t, timedatalist.get(0).time) > 20) {
			timedatalist.remove(0);
		}
		if (timedatalist.size() >= 3 && v >= 0 && timedatalist.get(timedatalist.size()-2).va < 0) {
			double maxp = 0;
			adapt(t);
			for (int i = timedatalist.size()-2; i >= 0; i--) {
				if (timedatalist.get(i).va > 1 && (i == 0 || timedatalist.get(i-1).va <= 1) && timesub(t, timedatalist.get(i).time) >= 4) {
					stepelement se = new CountDeepV().new stepelement();
					se.starttime = timedatalist.get(i).time;
					se.endtime = t;
					se.steplong = timesub(se.endtime, se.starttime);
					for (int j = i; j < timedatalist.size()-1; j++) {
						if (timedatalist.get(j).va < se.verpeakmin) se.verpeakmin = timedatalist.get(j).va;
						if (timedatalist.get(j).va > se.verpeakmax) se.verpeakmax = timedatalist.get(j).va;
						if (timedatalist.get(j).ha < se.horipeakmin) se.horipeakmin = timedatalist.get(j).ha;
						if (timedatalist.get(j).ha > se.horipeakmax) se.horipeakmax = timedatalist.get(j).ha;
						if (timedatalist.get(j).angle < se.anglepeakmin) se.anglepeakmin = timedatalist.get(j).angle;
						if (timedatalist.get(j).angle > se.anglepeakmax) se.anglepeakmax = timedatalist.get(j).angle;
						if (Math.sqrt(timedatalist.get(j).va*timedatalist.get(j).va+timedatalist.get(j).ha*timedatalist.get(j).ha) > se.totalpeakmax) se.totalpeakmax = Math.sqrt(timedatalist.get(j).va*timedatalist.get(j).va+timedatalist.get(j).ha*timedatalist.get(j).ha);
					}
					se.stepdis = getstepdis(se, stepdatalist[0], stepdatalist[1]);
					p01 = calcp1(se, verpeakmin[0], verpeakmax[0]);
					p02 = calcp2(se, horipeakmin[0], horipeakmax[0]);
					p03 = calcp3(se, anglepeakmin[0], anglepeakmax[0], conf[0]);
					p04 = calcp4(se, steplong[0], conf[0]);
					p05 = calcp5(se, stepdatalist[0], stepdatalist[1]);
					p06 = (calcp6(se, stepdis[0])-0.5)*0.6;
					p07 = calcp7(se, totalpeakmax[0]);
					p11 = calcp1(se, verpeakmin[1], verpeakmax[1]);
					p12 = calcp2(se, horipeakmin[1], horipeakmax[1]);
					p13 = calcp3(se, anglepeakmin[1], anglepeakmax[1], conf[1]);
					p14 = calcp4(se, steplong[1], conf[1]);
					p15 = calcp5(se, stepdatalist[1], stepdatalist[0]);
					p16 = (calcp6(se, stepdis[1])-0.5)*0.6;
					p17 = calcp7(se, totalpeakmax[1]);
					se.p = Math.max(p01*p02*p03*p04*p05*p07+p06*conf[2], p11*p12*p13*p14*p15*p17+p16*conf[3]);
					if (se.p < 0) se.p = 0;
					else if (se.p > 1) se.p = 1;
					if (p01*p02*p03*p04*p05*p07+p06*conf[2] >= p11*p12*p13*p14*p15*p17+p16*conf[3]) {
						se.foot = 0;
					} else {
						se.foot = 1;
					}
					double pr0 = 0, pr1 = 0;
					if (!stepdatalist[0].isEmpty() && timesub(se.starttime, stepdatalist[0].get(stepdatalist[0].size()-1).endtime) < 0) {
						pr0 = stepdatalist[0].get(stepdatalist[0].size()-1).p;
					}
					if (!stepdatalist[1].isEmpty() && timesub(se.starttime, stepdatalist[1].get(stepdatalist[1].size()-1).endtime) < 0) {
						pr1 = stepdatalist[1].get(stepdatalist[1].size()-1).p;
					}
					//System.out.println("debug1: "+se.starttime+" "+se.endtime+" "+se.p+" "+se.foot+" | "+p01+" "+p02+" "+p03+" "+p04+" "+p05+" "+p06+" "+p07+" | "+p11+" "+p12+" "+p13+" "+p14+" "+p15+" "+p16+" "+p17);
					if (se.p > psure) {
						se.replace = 100;
						for (int j = stepdatalist[0].size()-1; j >= 0; j--) {
							if (timesub(se.starttime, stepdatalist[0].get(j).endtime) < 0) {
								se.replace--;
							} else break;
						}
						for (int j = stepdatalist[1].size()-1; j >= 0; j--) {
							if (timesub(se.starttime, stepdatalist[1].get(j).endtime) < 0) {
								se.replace--;
							} else break;
						}
					} else {
						se.replace = 0;
					}
					if (se.p > maxp && se.p > pr0 && se.p > pr1 && pr0 <= psure && pr1 <= psure && se.p+se.replace > bestse.p+bestse.replace) {
						maxp = se.p;
						bestse.copy(se);
						if (se.foot == 0) {
							pb1 = p01;
							pb2 = p02;
							pb3 = p03;
							pb4 = p04;
							pb5 = p05;
							pb6 = p06;
							pb7 = p07;
						} else {
							pb1 = p11;
							pb2 = p12;
							pb3 = p13;
							pb4 = p14;
							pb5 = p15;
							pb6 = p16;
							pb7 = p17;
						}
					}
				}
			}
			if (bestse.p > 0) {
				if (bestse.p > psure) newstep = 1;
				while (!stepdatalist[0].isEmpty() && timesub(bestse.starttime, stepdatalist[0].get(stepdatalist[0].size()-1).endtime) < 0) {
					if (stepdatalist[0].get(stepdatalist[0].size()-1).p > psure) newstep = 0;
					stepdatalist[0].remove(stepdatalist[0].size()-1);
				}
				while (!stepdatalist[1].isEmpty() && timesub(bestse.starttime, stepdatalist[1].get(stepdatalist[1].size()-1).endtime) < 0) {
					if (stepdatalist[1].get(stepdatalist[1].size()-1).p > psure) newstep = 0;
					stepdatalist[1].remove(stepdatalist[1].size()-1);
				}
				stepdatalist[bestse.foot].add(bestse);
				possi = bestse.p;
			}
		}
		id++;
		result += newstep;
		//System.out.print(id+","+t+","+h+","+v+","+a+","+(10*possi+30)+","+(10*newstep+10)+","+result+",");
		//System.out.print(bestse.verpeakmin+","+verpeakmin[bestse.foot]+","+bestse.verpeakmax+","+verpeakmax[bestse.foot]+","+pb1+","+bestse.horipeakmin+","+horipeakmin[bestse.foot]+","+bestse.horipeakmax+","+horipeakmax[bestse.foot]+","+pb2+","+bestse.totalpeakmax+","+totalpeakmax[bestse.foot]+","+pb7+",");
		//System.out.println(bestse.anglepeakmin+","+anglepeakmin[bestse.foot]+","+bestse.anglepeakmax+","+anglepeakmax[bestse.foot]+","+pb3+","+bestse.steplong+","+steplong[bestse.foot]+","+pb4+","+bestse.stepdis+","+stepdis[bestse.foot]+","+pb6+","+bestse.foot+","+pb5);
		return possi;
	}
	
}
