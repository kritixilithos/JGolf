import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Exception;

public class compile {
	public static void main(String[] args) {
		String code = ""; //THE JGolf code
		String filename = "A"; //The default filename if none present in args
		if(args.length > 0) filename = args[0]; //get filename (without extension)
		String verbose = 
			"import java.io.*;\n"
			+"import java.lang.*;\n"
			+"import java.util.*;\n\n"
			+"public class "+filename+" {\n"
			+"\tpublic static void main(String[]args) {\n"; //default stuff for java
		String ending =
			"\t}"
			+"}"; //the ending parenthesis


		String line = null; //initialising at null for reading file
		//reading file
		try {
			FileReader fileReader = new FileReader(filename+".jgolf");//filename.jgolf
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				code+=(line+"\n");//adding lines to code with new line with \n
			}
		}catch(Exception e) {
			System.err.println("Exception, failed to reader file: " + e);
		}

		System.out.println("compile.java: Read file "+filename+".jgolf");

		//Replace "P(" with System.out.println(
		Pattern PprintLine = Pattern.compile("P\\|");//Compiling printLine regex
		Matcher MprintLine = PprintLine.matcher(code);//getting matcher obj for ^
		code = MprintLine.replaceAll("System.out.println(\\|");//replacing for ^^

		Pattern Pstringify = Pattern.compile("\\|([^\\|]*)\\|");//finding strings in |asdasd| format
		Matcher Mstringify = Pstringify.matcher(code);//getting matcher for ^
		code = Mstringify.replaceAll("\"$1\")");//replacing for ^^
		
		System.out.println("compile.java: Compiled code");//TODO: added syntax changes

		//write as a java file
		try {
			PrintWriter writer = new PrintWriter((filename+".java"), "UTF-8");
			writer.println(verbose);//clutter
			writer.println(code);//code, TODO: add syntax changes
			writer.println(ending);//ending brackets
			writer.close();
		}catch(Exception e) {
			System.err.println("Error caught while reading file:  " + e);
		}

		System.out.println("compile.java: Finished writing "+filename+".java");
	}
}
