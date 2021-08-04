
import java.util.*;

public class StaticValues {

    public static Map<String, Type> semanticVars = new HashMap<>();
    public static List<String> semanticErrors = new ArrayList<>();
    public static Map<String, String> VarToVar = new HashMap<>();
    public static Map<String, String> VarToVarTemp = new HashMap<>();
    public static String nomeFuncao;
    public static Type valRet;
    public static Type funcType;
    public static Map<String,Integer> numOfCalls = new HashMap<>();
    public static Map<String,ArrayList<Type>> argTypes = new HashMap<String,ArrayList<Type>>();
    public static Map<String,Type> listTypes = new HashMap<>();

    // encher este map com cenas 
    public static Map<String, Question> bankQuestions = new HashMap<>();

    // maximum number of characters of an option description
    private final static int OPTION_MAX_CHARS = 200;
    private final static int QUESTION_MATCHING_GAP = 20;


    public static int OPTION_MAX_CHARS() {
        return OPTION_MAX_CHARS;
    }

    public static int QUESTION_MATCHING_GAP() {
        return QUESTION_MATCHING_GAP;
    }
}