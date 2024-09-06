package application;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.*;
import org.apache.commons.io.FileUtils;

public class Filehandler {
	
	public static void createOpens () {
		File folder = new File(System.getProperty("user.home") + "/Documents/Quizify/Opens");
		folder.mkdirs();
	}

	public static void writeFile (String fileName, String password, String content) {
		File file = new File(System.getProperty("user.home") + "/Documents/Quizify/Creations/" + fileName + ".json");
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileWriter jsonFile = new FileWriter(System.getProperty("user.home") + "/Documents/Quizify/Creations/" + fileName + ".json");
			jsonFile.write(content);
            System.out.println("Quiz file was saved in " + System.getProperty("user.home") + "\\Documents\\Quizify\\Creations\\" + fileName + ".json");
            jsonFile.close();
            makeSharable(fileName, password, content, System.getProperty("user.home") + "/Documents/Quizify/Creations/" + fileName + ".json");
		} catch (IOException e) {
			System.out.println("An error occurred while writing to file.");
		}
	}
	
	public static void makeSharable(String name, String password, String content, String filePath) {
		String inputFilePath = filePath;
        String outputFilePath = System.getProperty("user.home") + "/Documents/Quizify/Sharable/" + name + ".qzf";
        
        try {
            File inputFile = new File(inputFilePath);
            String jsonStr = FileUtils.readFileToString(inputFile, "UTF-8");
            JSONObject json = new JSONObject(jsonStr);

            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(password);
            String encryptedJsonStr = encryptor.encrypt(json.toString());

            File outputFile = new File(outputFilePath);
            FileUtils.writeStringToFile(outputFile, encryptedJsonStr, "UTF-8");

            System.out.println("Sharable file was saved in " +  System.getProperty("user.home") + "\\Documents\\Quizify\\Sharable\\" + name + ".qzf");
            System.out.println("\tName: " + name);
            System.out.println("\tPassword: " + password);
            
        } catch (IOException e) {
            System.err.println("Error reading/writing file");
        } catch (Exception e) {
            System.err.println("Error encrypting file");
        }
		
	}
	
	public static String readFile (String fileName, String password) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        
        File inputFile = new File(System.getProperty("user.home") + "/Documents/Quizify/Opens/" + fileName);
		try {
			String encryptedJsonStrFromFile = FileUtils.readFileToString(inputFile, "UTF-8");
			String decryptedJsonStr = encryptor.decrypt(encryptedJsonStrFromFile);
			return decryptedJsonStr;
		} catch (Exception error) {
			System.out.println("Error decrypting file");
			return "";
		}
        
	}
	
	public static String[] openDirectory () {
		String directoryPath = System.getProperty("user.home") + "/Documents/Quizify/Opens";
        String fileExtension = ".qzf";
        String[] filesList = new String[100];
        
        File directory = new File(directoryPath);
        File[] fileList = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(fileExtension));
        
        if (fileList != null && fileList.length > 0) {
        	int i = 0;
            for (File file : fileList) {
                filesList[i] = file.getName();
                i++;
            }
        } else {
            System.out.println("No files found with extension " + fileExtension);
        }
        
        return filesList;
	}

}
