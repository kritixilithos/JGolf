import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Exception;

/**
 * NOTES: input is accepted through args:
 *  a = args        A = length(1)
 *  r = int(0)      R = double(0.0)
 *  g = char(' ')   G = char array(null)
 *  s = string("")  S = args as string(" ")
 *  r,R,G,s are all for args[0] where g is the first character in args[0]
 *  Parenthesis indicate the default initialised value of the variables
 */

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
			+"\tpublic static void main(String[]a) {\n"; //default stuff for java
		String argDeclare =
			"\t\tString s=\"\";\n"
			+"\t\tString S=\" \";\n"
			+"\t\tint r=0;\n"
			+"\t\tchar g=\' \';\n"
			+"\t\tchar G[] = null;\n"
			+"\t\tdouble R = 0.0;\n"
			+"\t\tint A = 1;\n"
			+"\t\tif(a.length > 0) {\n"
			+"\t\ts = a[0];\n"
			+"\t\tS = String.join(\" \", a);\n"
			+"\t\ttry {\n"
			+"\t\t\tr = Integer.parseInt(a[0]);\n"
			+"\t\t\tg = a[0].charAt(0);\n"
			+"\t\t\tG = a[0].toCharArray();\n"
			+"\t\t\tR = Double.parseDouble(a[0]);\n"
			+"\t\t}catch(Exception e){\n"
			+"\t\t}\n"
			+"\t\tA = a.length;\n"
			+"\t\t}\n";//a is args. Default values are listed above
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

			//regex for functions and methods with return/void type
			Pattern Pmethod = Pattern.compile("([iScdf])([a-zA-Z])\\(([^(]*)\\)\\{");
			Matcher Mmethod = Pmethod.matcher(statement);
			if(Mmethod.find()) {
				statement = Mmethod.replaceAll(returnType(Mmethod.group(1))+" $2($3){");
			}

			//regex for if/else if statements
			Pattern Pelif = Pattern.compile("(e?)F\\(");
			Matcher Melif = Pelif.matcher(statement);
			if(Melif.find()) {
				if(Melif.group(1).equals("e"))
					statement = Melif.replaceAll("else if(");
				else
					statement = Melif.replaceAll("if(");
			}

			Pattern Pelse = Pattern.compile("\\}e\\{");
			Matcher Melse = Pelse.matcher(statement);
			statement = Melse.replaceAll("}else{");

			//Pattern for for-loop f(var, start, end, increment){
			Pattern Pfor = Pattern.compile("f\\(([a-zA-Z]),([-]?[\\d]+),([-]?[\\d]+),([-]?[\\d]+)\\)");//with support for negative numbers
			Matcher Mfor = Pfor.matcher(statement);
			if(Mfor.find()) {
				if(Integer.parseInt(Mfor.group(2)) > Integer.parseInt(Mfor.group(3))) {
					statement = Mfor.replaceAll("for(int $1 = $2; $1 >= $3; $1 += $4){");
				}else{
					statement = Mfor.replaceAll("for(int $1 = $2; $1 <= $3; $1 += $4){");
				}
			}
			//System.out.println(statement);
			//Replace "P(" with System.out.println(
			Pattern Pprint = Pattern.compile("([Pp])([\\|\\(])");//Compiling printLine regex
			Matcher Mprint = Pprint.matcher(statement);//getting matcher obj for ^
			String javaPrint = "System.out.print";
			if(Mprint.find()) {
				if(Mprint.group(1).equals("P")) {//checking if it is P[\(\|] or p[\(\|]
					javaPrint = "System.out.println";
				}
				/*if(Mprint.group(2).equals("|")) {
					statement = Mprint.replaceAll(javaPrint + "(|");//replacing for [Pp]|...
				}else*/ if(Mprint.group(2).equals("(")) {
					statement = Mprint.replaceAll(javaPrint + "(");//replacing for [Pp](..
				}
			}/*
			Pattern Pstringify = Pattern.compile("\\|([^\\|]*)\\|");//finding strings in |asdasd| format
			Matcher Mstringify = Pstringify.matcher(statement);//getting matcher for ^
			statement = Mstringify.replaceAll("\"$1\")");//replacing for ^^*/

			statements[i] = statement;//Adding the changes to statements
		}

		System.out.println("compile.java: Transpiled JGolf code to Java");//TODO: added syntax changes

		//write as a java file
		try {
			PrintWriter writer = new PrintWriter((filename+".java"), "UTF-8");
			writer.println(verbose);//clutter
			writer.println(argDeclare);//arg variables
			for(String statement:statements) {//iterates through the statements
				if(statement!=null) {
					if(statement.charAt(statement.length()-1) == '}')
						writer.println(statement);
					else
						writer.println(statement+";");
				}
			}
			//writer.println(code);//code, TODO: add syntax changes
			writer.println(ending);//ending brackets
			writer.close();
		}catch(Exception e) {
			System.err.println("Error caught while reading file:  " + e);
		}

		System.out.println("compile.java: Finished writing "+filename+".java");

		}

		static String returnType(String s) {
			switch(s) {
				case "i":
					return "int";
				case "S":
					return "String";
				case "c":
					return "char";
				case "d":
					return "double";
				case "f":
					return "float";
				case "v":
					return "void";
				default:
					return "";
			}
		}
	}
