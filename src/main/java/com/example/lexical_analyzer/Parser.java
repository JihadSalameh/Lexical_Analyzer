package com.example.lexical_analyzer;

import java.util.LinkedList;
import java.util.Queue;

//bold and " " terminals
//everything else non-terminal
//when first thing is non-terminal get the first only if there's lambda
//take the follow of non-terminal you're on
public class Parser {

    private Queue<Token> tokenQueue = new LinkedList<>();

    private Token token;

    public Queue<Token> getTokenQueue() {
        return tokenQueue;
    }

    public void setTokenQueue(Queue<Token> tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Parser() {

    }

    //progDecl -> heading declarations block "."
    public void ProgDecl() {
        token = tokenQueue.poll();

        Heading();
        Declarations();
        Block();

        if(token.getX().equals(".")) {
            token = tokenQueue.poll();
        } else {
            ERROR();
        }
    }

    //heading -> program "programName" ";"
    private void Heading() {
        if(token.getX().equals("program")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getType().equals("string")) {
                token = tokenQueue.poll();

                assert token != null;
                if(token.getX().equals(";")) {
                    token = tokenQueue.poll();
                } else {
                    ERROR();
                }
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //block -> begin stmtList end
    private void Block() {
        if(token.getX().equals("begin")) {
            token = tokenQueue.poll();
            stmtList();

            if(token.getX().equals("end")) {
                token = tokenQueue.poll();
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //declarations -> constDecl varDecl
    private void Declarations() {
        constDecl();
        varDeclaration();
    }

    //constDecl -> const constList | LAMBDA
    private void constDecl() {
        if(token.getX().equals("const")) {
            token = tokenQueue.poll();
            constList();
        } else if(!token.getX().equals("var") && !token.getX().equals("begin")) {
            ERROR();
        }
    }

    //constList -> "constName" "=" value ";" constList | LAMBDA
    private void constList() {
        if(token.getType().equals("string")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals("=")) {
                token = tokenQueue.poll();
                Value();

                if(token.getX().equals(";")) {
                    token = tokenQueue.poll();
                    constList();
                } else {
                    ERROR();
                }
            } else {
                ERROR();
            }
        } else if(!token.getX().equals("var") && !token.getX().equals("begin")) {
            ERROR();
        }
    }

    //varDeclaration -> var varList | LAMBDA
    private void varDeclaration() {
        if(token.getX().equals("var")) {
            token = tokenQueue.poll();
            varList();
        } else if(!token.getX().equals("begin")) {
            ERROR();
        }
    }

    //varList -> varItem ";" varList | LAMBDA
    private void varList() {
        if(token.getType().equals("string")) {
            varItem();

            if(token.getX().equals(";")) {
                token = tokenQueue.poll();
                varList();
            } else {
                ERROR();
            }
        } else if(!token.getX().equals("begin")) {
            ERROR();
        }
    }

    //varItem -> nameList ":" dataType
    private void varItem() {
        nameList();

        if(token.getX().equals(":")) {
            token = tokenQueue.poll();
            dataType();
        } else {
            ERROR();
        }
    }

    //nameList -> "varName" moreNames
    private void nameList() {
        if(token.getType().equals("string")) {
            token = tokenQueue.poll();
            moreNames();
        } else {
            ERROR();
        }
    }

    //moreNames -> "," nameList | LAMBDA
    private void moreNames() {
        if(token.getX().equals(",")) {
            token = tokenQueue.poll();
            nameList();
        } else if(!token.getX().equals(")") && !token.getX().equals(":")){
            ERROR();
        }
    }

    //dataType -> integer | real | char
    private void dataType() {
        switch (token.getX()) {
            case "integer", "real", "char" -> token = tokenQueue.poll();
            default -> ERROR();
        }
    }

    //stmtList -> statement ";" stmtList | LAMBDA
    private void stmtList() {
        if(token.getType().equals("string") || token.getX().equals("read") || token.getX().equals("readln") || token.getX().equals("write") || token.getX().equals("writeln")|| token.getX().equals("if")|| token.getX().equals("while")|| token.getX().equals("repeat")|| token.getX().equals("begin")) {
            statement();

            if(token.getX().equals(";")) {
                token = tokenQueue.poll();
                stmtList();
            } else {
                ERROR();
            }
        } else if(!token.getX().equals("end") && !token.getX().equals("until")) {
            ERROR();
        }
    }

    //statement -> assStmt | readStmt | writeStmt | ifStmt | whileStmt | repeatStmt | block
    private void statement() {
        if(token.getType().equals("string")) {
            assStmt();
        } else if(token.getX().equals("if")) {
            ifStmt();
        } else if(token.getX().equals("while")) {
            whileStmt();
        } else if(token.getX().equals("repeat")) {
            repeatStmt();
        } else if(token.getX().equals("begin")) {
            Block();
        } else if(token.getX().equals("read") || token.getX().equals("readln")) {
            readStmt();
        } else if(token.getX().equals("write") || token.getX().equals("writeln")) {
            writeStmt();
        } else {
            ERROR();
        }
    }

    //assStmt -> varName := exp
    private void assStmt() {
        if(token.getX().equals("string")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals(":=")) {
                token = tokenQueue.poll();
                exp();
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //exp -> term expPrime
    private void exp() {
        term();
        expPrime();
    }

    //expPrime -> addOperation term expPrime | LAMBDA
    private void expPrime() {
        if(token.getX().equals("+") || token.getX().equals("-")) {
            addOperation();
            term();
            expPrime();
        } else if(!token.getX().equals(")")) {
            ERROR();
        }
    }

    //term -> factor termPrime
    private void term() {
        factor();
        termPrime();
    }

    //termPrime -> mulOperation factor termPrime | LAMBDA
    private void termPrime() {
        if(token.getX().equals("*") || token.getX().equals("/") || token.getX().equals("mod") || token.getX().equals("div")) {
            mulOperation();
            factor();
            termPrime();
        } else if(!token.getX().equals("+") && !token.getX().equals("-") && !token.getX().equals("else") && !token.getX().equals(";") && !token.getX().equals(")")) {
            ERROR();
        }
    }

    //mulOperation -> * | / | mod | div
    private void mulOperation() {
        switch (token.getX()) {
            case "*", "/", "mod", "div" -> token = tokenQueue.poll();
            default -> ERROR();
        }
    }

    //factor -> "(" exp ")" | nameValue
    private void factor() {
        if(token.getX().equals("(")) {
            token = tokenQueue.poll();
            exp();

            if(token.getX().equals(")")) {
                token = tokenQueue.poll();
            } else {
                ERROR();
            }
        } else if(token.getType().equals("string") || token.getType().equals("integer") || token.getType().equals("real")) {
            nameValue();
        } else {
            ERROR();
        }
    }

    //addOperation -> + | -
    private void addOperation() {
        if(token.getX().equals("+")) {
            token = tokenQueue.poll();
        } else if(token.getX().equals("-")) {
            token = tokenQueue.poll();
        } else {
            ERROR();
        }
    }

    //value -> floatValue | integerValue
    private void Value() {
        if(token.getType().equals("real")) {
            token = tokenQueue.poll();
        } else if(token.getType().equals("integer")) {
            token = tokenQueue.poll();
        } else {
            ERROR();
        }
    }

    //nameValue -> varName | constName | value
    private void nameValue() {
        switch (token.getX()) {
            case "string", "integer", "real" -> token = tokenQueue.poll();
            default -> ERROR();
        }
    }

    //repeatStmt -> repeat stmtList until condition
    private void repeatStmt() {
        if(token.getX().equals("repeat")) {
            token = tokenQueue.poll();
            stmtList();

            if(token.getX().equals("until")) {
                token = tokenQueue.poll();
                condition();
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //whileStmt -> while condition do statement
    private void whileStmt() {
        if(token.getX().equals("while")) {
            token = tokenQueue.poll();
            condition();

            if(token.getX().equals("do")) {
                token = tokenQueue.poll();
                statement();
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //ifStmt -> if condition then statement elsePart
    private void ifStmt() {
        if(token.getX().equals("if")) {
            token = tokenQueue.poll();
            condition();
            
            if(token.getX().equals("then")) {
                token = tokenQueue.poll();
                statement();
                elsePart();
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //elsePart -> else statement | LAMBDA
    private void elsePart() {
        if(token.getX().equals("else")) {
            token = tokenQueue.poll();
            statement();
        } else if(!token.getX().equals(";")) {
            ERROR();
        }
    }

    //condition nameValue relationalOperation nameValue
    private void condition() {
        nameValue();
        relationalOperator();
        nameValue();
    }

    //relational Operators -> "=" | "<>" | "<" | "<=" | ">" | ">="
    private void relationalOperator() {
        switch (token.getX()) {
            case "=", "<>", "<=", "<", ">", ">=" -> token = tokenQueue.poll();
            default -> ERROR();
        }
    }

    //writeStmt -> write "(" nameList ")" | writeln "(" namelist ")"
    private void writeStmt() {
        if(token.getX().equals("write") || token.getX().equals("writeln")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals("(")) {
                token = tokenQueue.poll();
                nameList();
                if(token.getX().equals(")")) {
                    token = tokenQueue.poll();
                } else {
                    ERROR();
                }
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    //readStmt -> read "(" nameList ")" | readln "(" nameList ")"
    private void readStmt() {
        if(token.getX().equals("read") || token.getX().equals("readln")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals("(")) {
                token = tokenQueue.poll();
                nameList();
                if(token.getX().equals(")")) {
                    token = tokenQueue.poll();
                } else {
                    ERROR();
                }
            } else {
                ERROR();
            }
        } else {
            ERROR();
        }
    }

    private void ERROR() {
        System.out.println("ERROR: Expected -> " + token.getX());
        System.exit(0);
    }

}
