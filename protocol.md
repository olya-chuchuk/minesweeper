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
