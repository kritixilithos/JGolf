import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Exception;

public class compile {
	public static void main(String[] args) {
		String code = ""; //THE JGolf code
		String[]statements;//Contains individual statements in the code
		String filename = "A"; //The default filename if none present in args
		if(args.length > 0) filename = args[0]; //get filename (without extension)
		String verbose = 
			"import java.io.*;\n"
			+"import java.lang.*;\n"
			+"import java.util.*;\n\n"
			+"public class "+filename+" {\n"
			+"\tpublic static void main(String[]args) {\n"; //default stuff for java
		String ending =
			"\t}\n"
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

		statements = code.split(";");

		System.out.println("compile.java: Read file "+filename+".jgolf");

		//iterator through all the statements
		for(int i = 0; i < statements.length; i++) {
			String statement = statements[i];
			//Replace "P(" with System.out.println(
			Pattern Pprint = Pattern.compile("([Pp])([\\|\\(])");//Compiling printLine regex
			Matcher Mprint = Pprint.matcher(statement);//getting matcher obj for ^
			String javaPrint = "System.out.print";
			if(Mprint.find()) {
				if(Mprint.group(1).equals("P")) {//checking if it is P[\(\|] or p[\(\|]
					javaPrint = "System.out.println";
				}
				if(Mprint.group(2).equals("|")) {
					statement = Mprint.replaceAll(javaPrint + "(|");//replacing for [Pp]|...
				}else if(Mprint.group(2).equals("(")) {
					statement = Mprint.replaceAll(javaPrint + "(");//replacing for [Pp](...
				}
			}
			Pattern Pstringify = Pattern.compile("\\|([^\\|]*)\\|");//finding strings in |asdasd| format
			Matcher Mstringify = Pstringify.matcher(statement);//getting matcher for ^
			statement = Mstringify.replaceAll("\"$1\")");//replacing for ^^

			statements[i] = statement;//Adding the changes to statements
		}

		System.out.println("compile.java: Compiled code");//TODO: added syntax changes

		//write as a java file
		try {
			PrintWriter writer = new PrintWriter((filename+".java"), "UTF-8");
			writer.println(verbose);//clutter
			for(String statement:statements) {//iterates through the statements
				if(statement!=null) writer.println(statement+";");
			}
			//writer.println(code);//code, TODO: add syntax changes
			writer.println(ending);//ending brackets
			writer.close();
		}catch(Exception e) {
			System.err.println("Error caught while reading file:  " + e);
		}

		System.out.println("compile.java: Finished writing "+filename+".java");
	}
}
