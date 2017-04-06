package algorithm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StepCycle {

	public class Element {
		int tnum;
		int time;
		double fa;
		double va;
		double orientation;
		int isstep = 0;
		int isstart = 0;
		int isend = 0;
		public Element(){};
		public Element(int t, double f, double v, double o) {
			time = t;
			fa = f;
			va = v;
			orientation = o;
		}
		public Element(int tnum, int t, double f, double v, double o) {
			this.tnum = tnum;
			time = t;
			fa = f;
			va = v;
			orientation = o;
		}
	}
	
	public class Step {
		int starttnum;
		int starttime;
		int endtnum;
		int endtime;
		int timedis;
		List<Element> trace;
		boolean isstd;
		public Step() {
			trace = new ArrayList();
			timedis = -1;
			isstd = false;
		}
		public int getsteplength() {
			return timesub(endtime, starttime)+1;
		}
		public double getmaxfa() {
			double maxfa = 0;
			for (Element e:trace) {
				if (e.fa > maxfa) {
					maxfa = e.fa;
				}
			}
			return maxfa;
		}
		public double getminfa() {
			double minfa = 9999999;
			for (Element e:trace) {
				if (e.fa < minfa) {
					minfa = e.fa;
				}
			}
			return minfa;
		}
		public double getavgori() {
			double sum = 0;
			for (Element e:trace) {
				sum += e.orientation;
			}
			return sum/trace.size();
		}
		public double getsteori() {
			double sum = 0;
			double avg = getavgori();
			for (Element e:trace) {
				sum += (e.orientation-avg)*(e.orientation-avg);
			}
			if (sum <= 0) return 0;
			return Math.sqrt(sum/trace.size());
		}
		public double getrangeori() {
			double maxori = -90;
			double minori = 90;
			for (Element e:trace) {
				if (e.orientation < minori) {
					minori = e.orientation;
				}
				if (e.orientation > maxori) {
					maxori = e.orientation;
				}
			}
			return maxori-minori;
		}
		public double getavgacce2() {
			double total = 0;
			for (Element e:trace) {
				total += e.va*e.va;
			}
			return total/trace.size();
		}
	}
	
	static List<Step> recentsteplist = new ArrayList();
	static Step adapt[];
	static Step std[];
	static List<Element> curlist = new ArrayList();
	static List<Element> debug = new ArrayList();
	static int tnum;
	static int counter;
	static int laststeptime;
	
	static double adjust = 0.5;
	static double forwardacce = 5;
	static double inf = 99999999;
	
	static double threshold1 = 8;
	static double threshold2 = 5;
	
	static boolean debug1 = false;
	static boolean debug2 = false;
	
	public static int getcounter() {
		return counter;
	}
	
	public static void init() {
		adapt = new Step[20];
		std = new Step[13];
		for (int i = 0; i < adapt.length; i++) {
			adapt[i] = new StepCycle().new Step();
		}
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream("original.ini");
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String line;
			for (int i = 0; i < std.length; i++) {
				std[i] = new StepCycle().new Step();
				std[i].isstd = true;
				line = br.readLine();
				for (int j = 0; j < Integer.parseInt(line); j++) {
					String acce[] = br.readLine().split(" ");
					double fa = Double.parseDouble(acce[0]);
					double va = Double.parseDouble(acce[1]);
					std[i].trace.add(new StepCycle().new Element(j, fa, va, 0));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
				if (isr != null) isr.close();
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public static void reset() {
		for (int i = 0; i < adapt.length; i++) {
			adapt[i].endtime = -1;
			adapt[i].timedis = -1;
			adapt[i].trace.clear();
		}
		recentsteplist.clear();
		curlist.clear();
		debug.clear();
		counter = 0;
		tnum = 0;
		laststeptime = -1;
	}
	
	private static int timesub(int latetime, int earlytime) {
		int ans = latetime-earlytime;
		if (ans > 2000) ans -= 2500;
		else if (ans < -2000) ans += 2500;
		return ans;		
	}
	
	public static double getvadis(double va1, double va2) {
		if (va1 > va2) {
			double tmp = va1;
			va1 = va2;
			va2 = tmp;
		}
		if (va2 - va1 <= 2*adjust) {
			return 0;
		} else {
			va2 -= adjust;
			va1 += adjust;
			if (va1 <= 0 && va2 >= 0) {
				return 3*(va2-va1)/Math.sqrt(adjust);
			} else {
				return 1.5*(va2-va1)/Math.sqrt(Math.max(adjust, Math.min(Math.abs(va1), Math.abs(va2))));
			}
			/*if (va1 <= 0 && va2 >= 0) {
				return inf;
			} else if (va1 > 0) {
				return Math.log(Math.max(1, va2/va1-0.1));
			} else {
				return Math.log(Math.max(1, va1/va2-0.1));
			}*/
		}
	}
	
	public static double getfadis(double famax1, double famax2, double famin1, double famin2) {
		double result = 0;
		famax1 += 2*adjust;
		famax2 += 2*adjust;
		if (famax1 < forwardacce) {
			result += (forwardacce-famax1)*(forwardacce-famax1)*4;
		}
		if (famax2 < forwardacce) {
			result += (forwardacce-famax2)*(forwardacce-famax2)*4;
		}
		if (famax1 > famax2) {
			double tmp = famax1;
			famax1 = famax2;
			famax2 = tmp;
		}
		if (famax2-famax1 >= 4*adjust) {
			famax2 -= 4*adjust;
			result += 0.25*(famax2-famax1)/Math.sqrt(Math.max(adjust, famax1));
		}	
		famin1 -= 2*adjust;
		famin2 -= 2*adjust;
		if (famin1 > forwardacce) {
			result += (famin1-forwardacce)*(famin1-forwardacce);
		}
		if (famin2 > forwardacce) {
			result += (famin2-forwardacce)*(famin2-forwardacce);
		}
		return result;
	}
	
	public static double getorirangeresult(double range) {
		if (range <= 45) {
			return 0;
		} else {
			return Math.pow(range-45, 1.5)/20;
		}
	}
	
	public static double getoridis(double avgori1, double avgori2, double steori1, double steori2) {
		steori1 = Math.max(5, steori1);
		steori2 = Math.max(5, steori2);
		double min1 = avgori1-steori1;
		double max1 = avgori1+steori1;
		double min2 = avgori2-steori2;
		double max2 = avgori2+steori2;
		double p1 = Math.min(0, Math.max(min1, min2)-Math.min(max1, max2));
		double p2 = Math.max(max1, max2)-Math.min(min1, min2)-Math.max(0, (Math.max(min1, min2)-Math.min(max1, max2)));
		if (p1 < 0) {
			return 3*p1/p2;
		}
		min1 = avgori1-2*steori1;
		max1 = avgori1+2*steori1;
		min2 = avgori2-2*steori2;
		max2 = avgori2+2*steori2;
		if (Math.max(min1, min2)-Math.min(max1, max2) < 0) {
			return (max2-min2+max1-min1)/10;
		}
		return 0;
	}
	
	public static double geteledis(Element e1, int i1, Element e2, int i2) {
		double varesult = getvadis(e1.va, e2.va);
		if (Math.abs(i1-i2) <= 1) return varesult;
		else if (Math.abs(i1-i2) == 2) return Math.min(inf, 3*varesult);
		else if (Math.abs(i1-i2) == 3) return Math.min(inf, 8*varesult);
		else return inf;
	}
	
	public static double getavgacce2dis(double x, double y) {
		if (x > 25) x = 25;
		if (y > 25) y = 25;
		return 2*(Math.max(1, (Math.max(1.5, Math.max(x, y))-adjust*adjust)/(Math.min(x, y)+adjust*adjust))-1);
	}
	
	public static double gettracedis(Step step1, Step step2) {
		double famax1 = step1.getmaxfa();
		double famax2 = step2.getmaxfa();
		double famin1 = step1.getminfa();
		double famin2 = step2.getminfa();
		double result[][] = new double[step1.trace.size()][step2.trace.size()];
		for (int i = 0; i < step1.trace.size(); i++) {
			Element e1 = step1.trace.get(i);
			for (int j = 0; j < step2.trace.size(); j++) {
				Element e2 = step2.trace.get(j);
				double dis = geteledis(e1, timesub(step1.trace.get(i).time, step1.trace.get(0).time), e2, timesub(step2.trace.get(j).time, step2.trace.get(0).time));
				if (i == 0 && j == 0) {
					result[0][0] = dis;
				} else if (i == 0) {
					result[0][j] = Math.min(inf, result[0][j-1]+dis);
				} else if (j == 0) {
					result[i][0] = Math.min(inf, result[i-1][0]+dis);
				} else {
					result[i][j] = Math.min(inf, Math.min(Math.min(result[i-1][j], result[i][j-1]), result[i-1][j-1])+dis);
				}
			}
		}
		double faresult = getfadis(famax1, famax2, famin1, famin2);
		double oriresult = 0;
		if (!step2.isstd) {
			double avgori1 = step1.getavgori();
			double steori1 = step1.getsteori();
			double avgori2 = step2.getavgori();
			double steori2 = step2.getsteori();
			oriresult = getoridis(avgori1, avgori2, steori1, steori2);
		}
		double orirangeresult1 = getorirangeresult(step1.getrangeori());
		double orirangeresult2 = getorirangeresult(step2.getrangeori());
		double avgacce21 = step1.getavgacce2();
		double avgacce22 = step2.getavgacce2();
		double avgacce2dis = getavgacce2dis(avgacce21, avgacce22);
		if (debug1) {
			for (Element e:step1.trace) {
				System.out.println(e.time+" : "+e.va);
			}
			System.out.println();
			for (Element e:step2.trace) {
				System.out.println(e.time+" : "+e.va);
			}
			System.out.println();
			System.out.println("fa_max : "+famax1+" >< "+famax2);
			System.out.println("fa_min : "+famin1+" >< "+famin2);
			System.out.println();
			System.out.println(result[step1.trace.size()-1][step2.trace.size()-1]/(step1.trace.size()+step2.trace.size())+" "+faresult+" "+oriresult+" "+orirangeresult1+" "+orirangeresult2+" "+avgacce2dis);
			System.out.println("--------------------------------------------------------------------------");
		}
		if (debug2) {
			System.out.println(result[step1.trace.size()-1][step2.trace.size()-1]/(step1.trace.size()+step2.trace.size())+","+faresult+","+oriresult+","+orirangeresult1+","+orirangeresult2+","+avgacce2dis+","+(result[step1.trace.size()-1][step2.trace.size()-1]/(step1.trace.size()+step2.trace.size())+faresult+oriresult+orirangeresult1+orirangeresult2+avgacce2dis));
		}
		return Math.max(0, result[step1.trace.size()-1][step2.trace.size()-1]/(step1.trace.size()+step2.trace.size())+faresult+oriresult+orirangeresult1+orirangeresult2+avgacce2dis);
	}
	
	public static double getsteppossi(Step newstep) {
		double stdresult[] = new double[std.length];
		double adaptresult[] = new double[adapt.length];
		double dis[] = new double[std.length+adapt.length];
		int validnum = 0;
		for (int i = 0; i < std.length; i++) {
			stdresult[i] = gettracedis(newstep, std[i]);
			dis[i] = stdresult[i];
			validnum++;
		}
		for (int i = 0; i < adapt.length; i++) {
			if (adapt[i].timedis != -1) {
				adaptresult[i] = gettracedis(newstep, adapt[i]);
				dis[validnum] = adaptresult[i];
				validnum++;
			} else {
				adaptresult[i] = -1;
			}
		}
		for (int i = 0; i < validnum; i++) {
			for (int j = i+1; j < validnum; j++) {
				if (dis[i] > dis[j]) {
					double tmp = dis[i];
					dis[i] = dis[j];
					dis[j] = tmp;
				}
			}
		}
		int adaptnum = Math.max(1, (validnum-std.length-1)/2);
		double finaldis = inf;
		for (int i = 0; i < adaptnum; i++) {
			double sum = 0;
			for (int j = 0; j <= i; j++) {
				sum += dis[j]*(i-j+1);
			}
			sum /= ((i+1)*(i+2))/2;
			sum *= 1+0.1*(adaptnum-1-i);
			if (sum < finaldis) finaldis = sum;
		}
		return finaldis;
	}
	
	public static int add(int t, double fa, double va, double ori) {
		int result = 0;
		if (timesub(t, laststeptime) > 300) {
			laststeptime = -1;
		}
		for (int i = 0; i < adapt.length; i++) {
			if (adapt[i].timedis != -1) {
				adapt[i].timedis++;
				if (adapt[i].timedis > 375) {
					adapt[i].timedis = -1;
					adapt[i].trace.clear();
				}
			}
		}
		curlist.add(new StepCycle().new Element(tnum, t, fa, va, ori));
		debug.add(new StepCycle().new Element(tnum, t, fa, va, ori));
		tnum++;
		while (timesub(t, curlist.get(0).time) > 150) {
			curlist.remove(0);
		}
		while (!recentsteplist.isEmpty() && timesub(t, recentsteplist.get(0).starttime) > 260) {
			recentsteplist.remove(0);
		}
		List<Integer> point = new ArrayList();
		for (int i = Math.max(0, curlist.size()-150); i < curlist.size()-1; i++) {
			Element e1 = curlist.get(i);
			Element e2 = curlist.get(i+1);
			if ((laststeptime == -1 || timesub(e1.time, laststeptime) >= -2) && e1.va <= 0 && e2.va > 0) {
				point.add(i);
			}
		}
		List<Step> newsteplist = new ArrayList();
		for (int i = 0; i < point.size(); i++) {
			int p = point.get(i);
			Element estart = curlist.get(p);
			for (int j = i+1; j < point.size(); j++) {
				int q = point.get(j);
				Element eend = curlist.get(q+1);
				if (timesub(eend.time, estart.time) >= 8 && timesub(eend.time, estart.time) <= 30) {
					Step newstep = new StepCycle().new Step();
					newstep.starttime = curlist.get(p).time;
					newstep.endtime = curlist.get(q+1).time;
					newstep.starttnum = curlist.get(p).tnum;
					newstep.endtnum = curlist.get(q+1).tnum;
					newstep.timedis = timesub(t, newstep.endtime);
					newstep.isstd = false;
					for (int k = p; k <= q+1; k++) {
						newstep.trace.add(curlist.get(k));
					}
					double steppossi = getsteppossi(newstep);
					if (steppossi < threshold1) {
						newsteplist.add(newstep);					
					}
				}
			}
		}
		if (newsteplist.isEmpty()) {
			return 0;
		}
		double distance[][] = new double[newsteplist.size()][newsteplist.size()];
		for (int i = 0; i < newsteplist.size(); i++) {
			distance[i][i] = inf;
			for (int j = i+1; j < newsteplist.size(); j++) {
				distance[j][i] = distance[i][j] = gettracedis(newsteplist.get(i), newsteplist.get(j));
				if (newsteplist.get(i).starttime == newsteplist.get(j).starttime) {
					distance[j][i] = distance[i][j] = inf;
				} else if (timesub(newsteplist.get(i).starttime, newsteplist.get(j).starttime) > 0 && timesub(newsteplist.get(i).starttime, newsteplist.get(j).endtime) < -2) {
					distance[j][i] = distance[i][j] = inf;
				} else if (timesub(newsteplist.get(j).starttime, newsteplist.get(i).starttime) > 0 && timesub(newsteplist.get(j).starttime, newsteplist.get(i).endtime) < -2) {
					distance[j][i] = distance[i][j] = inf;
				}			
			}
		}
		int needsimilarnum[] = new int[newsteplist.size()];
		double totaltimelengthsecond;
		if (recentsteplist.isEmpty()) {
			totaltimelengthsecond = 0.04*timesub(t, curlist.get(0).time);
		} else {
			totaltimelengthsecond = 0.04*timesub(t, recentsteplist.get(0).starttime);
		}
		for (int i = 0; i < needsimilarnum.length; i++) {
			double checktimelengthsecond = Math.min(totaltimelengthsecond, Math.max(6, 8*(0.04*timesub(newsteplist.get(i).endtime, newsteplist.get(i).starttime)+0.08)));
			needsimilarnum[i] = Math.max(3, (int)(checktimelengthsecond/2/(0.04*timesub(newsteplist.get(i).endtime, newsteplist.get(i).starttime)+0.08))-1);
			for (int j = 0; j < recentsteplist.size(); j++) {
				if (0.04*timesub(t, recentsteplist.get(j).starttime) <= checktimelengthsecond && gettracedis(recentsteplist.get(j), newsteplist.get(i)) < threshold2) {
					needsimilarnum[i]--;
					if (needsimilarnum[i] <= 0) {
						break;
					}
				}
			}
		}
		boolean valid[] = new boolean[newsteplist.size()];
		for (int i = 0; i < valid.length; i++) {
			valid[i] = true;
		}
		int overlapcheck = 0;
		do {
			boolean notchange = false;
			while (!notchange) {
				notchange = true;
				for (int i = 0; i < newsteplist.size(); i++) {
					if (valid[i] && needsimilarnum[i] > 0) {
						int needsimilarnumleft = needsimilarnum[i];
						for (int j = 0; j < newsteplist.size(); j++) {
							if (valid[j] && distance[i][j] < threshold2) {
								needsimilarnumleft--;
								if (needsimilarnumleft <= 0) {
									break;
								}
							}
						}
						if (needsimilarnumleft > 0) {
							notchange = false;
							valid[i] = false;
						}
					}
				}
			}
			while (overlapcheck < valid.length) {
				if (!valid[overlapcheck]) {
					overlapcheck++;
				} else {
					boolean change = false;
					for (int i = overlapcheck+1; i < valid.length; i++) {
						if (valid[i] && timesub(newsteplist.get(i).starttime, newsteplist.get(overlapcheck).endtime) < -1) {
							valid[i] = false;
							change = true;
							break;
						} else if (timesub(newsteplist.get(i).starttime, newsteplist.get(overlapcheck).endtime) >= -1) {
							break;
						}
					}
					if (change) {
						break;
					} else {
						overlapcheck++;
					}
				}
			}
		} while (overlapcheck < valid.length);	
		for (int i = 0; i < newsteplist.size(); i++) {
			if (valid[i]) {
				result++;
				recentsteplist.add(newsteplist.get(i));
				int change = 0;
				for (int j = 0; j < adapt.length; j++) {
					if (adapt[j].timedis == -1 || adapt[j].timedis > adapt[change].timedis) {
						change = j;
					}
				}
				if (adapt[change].timedis == -1) {
					adapt[change].endtime = newsteplist.get(i).endtime;
					adapt[change].starttime = newsteplist.get(i).starttime;
					adapt[change].timedis = timesub(t, adapt[change].endtime);
					adapt[change].trace.clear();
					for (Element e:newsteplist.get(i).trace) {
						adapt[change].trace.add(e);
					}
				}
				if (laststeptime == -1 || timesub(newsteplist.get(i).endtime, laststeptime) > 0) {
					laststeptime = newsteplist.get(i).endtime;
				}
				debug.get(newsteplist.get(i).starttnum).isstart = 1;
				debug.get(newsteplist.get(i).endtnum).isend = 1;
				for (int j = newsteplist.get(i).starttnum; j <= newsteplist.get(i).endtnum; j++) {
					debug.get(j).isstep = 1;
				}
			}
		}
		counter += result;
		return result;
	}
	
	public static void debugprint() {
		int counter = 0;
		for (Element e:debug) {
			if (e.isend == 1) {
				counter++;
			}
			System.out.println(e.tnum+","+e.time+","+e.fa+","+e.va+","+e.va*e.va+","+e.orientation+","+10*(2*e.isstep-e.isstart-e.isend)+","+counter);
		}
	}
	
}
