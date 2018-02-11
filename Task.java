import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Task {
	
	private static List<File> readFromZip(String str, File file) {
        try {
        	List<File> list = new ArrayList<>();
            ZipFile zip = new ZipFile(str);
            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File newFile;
                if (entry.isDirectory()) {
                    new File(file.getParent(), entry.getName()).mkdirs();
                } else {
                    write(zip.getInputStream(entry),
                         new BufferedOutputStream(new FileOutputStream(newFile =
                             new File(new File(entry.getName()).getName()))));
                    list.add(newFile);
                }
            }
            zip.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void write(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int len;
	    while ((len = in.read(buffer)) >= 0)
		    out.write(buffer, 0, len);
	    out.close();
	    in.close();
	}
    
    private static Map<Integer, String> readAndWrite(List<File> list) {
    	if (list == null) {
    		return null;
    	}
    	Map<Integer, String> result = new TreeMap<>((a,b) -> b.compareTo(a));
    	for (File file : list) {
    		String outFileName;
    		try (BufferedReader buff = new BufferedReader(new FileReader(file));
    				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName = "out" + 
    						file.getName().substring(2, file.getName().length()-4) + ".txt")));){
    			int numLines = Integer.valueOf(buff.readLine());
    			int numFig = Integer.valueOf(buff.readLine());
    			int lineCounter = 0;
    			int fileTotalSum = 0;
        		String line;
    			while((line = buff.readLine()) != null) {
    				List<String> figuresInLine = Arrays.asList(line.split(" "));
    				lineCounter++;
    				if (lineCounter <= numLines) {
    				if (figuresInLine.size() < numFig) {
    					writer.write(lineCounter + " : Error, найдено " + figuresInLine.size() + " чисел вместо " + numFig + "\n");
    				} else {
    					int lineSum = figuresInLine.stream().limit(numFig).mapToInt(e -> Integer.valueOf(e)).sum();
    					int lineMax = figuresInLine.stream().limit(numFig).mapToInt(e -> Integer.valueOf(e)).max().getAsInt();
    					int lineMin = figuresInLine.stream().limit(numFig).mapToInt(e -> Integer.valueOf(e)).min().getAsInt();
    					writer.write(lineCounter + " : " + "sum=" + lineSum + " : " + "max=" + lineMax + " : " + "min=" + lineMin + "\n");
    				}
    				}
    				fileTotalSum += figuresInLine.stream().mapToInt(e -> Integer.valueOf(e)).sum();
    			}
    			if (lineCounter < numLines) {
    				writer.close();
    				writeWrongInFile(outFileName, lineCounter, numLines);
    				continue;
    			}
    			writer.write("total=" + fileTotalSum);
    			result.put(fileTotalSum, outFileName);
    			}
    		 catch (IOException e) {
    			 e.printStackTrace();
    		}
    	}
    	return result;
    }
    
    private static void writeWrongInFile(String outFileName, int lineCounter, int numLines) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName)));){
    		writer.write( "Error, найдено " + lineCounter + " строки вместо " + numLines + "\n");
    		writer.write("total=0");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    private static void printResult(Map<Integer, String> result) {
    	if (result == null) {
    		return;
    	}
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("result.txt")));){
    		for (Map.Entry<Integer, String> entry : result.entrySet()) {
    			writer.write(entry.getValue() + "\n");
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
	
	public static void main(String[] args) {
		if (args.length != 1) {
            System.out.println("Usage: Task [zipfile]");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists() || !file.canRead()) {
            System.out.println("File cannot be read");
            return;
        }
        List<File> filesFromZip = readFromZip(args[0], file);
        Map<Integer, String> result = readAndWrite(filesFromZip);
        printResult(result);
	}
}