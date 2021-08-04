public enum Type {
    QUESTION("Question"),
    OPTION("Option"),
    NUMBER("Number"),
    BOOL("Bool"),
    TEXT("Text"),
    LIST("List"),
    BLOCK("Block"),
    NONE("None"),
    BLOCKQUIZ("BlockQuiz"),
    QUIZ("Quiz"),
    FUNCT_RET("Function Return");

    public String name;

    private Type(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
} 