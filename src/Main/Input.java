package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Input {

	public static int test(File inputfile) {
		FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
			fis = new FileInputStream(inputfile);
	        isr = new InputStreamReader(fis);
	        br = new BufferedReader(isr);
	        String line;
	        boolean firstline = true;
	        while ((line = br.readLine()) != null) {
	        	String readnum[] = line.split(",");
	        	int time = Integer.parseInt(readnum[0]);
	        	double horiacce = Double.parseDouble(readnum[1]);
	        	double veracce = Double.parseDouble(readnum[2]);
	        	double angle = Double.parseDouble(readnum[3]);
	        	if (firstline) {
	        		DynamicTimeWarping.reset();
	        		firstline = false;
	        	}
	        	DynamicTimeWarping.add(time, horiacce, veracce, angle);
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
        //return DynamicTimeWarping.getcounter();
        return DynamicTimeWarping.getcounter();		
	}
	
	public static void main(String args[]) {
		DynamicTimeWarping.init();
		//DynamicTimeWarping.debug1 = true;
		//DynamicTimeWarping.debug2 = true;
		/*try {
			System.setOut(new PrintStream(new FileOutputStream("out.csv")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int result = test(new File("C:\\Users\\ArsenHo\\Desktop\\testdata\\2.2统计\\长裤前袋走\\12-100.csv"));
		DynamicTimeWarping.debugprint();*/
		//System.out.println(result);
		
		
		File root = new File("C:\\Users\\ArsenHo\\Desktop\\testdata\\2.2统计");
		for (File dir:root.listFiles()) {
			System.out.println(dir.getName());
			for (File inputfile:dir.listFiles()) {
				//System.out.println(dir.getName()+"\\\\"+inputfile.getName());
				double x = Double.parseDouble((inputfile.getName().replace(".csv", "").split("-"))[1]);
				double y = test(inputfile);
				System.out.print(Math.abs(y-x)/x+" ");
			}
			System.out.println();
		}
	}
	
}
