package com.example.lexical_analyzer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class HelloApplication extends Application {
    String[] temp = new String[100]; //to split the output we got when reading from the symbols file
    static char[] ch = null; //a char array to store the code file we read.
    static ArrayList<Token> reserved_symbols = new ArrayList<>(); //to store the symbols with an id to each one
    static ArrayList<String> code = new ArrayList<>(); //store the code separated
    //static ArrayList<Token> tokens = new ArrayList<>(); //tokens after checking the code and assigning IDs

    /*@FXML
    private TextArea answer;*/

    @FXML
    void CompileText(ActionEvent event) throws FileNotFoundException {
        readSymbols();
        readCode();

        //loops the code
        /*for(int i = 0; i < code.size(); i++) {
            //to check if its already in the reserved_symbols or not
            boolean flag = false;

            //loops all the tokens in the reserved_symbols arraylist
            for(int j = 0; j < reserved_symbols.size(); j++) {
                if(code.get(i).compareTo(reserved_symbols.get(j).getX()) == 0) {
                    tokens.add(reserved_symbols.get(j));
                    flag = true;
                }
            }

            //if first character is letter and its not a reserved symbol it's considered a string
            if(Character.isLetter(code.get(i).charAt(0)) && flag == false) {
                tokens.add(new Token(reserved_symbols.get(50).getId(), code.get(i), reserved_symbols.get(51).getType())); //String value
            } else if(Character.isDigit(code.get(i).charAt(0)) || code.get(i).charAt(0) =='-' || code.get(i).charAt(0) == '+' && flag == false) {
                //to check if the number is a float or integer
                boolean isfloat = false;

                //if it contains a . then isFloat = true
                for(int j = 0; j < code.get(i).length(); j++) {
                    if(code.get(i).charAt(j) == '.') {
                        isfloat = true;
                    }
                }

                if(isfloat == true) {
                    tokens.add(new Token(reserved_symbols.get(81).getId(), code.get(i), reserved_symbols.get(82).getType())); //real value
                } else {
                    tokens.add(new Token(reserved_symbols.get(72).getId(), code.get(i), reserved_symbols.get(73).getType())); //integer value
                }
            }
        }

        Parser parser = new Parser();
        Queue<Token> tokenQueue1 = new LinkedList<>();
        for(int i = 0; i < tokens.size(); i++) {
            tokenQueue1.add(tokens.get(i));
        }
        parser.setTokenQueue(tokenQueue1);
        parser.ProgDecl();

        //System.out.println();System.out.println();System.out.println();

        //prints the tokens we took out of the code
        for(int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).toString());
        }

        String ans = "Name --> ID --> Token-Type\n----------------------------------\n";

        //prints the tokens in a table
        for(int i = 0; i < tokens.size(); i++) {
            ans += tokens.get(i).toString() + "\n";
        }

        answer.setText(ans);*/
    }

    private void readCode() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("C:\\Users\\Smart\\Desktop\\IntelliJ\\Lexical_Analyzer\\src\\main\\java\\com\\example\\lexical_analyzer\\code.txt"));
        StringBuilder temp1 = new StringBuilder();

        //stores the whole code file in temp1
        while(scanner.hasNext()) {
            temp1.append(scanner.nextLine()).append(" ");
        }

        //making it an array of characters
        ch = temp1.toString().toCharArray();

        //looping the code one character at a time
        for(int i = 0; i < ch.length; i++) {

            //if space skip
            if(ch[i] == ' ') {
                continue;
            }

            //if digit or if it's a + - with a digit after it
            //to handle numbers
            if(Character.isDigit(ch[i]) || (ch[i] == '-' || ch[i] == '+' && Character.isDigit(ch[i + 1]))) {
                StringBuilder temp2 = new StringBuilder();

                if(isSymbol(ch[i - 1]) || ch[i - 1] == ' ' || isSymbol(ch[i + 1])) {
                    temp2.append(ch[i]);
                    i++;
                }

                for(int j = i; j < ch.length; j++) {
                    if(Character.isDigit(ch[j]) || ch[j] == '.') {
                        temp2.append(ch[j]);
                    } else if(ch[j] == ' ' || isSymbol(ch[j])) {
                        code.add(temp2.toString());
                        i = j;
                        break;
                    } else {
                        break;
                    }
                }
            }

            //to handle strings and letters
            if(Character.isLetter(ch[i]) || ch[i] == '_') {
                StringBuilder temp2 = new StringBuilder("" + ch[i]);

                for(int j = i + 1; j < ch.length; j++) {
                    if(Character.isLetter(ch[j]) || ch[j] == '_' || Character.isDigit(ch[j])) {
                        temp2.append(ch[j]);
                    } else if(ch[j] == ' ' || isSymbol(ch[j])) {
                        code.add(temp2.toString());
                        i = j;
                        break;
                    }
                }
            }

            //to handle symbols
            if(isSymbol(ch[i])) {
                String temp2 = ch[i] + "";

                //to check if the symbol is more than one character
                if(i + 1 < ch.length) {
                    if(isSymbol(ch[i+1]) && ch[i+1] == '=' || isSymbol(ch[i+1]) && ch[i+1] == '>' || isSymbol(ch[i+1]) && ch[i+1] == '.') {
                        temp2 += ch[i+1];
                        i++;
                    }
                }

                code.add(temp2);
            }
        }

        //prints the code we just read from the file and stored
        for (String s : code) {
            System.out.println(s);
        }

        scanner.close();
    }

    public static boolean isSymbol(char x) {
        for(int i = 0; i < 21; i++) {
            char temp = reserved_symbols.get(i).getX().charAt(0);

            if(x == temp) {
                return true;
            }
        }

        return false;
    }

    public void readSymbols() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("C:\\Users\\Smart\\Desktop\\IntelliJ\\Lexical_Analyzer\\src\\main\\java\\com\\example\\lexical_analyzer\\tokens.txt"));

        String line = scanner.nextLine();
        temp = line.split(" "); //split string according to spaces
        int id = 1;

        for (String s : temp) {
            reserved_symbols.add(new Token(id, s, s)); //creating the tokens and adding them
            id++;
        }

        //prints the tokens that we read from the file
        for (Token reserved_symbol : reserved_symbols) {
            System.out.println(reserved_symbol.toString());
        }

        scanner.close();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Lexical Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}