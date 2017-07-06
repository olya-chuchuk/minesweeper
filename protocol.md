# Protocol of client-server communication

## Messages from the user to the server
### Formal grammar

    MESSAGE ::= ( LOOK | DIG | FLAG | DEFLAG | HELP_REQ | BYE ) NEWLINE  
    LOOK ::= "look"   
    DIG ::= "dig" SPACE X SPACE Y  
    FLAG ::= "flag" SPACE X SPACE Y  
    DEFLAG ::= "deflag" SPACE X SPACE Y  
    HELP_REQ ::= "help"  
    BYE ::= "bye"  
    NEWLINE ::= "\n" | "\r" "\n"?  
    X ::= INT  
    Y ::= INT  
    SPACE ::= " "  
    INT ::= "-"? [0-9]+  
    
## Messages from the server to the user

### Formal grammar

```
MESSAGE ::= BOARD | BOOM | HELP | HELLO
BOARD ::= LINE+
LINE ::= (SQUARE SPACE)* SQUARE NEWLINE
SQUARE ::= "-" | "F" | COUNT | SPACE
SPACE ::= " "
NEWLINE ::= "\n" | "\r" "\n"?
COUNT ::= [1-8]
BOOM ::= "BOOM!" NEWLINE
HELP ::= [^\r\n]+ NEWLINE
HELLO ::= "Welcome to Minesweeper. Players: " N " including you. Board: "
          X " columns by " Y " rows. Type 'help' for help." NEWLINE
N ::= INT
X ::= INT
Y ::= INT
INT ::= "-"? [0-9]+
```
