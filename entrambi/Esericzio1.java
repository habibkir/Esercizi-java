package entrambi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Esericzio1 {
	public static void man(String[] args)
	/* se mettevo due main nel package si arrabbiava, piccolo bambino ritardato */
			throws FileNotFoundException
			,IOException
			,InterruptedException {
		int[] frequenze = new int[101];
		
		File file = new File("src\\data.txt");
		Scanner scanner = new Scanner(file);
		Semaphore scannerAccess = new Semaphore(1);
		
		long startingTime = System.currentTimeMillis();

		DataThread[] dt = new DataThread[1];
		for(int i = 0; i<dt.length; ++i) 
			dt[i] = new DataThread(scanner, scannerAccess);
		for(int i = 0; i<dt.length; ++i)
			dt[i].start();
		for(int i = 0; i<dt.length; ++i) {
			dt[i].join();
			for(int j = 0; j<101; ++j) {
				frequenze[i] += dt[i].frequenze[j];
			}
		}
		scanner.close();
		for(int i = 0; i< 101; ++i) {
			System.out.println(i + " : " + frequenze[i]);
		}

		System.out.println("time taken : " + (System.currentTimeMillis()-startingTime));
	}
}


class DataThread extends Thread {
	Scanner scanner;
	Semaphore scannerAccess;
	public int[] frequenze = new int[101];
	
	public DataThread(Scanner scanner, Semaphore scannerAccess) {
		this.scanner = scanner;
		this.scannerAccess = scannerAccess;
	}
	
	@Override
	public void run() {
		try {
			int num;
			while(!interrupted()) {
				scannerAccess.acquire();
				if(scanner.hasNext()) {
					num = scanner.nextInt();
					scannerAccess.release();
					frequenze[num]++;
					System.out.println(getName() + " : " + num);
				} else {
					scannerAccess.release();
					System.out.println
					("File is over, thought I'd something more to read : "
					+ getName());
					break;
				}
			}
		} catch(InterruptedException e) {
			System.out.println
			("To be interrutped, to sleep, to sleep perhance to dream\n~"
			+ getName());
		}
	}
}