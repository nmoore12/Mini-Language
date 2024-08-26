import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProjectOne {
    private static BufferedReader reader;
    private static String currentChar;
    private static char nextChar;
    private static String tokenValue;
    private static ArrayList<Position> tokenPos = new ArrayList<Position>();
    private static Position pos;
    private static ArrayList<String> tokens = new ArrayList<String>();
    private static int index = 0;
    private static int currentLine = 1;
    private static int currentColumn = 0;

    /*
     * Method for reading the next character from the file.
     */
    public static void nextChar() throws IOException { 
        int next = reader.read();
        nextChar = (char)next;
    }

    /*
     * Method for keeping track of the current char being read from the file.
     */
    public static String currentChar() {
        try {
            int currChar = reader.read(); //read character from file

            if(currChar == -1) {          //if the char equals -1(end of text)
                return "end-of-text";               //return ET(X) (end of text)
            } else {                      //otherwise, return the current char          
                char current = (char)currChar;
                return Character.toString(current);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "end-of-text";
    }

    /*
     * Method for returning the kind of lexeme read from the file.
     */
    public static String kind() {
        if(keywords().contains(tokenValue)) {
            return tokenValue;
        } else if(symbols().contains(tokenValue)) {
            return "'"+tokenValue+"'";
        } else if(tokenValue.chars().allMatch(Character :: isDigit)) {
            return "'NUM'";
        } else  if(tokenValue.equals("end-of-text")) {
            return "'end-of-text'";
        } else {
            return "'ID'";
        }
    }

    /*
     * Method for returning the value of the lexeme read from the file.
     */
    public static String value() {
        if(kind().equals("'ID'")) {
            return "'"+tokenValue+"'";
        } else if(kind().equals("'NUM'")) {
            return "'"+tokenValue+"'";
        } else  if(currentChar.equals("'end-of-text'")) {
            return "";
        } else {
            return "";
        }
    }

    /*
     * Method for returning the position of the lexeme.
     */
    public static String position() {
        pos = new Position(currentLine, (currentColumn-tokenValue.length()+1));
        tokenPos.add(pos);
        return currentLine + ":" + (currentColumn-tokenValue.length()+1);
    }

    /*
     * Helper class for storing token position.
     */
    private static class Position {
        private int col;
        private int line;

        public Position(int line, int col) {
            this.line = line;
            this. col = col;
        }

        public int getCol() {
            return col;
        }

        public int getLine() {
            return line;
        }
    }

    /*
     * Method for loading all of the keywords accepted by the grammar.
     */
    private static List<String> keywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add("program");
        keywords.add("bool");
        keywords.add("int");
        keywords.add("if");
        keywords.add("then");
        keywords.add("else");
        keywords.add("end");
        keywords.add("while");
        keywords.add("do");
        keywords.add("end");
        keywords.add("print");
        keywords.add("or");
        keywords.add("mod");
        keywords.add("and");
        keywords.add("not");
        keywords.add("false");
        keywords.add("true");

        return keywords;
    }

    /*
     * Method for loading all of the symbols accepted by the grammar.
     */
    private static HashSet<String> symbols() {
        HashSet<String> allowedSymbols = new HashSet<>();
        allowedSymbols.add(":");
        allowedSymbols.add(".");
        allowedSymbols.add(",");
        allowedSymbols.add(";");
        allowedSymbols.add("=");
        allowedSymbols.add("!=");
        allowedSymbols.add("<");
        allowedSymbols.add(">");
        allowedSymbols.add("+");
        allowedSymbols.add("-");
        allowedSymbols.add("*");
        allowedSymbols.add("/");
        allowedSymbols.add("(");
        allowedSymbols.add(")");
        allowedSymbols.add("_");

        return allowedSymbols;
    }


    /*
     * SYNTAX ANALYZER:
     */

     /*
      * Method for iterating through tokens.
      */
    public static void next() {
        index++;
    }

    /*
     * Method for matching current symbol with the expected symbol in the grammar.
     */
    public static void match(String symbol) {
        if(tokens.get(index).equals(symbol)) {  //if the current token index equals the symbol to be matched...
            next();                             //call next() 
        } else {                                //else, print error message with token, expected symbol, and position. terminate.
            System.err.println("Bad symbol >>> '" + tokens.get(index) + "' at " + tokenPos.get(index).getLine() + ":" + tokenPos.get(index).getCol() + ". Expected: " + symbol);
            System.exit(0);
        }

        if(tokens.get(index).equals("end-of-text")) {
            System.out.println("Success.");
            index = 0;
        } 
    }

    /*
     * PROGRAM: 
     */
    public static void program(ArrayList<String> follow) {
        match("program");
        match("'ID'");
        match(":");
        ArrayList<String> followBody = new ArrayList<String>();
        followBody.add(".");
        body(followBody);
        match(".");
    }

    /*
     * BODY: 
     */
    public static void body(ArrayList<String> follow) {
        if(tokens.get(index).equals("bool") || tokens.get(index).equals("int")) {
            ArrayList<String> followDecs = new ArrayList<String>();
            followDecs.add("'ID'");
            followDecs.add("if");
            followDecs.add("while");
            followDecs.add("print");
            declarations(followDecs);
        }
        ArrayList<String> followState = new ArrayList<String>();
        for(String f : follow) {
            followState.add(f);
        }
        statements(followState);
    }

    /*
     * DECLARATIONS: 
     */
    public static void declarations(ArrayList<String> follow) {
        ArrayList<String> followDec = new ArrayList<String>();
        followDec.add("bool");
        followDec.add("int");
        for(String f : follow) {
            followDec.add(f);
        }
        declaration(followDec);
        while(tokens.get(index).equals("bool") || tokens.get(index).equals("int")) {
            declaration(followDec);
        }
        expected(followDec);
    }

    /*
     * DECLAATION:
     */
    public static void declaration(ArrayList<String> follow) {
        if(tokens.get(index).equals("bool") || tokens.get(index).equals("int")) {
            next();
        } else {
            System.err.println("Bad symbol >>> '" + tokens.get(index) + "' at " + tokenPos.get(index).getLine() + ":" + tokenPos.get(index).getCol() + ". Expected 'bool' or 'int'");
            System.exit(0);
        }
        match("'ID'");
        while(tokens.get(index).equals(",")) {
            match("'ID'");
        }
        match(";");
        expected(follow);
    }

    /*
     * STATEMENTS:
     */
    public static void statements(ArrayList<String> follow) {
        ArrayList<String> followState = new ArrayList<String>();
        followState.add(";");
        for(String f : follow) {
            followState.add(f);
        }
        statement(followState);
        while(tokens.get(index).equals(";")) {
            next();
            statement(followState);
        }
        expected(followState);
    }

    /*
     * STATEMENT:
     */
    public static void statement(ArrayList<String> follow) {
        if(tokens.get(index).equals("'ID'")) {
            assignment(follow);
        } else if(tokens.get(index).equals("if")){
            conditional(follow);
        } else if(tokens.get(index).equals("while")) {
            iterative(follow);
        } else if (tokens.get(index).equals("print")) {
            printStatement(follow);
        } else {
            ArrayList<String> expectedSymbols = new ArrayList<String>();
            expectedSymbols.add("'ID'");
            expectedSymbols.add("if");
            expectedSymbols.add("while");
            expectedSymbols.add("print");
            expected(expectedSymbols);
        }
    }

    /*
     * Method for expected symbols.
     */
    public static void expected(ArrayList<String> symbols) {
        if(!symbols.contains(tokens.get(index))) { //if the current symbol is not in the expected list of symbols...
            System.out.println("Bad symbol >>> '" + tokens.get(index) + "' at " + tokenPos.get(index).getLine() + ":" + tokenPos.get(index).getCol() + ". Expected symbol(s): ");
            for(String s : symbols) {
                System.out.print("'" + s + "' , ");  //print expected symbols, then print the current symbol, terminate.
            }
            System.out.println();
            System.exit(0);
        }
    }

    /*
     * ASSIGNMENT: 
     */
    public static void assignment(ArrayList<String> follow) {
        match("'ID'");
        match(":=");
        expression(follow);
    }

    /*
     * CONDITIONAL: 
     */
    public static void conditional(ArrayList<String> follow) {
        match("if");
        ArrayList<String> followExpr = new ArrayList<String>();
        followExpr.add("then");
        expression(followExpr);
        match("then");
        ArrayList<String> followBody = new ArrayList<String>();
        followBody.add("end");
        followBody.add("else");
        body(followBody);
        if(tokens.get(index).equals("else")) {
            next();
            ArrayList<String> followBodyElse = new ArrayList<String>();
            followBodyElse.add("end");
            body(followBodyElse);
        }
        match("end");
    }

    /*
     * ITERATIVE: 
     */
    public static void iterative(ArrayList<String> follow) {
        match("while");
        ArrayList<String> followExr = new ArrayList<String>();
        followExr.add("do");
        expression(followExr);
        match("do");
        ArrayList<String> followBody = new ArrayList<String>();
        followBody.add("end");
        body(followBody);
        match("end");
    }

    /*
     * PRINT STATEMENT: 
     */
    public static void printStatement(ArrayList<String> follow) {
        match("print");
        expression(follow);
    }

    /*
     * EXPRESSION: 
     */
    public static void expression(ArrayList<String> follow) {
        ArrayList<String> followSim = new ArrayList<String>();
        followSim.add("<");
        followSim.add("=<");
        followSim.add("=");
        followSim.add("!=");
        followSim.add(">=");
        followSim.add(">");
        for(String f : follow) {
            followSim.add(f);
        }
        simpleExpression(followSim);
        if(tokens.get(index).equals("<") || tokens.get(index).equals("=<") || tokens.get(index).equals("<") || tokens.get(index).equals("!=") || tokens.get(index).equals(">=") || tokens.get(index).equals(">")) {
            next();
            simpleExpression(follow);
        }
        expected(followSim);
    }

    /*
     * SIMPLE EXPRESSION: 
     */
    public static void simpleExpression(ArrayList<String> follow) {
        ArrayList<String> followT = new ArrayList<String>();
        followT.add("+");
        followT.add("-");
        followT.add("or");
        for(String f : follow) {
            followT.add(f);
        }
        term(followT);
        while(tokens.get(index).equals("+") || tokens.get(index).equals("-") || tokens.get(index).equals("or")) {
            next();
            term(follow);
        }
    }

    /*
     * TERM: 
     */
    public static void term(ArrayList<String> follow) {
        ArrayList<String> followF = new ArrayList<String>();
        followF.add("*");
        followF.add("/");
        followF.add("mod");
        followF.add("and");
        for(String f : follow) {
            followF.add(f);
        }
        factor(followF);
        while(tokens.get(index).equals("*") || tokens.get(index).equals("/") || tokens.get(index).equals("mod") || tokens.get(index).equals("and")) {
            next();
            factor(follow);
        }
    }

    /*
     * FACTOR: 
     */
    public static void factor(ArrayList<String> follow) {
        if(tokens.get(index).equals("-") || tokens.get(index).equals("not")) {
            next();
        }
        if(tokens.get(index).equals("false") || tokens.get(index).equals("true") || tokens.get(index).equals("'NUM'")) {
            literal(follow);
            expected(follow);
        } else if(tokens.get(index).equals("'ID'")) {
            next();
        } else if(tokens.get(index).equals("(")) {
            next();
            ArrayList<String> followExpr = new ArrayList<String>();
            followExpr.add(")");
            expression(followExpr);
            match(")");
        } else {
            ArrayList<String> expectedSymbols = new ArrayList<String>();
            expectedSymbols.add("-");
            expectedSymbols.add("not");
            expectedSymbols.add("true");
            expectedSymbols.add("false");
            expected(expectedSymbols);
        }
    }

    /*
     * LITERAL: 
     */
    public static void literal(ArrayList<String> follow) {
        if(tokens.get(index).equals("'NUM'")) {
            next();
        } else {
            booleanLiteral(follow);
        }
    }

    /*
     * BOOLEAN LITERAL: 
     */
    public static void booleanLiteral(ArrayList<String> follow) {
        if(tokens.get(index).equalsIgnoreCase("true") || tokens.get(index).equalsIgnoreCase("true")) {
            next();
        }
    }
    
    public static void main(String[] args) {
        while(true) {
            try {
                String inFile = getUserInput("Enter file path (Or type quit or exit to stop): ");   //get the file from user
                if(inFile.equalsIgnoreCase("quit") || inFile.equalsIgnoreCase("exit")) { //if the user types "quit" or "exit," the program will stop
                    break;
                }
 
                reader = new BufferedReader(new FileReader(inFile));   //reader will read from the input file
                currentLine = 1;                                       //initialize current line and column
                currentColumn = 0;
                List<String> keywords = keywords();                    //loading keywords and symbols.
                HashSet<String> allowedSymbols = symbols();

                /*
                 * LEXICAL ANALYZER: 
                 */
                currentChar = currentChar();                            //set the current char.
                while(!currentChar.equals("end-of-text")) {
                    if(Character.isWhitespace(currentChar.charAt(0))) {
                        whiteSpace();
                    } else if(currentChar.charAt(0) == '/') {
                        comments();
                    } else if(currentChar.charAt(0) == ':') {
                        checkColon();
                    } else if(currentChar.charAt(0) == '=') {
                        checkEqual();
                    } else if(currentChar.charAt(0) == '>') {
                        checkGreaterThan();
                    }
                    /*
                     * Checking for keywords and identifiers 
                     */
                    else if(Character.isLetter(currentChar.charAt(0))) { //if the current char is a letter...
                        StringBuilder identifier = new StringBuilder();  //create string to store the word
                        while(!Character.isWhitespace(currentChar.charAt(0)) && (Character.isLetterOrDigit(currentChar.charAt(0)) || currentChar.equals("_")) && !currentChar.equals("end-of-text")) {
                            identifier.append(currentChar);              //append each letter/number/_ to the string
                            nextChar();
                            currentChar = Character.toString(nextChar);
                            currentColumn++;
                        }
                        String tokenVal = identifier.toString();         //create the token value from the string generated
                        if(keywords.contains(tokenVal)) {                //if the keyword list contains the token...
                            tokenValue = tokenVal;                       //add it to the token value, then print the keyword.
                            tokens.add(tokenValue);
                            System.out.println(position()+ " " + kind() + " " + value());
                        } else {                                         //otherwise, print the token
                            tokenValue = tokenVal;
                            tokens.add(kind());
                            System.out.println(position()+ " " + kind() + " " + value());
                        }
                    } 
                    /*
                     * Checking for numbers
                     */
                    else if(Character.isDigit(currentChar.charAt(0))) {  //if the current char is a number...
                        StringBuilder integer = new StringBuilder();     //create string to store the number(s)
                        while(!Character.isWhitespace(currentChar.charAt(0)) && Character.isDigit(currentChar.charAt(0))) {
                            integer.append(currentChar);                 //append each number to the string
                            currentChar = currentChar();
                            currentColumn++;
                        }
                        String tokenVal = integer.toString();            //create the token value from the string generated
                        tokenValue = tokenVal;                           //set the token value as the token read, then print the number token.
                        tokens.add(kind());
                        System.out.println(position()+ " " + kind() + " " + value());
                    } 
                    /*
                     * Checking for valid symbols
                     */
                    else {                                               
                        StringBuilder symbol = new StringBuilder();     //create string to store symbol
                        symbol.append(currentChar);
                        nextChar();
                        currentChar = Character.toString(nextChar);
                        currentColumn++;

                        String tokenVal = symbol.toString();            //create token value from the string generated
                        if(allowedSymbols.contains(tokenVal)) {         //check to see if the symbol is valid or invalid
                            tokenValue = tokenVal;
                            tokens.add(tokenValue);
                            System.out.println(position()+ " " + kind() + " " + value());
                        } else {                                        //if it is invalid, then print out the symbol and its position and terminate.
                            tokenValue = tokenVal;
                            System.out.println("Invalid Token >>> "+ value() + " " + position());
                            return;
                        }
                    }
                                                            
                }
                /*
                 * Creating "end-of-text" token
                 */
                tokenValue = currentChar;                       //set the "end-of-text" token as the token value
                tokens.add(tokenValue);                         //set the position of the token, then print.
                System.out.println(currentLine + ":" + (currentColumn+1) + " " + kind() + " " + value());
                reader.close();


                /*
                 * Calling Synatx Anaylzer:
                 */
                ArrayList<String> followProgram = new ArrayList<String>();
                followProgram.add("end-of-text");
                program(followProgram);

            } catch(IOException e) {
                System.out.println("File not found.");
                System.out.println("Please try again: ");
            }
        }        
    }

    public static void whiteSpace() {
        /*
        * Skipping white spaces.
        */
        if(currentChar.charAt(0) == '\n') {
            currentLine++;
            currentColumn = 0;
            currentChar = currentChar();
        } else if(Character.isWhitespace(currentChar.charAt(0))) {
            currentChar = currentChar();
            currentColumn++;
        } 
    }

    public static void comments() throws IOException {
        /*
        * Checking for comments >>> //
        */
        if(currentChar.equals("/")) {                  //if the current character is a '/'
            nextChar();                                         //call nextChar()
            if(nextChar == '/') {                               //if the next char is another '/' ...
                while(currentChar.charAt(0) != '\n') {    //then ignore the rest of the line
                    currentChar = currentChar();
                    currentColumn++;
                }
            } else {                                            //otherwise, add the '/' as a symbol.
                tokenValue = currentChar;
                tokens.add(tokenValue);
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = Character.toString(nextChar);
                currentColumn++;
            }
        }
    }

    public static void checkColon() throws IOException {
        /*
        * Checking for colon and colon equals >>> ':='
        */
        if (currentChar.equals(":")) {            //if the current character is a ':'
            nextChar();                                    //call nextChar()
            if(nextChar == '=') {                          //if the next char is '=', then add ':=' as a token
                tokenValue = ":=";
                tokens.add(tokenValue);
                currentColumn+=2;
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = currentChar();
            } else {                                       //otherwise, add ':' as a symbol.
                tokenValue = currentChar;
                tokens.add(tokenValue);
                currentColumn++;
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = Character.toString(nextChar);
            }
        }
    }

    public static void checkEqual() throws IOException {
        /*
        * Checking for equals and less than or equal to >>> '=<'
        */
        if(currentChar.equals("=")) {             //if the current character is a '='
            nextChar();                                    //call nextChar()
            if(nextChar == '<') {                          //if the next char is '<', then add '=<' as a token
                tokenValue = "=<";
                tokens.add(tokenValue);
                currentColumn+=2;
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = currentChar();
            } else {                                       //otherwise, add '=' as a symbol.
                tokenValue = currentChar;
                tokens.add(tokenValue);
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = Character.toString(nextChar);
                currentColumn++;
            }
        }
    }

    public static void checkGreaterThan() throws IOException {
        /*
        * Checking for greater than and greater than or equal to >>> '>='
        */
        if(currentChar.equals(">")) {             //if the current character is a '>'
            nextChar();                                    //call nextChar()
            if(nextChar == '=') {                          //if the next char is '=', then add '>=' as a token
                tokenValue = ">=";
                tokens.add(tokenValue);
                currentColumn+=2;
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = currentChar();
            } else {                                       //otherwise, add '>' as a symbol.
                tokenValue = currentChar;
                tokens.add(tokenValue);
                System.out.println(position()+ " " + kind() + " " + value());
                currentChar = Character.toString(nextChar);
                currentColumn++;
            }
        }
    }

    private static String getUserInput(String message) {
        try {
            System.out.print(message);
            BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));
            return br.readLine();
        } catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
