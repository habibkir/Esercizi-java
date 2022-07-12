package entrambi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;


class Esercizio2 {
	public static void main(String[] args) 
			throws FileNotFoundException
			,IOException
			,InterruptedException {
		/* there is no trying, only throw */

		File file = new File("src\\rubrica.txt");
		Scanner scanner = new Scanner(file);
		Semaphore scannerAccess = new Semaphore(1);
		Semaphore personeAccess = new Semaphore(1);
		ArrayList<Persona> persone = new ArrayList<>();
		
		/* time to doodoo dispatch */
		PersoneThread pt[] = new PersoneThread[10];
		for(int i = 0; i<pt.length; ++i)
			pt[i] = new PersoneThread(scanner,scannerAccess,persone,personeAccess);
		for(int i = 0; i<pt.length; ++i)
			pt[i].start();
		for(int i = 0; i<pt.length; ++i)
			pt[i].join();
		
		queries(persone, new Scanner(System.in));
	}
	
	static void queries(ArrayList<Persona> persone, Scanner scanner) {
		String query;
		do {
			query = scanner.nextLine();
			/* si portrebbero usare dei thread
			 * anche qui se fai che hai facciamo
			 * 10 thread che gestscono onguno
			 * un decimo dell'arraylist
			 * non ho lo sbatti di fare altre classi */
			for(Persona p : persone) {
				if(p.name.toLowerCase().contains(query.toLowerCase())) {
					System.out.println(p.data);
				}
			}
		} while(!(query.toLowerCase().equals("fine")));
	}
}

class Persona {
	String name;
	String data;
	
	public Persona(String name, String data) {
		this.name = name;
		this.data = data;
	}
}

class PersoneThread extends Thread {
	Scanner scanner;
	Semaphore scannerAccess;
	ArrayList<Persona> persone;
	Semaphore personeAccess;
	
	public PersoneThread(Scanner scanner,
			Semaphore scannerAccess,
			ArrayList<Persona> persone,
			Semaphore personeAccess) {
		this.scanner = scanner;
		this.scannerAccess = scannerAccess;
		this.persone = persone;
		this.personeAccess = personeAccess;
	}
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
				scannerAccess.acquire();
				if(scanner.hasNextLine()) {
					String s = scanner.nextLine();
					scannerAccess.release();

					String[] sArr = s.split(":");
					Persona p = new Persona(sArr[0], sArr[1]);
					System.out.println("abemus un " + p.name + " che è " + p.data);

					personeAccess.acquire();
					persone.add(p);
					personeAccess.release();
				}
				else {
					scannerAccess.release();
					System.out.println("Goodbye " + getName());
					break;
				}
			}
		} catch(InterruptedException e) {
			System.out.println("I'd like to interrupt you for a moment, "
		+ "what you're refearing to as " + getName()
		+ " is actually GNU/" + getName());
		}
	}
}