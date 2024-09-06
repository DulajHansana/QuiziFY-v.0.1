package application;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import org.json.*;

public class Main {
	private static Scanner consoleReader = new Scanner(System.in);
	
	private static String[] menuHolder[] = {{"Create", "Open", "Exit"}};
	
	public static void main(String[] args) throws IOException, InterruptedException {
		boolean loopContinue = true;
		while (loopContinue) {
			Filehandler.createOpens();
			clearConsole();
			printWelcome();
			menuOpener(0);
			
			switch (getInteger()) {
				case 1: {
					clearConsole();
					printWelcome();
					
					print("Home > Create\n", true);
					createQuizzes();
					consoleReader.nextLine();
					consoleReader.nextLine();
					break;
					
				} case 2: {
					clearConsole();
					printWelcome();
					
					print("Home > Open\n", true);
					openQuiz();
					consoleReader.nextLine();
					consoleReader.nextLine();
					break;
					
				} case 3: {
					loopContinue = false;
					break;
					
				} default: {
					print("\nNot supportive input", true);
					break;
				}
			}
		}
	}
	
	public static void openQuiz () {
		print("Use following quizzes", true);
		int i = 0;
		boolean available= false;
		String[] filesList = Filehandler.openDirectory(); 
		
		for (String fileName: filesList) {
			if (fileName != null) {
				available = true;
				print((i + 1) + ". " + fileName, true);
				i ++;
			}
		}
		
		if (available) {
			print("\nEnter quiz number: ", false);
			int option = getInteger();
			
			if (option > 0 && option < 100 && filesList[option - 1] != null) {
				print("\nQuiz password: ", false);
				String password = getWord();
				
				String quizContent = Filehandler.readFile(filesList[option - 1], password);
				
				try {
					JSONObject decryptedJson = new JSONObject(quizContent);
					JSONObject quizDetails = decryptedJson.getJSONObject("config"); 
					JSONObject quizData = decryptedJson.getJSONObject("content");
					
					clearConsole();
					printWelcome();
					
					print("Home > Open\n", true);
					
					print("Quiz configuration", true);
					print("\tID: " + quizDetails.getString("ID"), true);
					print("\tName: " + quizDetails.getString("name"), true);
					
					print("", true);
					
					int questionNumber =  1;
					int correctAnswers =  0;
					while (true) {
						try {
							String question = quizData.getJSONObject(String.valueOf(questionNumber)).getString("question");
							print("(" + questionNumber + "). " + question, true);
						} catch (Exception error) {
							break;
						}
						String option_01 = quizData.getJSONObject(String.valueOf(questionNumber)).getString("option1");
						String option_02 = quizData.getJSONObject(String.valueOf(questionNumber)).getString("option2");
						String option_03 = quizData.getJSONObject(String.valueOf(questionNumber)).getString("option3");
						String option_04 = quizData.getJSONObject(String.valueOf(questionNumber)).getString("option4");
						int answer = quizData.getJSONObject(String.valueOf(questionNumber)).getInt("answer");
						
						print("\t1. " + option_01, true);
						print("\t2. " + option_02, true);
						print("\t3. " + option_03, true);
						print("\t4. " + option_04, true);
						
						int userAnswer = getInteger();
						
						while (userAnswer < 0 || userAnswer > 4) {
							print("Answer should be in range of 1 - 4\n", true);
							userAnswer = getInteger();
						}
						
						if (userAnswer == answer) {
							print("✔ Yes, " + answer + " is the correct answer!\n", true);
							correctAnswers ++;
						} else {
							print("❌ No, answer " + answer + " is the correct answer.\n", true);
						}
						
						questionNumber ++;
					}
					print("🏆 You scored " + correctAnswers + " out of " + (questionNumber - 1) + "!", true);
					
				} catch (Exception error) {
					print("Error with quiz content", true);
					return;
				}
			} else {
				print("\nNo quiz is available for " + option + "\n", true);
			}
		} else {
			print("\nNo quiz files available in Documents/Quizify/Opens", true);
			return;
		}
	}
	
	public static void createQuizzes() {
		JSONObject quiz = new JSONObject();
		JSONObject config = new JSONObject();
		
		print("Quiz configuration", true);
		print("\tID: ", false);
		String id = generateID();
		print(id, true);
		config.put("ID", id);
		
		print("\tName: ", false);
		String name = consoleReader.next();
		config.put("name", name);
		
		print("\tPassword: ", false);
		String password = consoleReader.next();
		config.put("password", password);
		
		print("\nQuiz designing", true);
		
		int quizNumber = 1;
		JSONObject content = new JSONObject();
		while (true) {
			print(quizNumber + ". Question: ", false);
			consoleReader.nextLine();
			String question = consoleReader.nextLine();
			
			if (question.length() == 0) {
				print("Designing quiz is terminated.\n", true);
				break;
			}
			
			print("Option 1: ", false);
			String option_01 = consoleReader.nextLine();
			print("Option 2: ", false);
			String option_02 = consoleReader.nextLine();
			print("Option 3: ", false);
			String option_03 = consoleReader.nextLine();
			print("Option 4: ", false);
			String option_04 = consoleReader.nextLine();
			
			print("Answer: Option ", false);
			int answer = consoleReader.nextInt();
			
			JSONObject quizContent = new JSONObject();
			quizContent.put("question", question);
			quizContent.put("option1", option_01);
			quizContent.put("option2", option_02);
			quizContent.put("option3", option_03);
			quizContent.put("option4", option_04);
			quizContent.put("answer", answer);
			
			if (question.length() > 3 && answer < 5 && answer > 0 && password.length() > 0) {
				content.put(String.valueOf(quizNumber), quizContent);
				quizNumber++;
				print("", true);
				continue;
				
			} else {
				print("\nQuestion (" + quizNumber + ") is ignored due to be not valied.\n", true);
			}
		}
		
		quiz.put("config", config);
		quiz.put("content", content);
		
		Filehandler.writeFile(name, password, quiz.toString());
	}
	
	public static void printWelcome() {
		System.out.println("\t=== Quizify ===\ncreate, share, and master quizzes");
		System.out.println("---------------------------------");
	}
	
	public static void clearConsole() throws IOException, InterruptedException {
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}
	
	public static void menuOpener(Integer menuID) {
		int titleIndex = 1;
		
		for (String title: menuHolder[menuID]) {
			System.out.println(titleIndex + " - " + title);
			titleIndex++;
		}
	}
	
	public static int getInteger() {
		print("\n<- Input number: ", false);
		return consoleReader.nextInt();
	}
	
	public static String getWord() {
		print("\n<- Input word: ", false);
		return consoleReader.next();
	}
	
	public static void print(String text, boolean nextLine) {
		if (nextLine) {
			System.out.println(text);
		} else {
			System.out.print(text);
		}
	};
	
	public static String generateID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}