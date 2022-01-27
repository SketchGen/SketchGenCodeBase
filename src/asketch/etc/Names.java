package asketch.etc;

import java.nio.file.Paths;

public class Names {

  // Suffix
  public final static String DOT_ALS = ".als";
  // Serialized file suffix
  public final static String DOT_PKL = ".pkl";

  // Directory paths
  public final static String PROJECT_DIR_PATH = System.getProperty("user.dir");
  public final static String SKETCH_DIR_PATH = Paths.get(PROJECT_DIR_PATH, "sketch").toString();
  public final static String MODEL_DIR_PATH = Paths.get(SKETCH_DIR_PATH, "models").toString();
  public final static String TEST_DIR_PATH = Paths.get(SKETCH_DIR_PATH, "tests").toString();
  public final static String HIDDEN_DIR_PATH = Paths.get(SKETCH_DIR_PATH, ".hidden").toString();
  public final static String EQUIV_DIR_PATH = Paths.get(HIDDEN_DIR_PATH, "equiv").toString();
  public final static String EXPR_DIR_PATH = Paths.get(HIDDEN_DIR_PATH, "exprs").toString();

  // File paths
  public final static String CHECK_FILE_PATH = Paths.get(HIDDEN_DIR_PATH, "attempt" + DOT_ALS)
      .toString();
  public final static String TEMP_TEST_PATH = Paths.get(HIDDEN_DIR_PATH, "temp" + DOT_ALS)
      .toString();
  public final static String SOLVE_FILE_PATH = Paths.get(HIDDEN_DIR_PATH, "solve" + DOT_ALS)
      .toString();

  // Pruning rules
  // NONE means no pruning technique.
  public final static String NONE = "none";
  // BASIC means only prune based on commutativity and associativity.
  public final static String BASIC = "basic";
  // STATIC means prune using static rules.
  public final static String STATIC = "static";
  // DYNAMIC means prune dynamically using SAT solver.
  public final static String DYNAMIC = "dynamic";
  // MODULO means prune dynamically based on test cases.
  public final static String MODULO = "modulo";
  // Pruning levels.
  public final static int NO_PRUNING = 1;
  public final static int BASIC_PRUNING = 2;
  public final static int STATIC_PRUNING = 3;
  public final static int DYNAMIC_PRUNING = 4;
  public final static int MODULO_PRUNING = 5;

  // Special characters
  public final static String SLASH = "/";
  public final static String NEW_LINE = "\n";
  public final static String COMMA = ",";
  public final static String DOT = ".";
  public final static String SPACE = "\\s";
  public final static String UNDERSCORE = "_";
  public final static String VERTICAL_BAR = "|";
}
