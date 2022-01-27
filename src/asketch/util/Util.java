package asketch.util;

import static asketch.etc.Names.BASIC;
import static asketch.etc.Names.DYNAMIC;
import static asketch.etc.Names.MODULO;
import static asketch.etc.Names.NONE;
import static asketch.etc.Names.STATIC;
import static asketch.opts.DefaultOptions.logger;

import asketch.alloy.cand.Relation;
import asketch.opts.ASketchGenOpt;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class provides some utility methods.
 */
public class Util {

  public static void printASketchGenUsage() {
    logger.info(
        "ASketchGen requires: " +
            "model path, " +
            "maximum depth of the ast (>=0), " +
            "maximum arity of expressions (>=1), " +
            "maximum number of operators in each expression (>=0) (optional), " +
            "pruning rule (" + String.join(", ", Arrays.asList(NONE, BASIC, STATIC, DYNAMIC, MODULO)) + ")," +
            "and potentially test suite path and scope if the pruning rule is " + MODULO + "."
    );
  }

  public static boolean isValidPruningRule(String pruningRule) {
    return pruningRule.equals(NONE) || pruningRule.equals(BASIC) || pruningRule.equals(STATIC)
        || pruningRule.equals(DYNAMIC) || pruningRule.equals(MODULO);
  }

  public static void printASketchSolveUsage() {
    logger.info(
        "ASketchSolve requires: " +
            "model name, " +
            "pruning rule, " +
            "and number of tests to use (>=0)."
    );
  }

  public static boolean serialize(Object obj, String fpath) {
    try {
      FileOutputStream fout = new FileOutputStream(fpath);
      ObjectOutputStream oos = new ObjectOutputStream(fout);
      oos.writeObject(obj);
      oos.close();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static Object deserialize(String fpath) {
    try {
      FileInputStream fin = new FileInputStream(fpath);
      ObjectInputStream ois = new ObjectInputStream(fin);
      Object obj = ois.readObject();
      ois.close();
      return obj;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static void printGeneratedExpressionsByDepth(ASketchGenOpt opt,
      Map<Integer, Map<Integer, List<Relation>>> cands) {
    String depthOrCost = opt.getBoundType().name();
    for (Map.Entry<Integer, Map<Integer, List<Relation>>> entry1 : cands.entrySet()) {
      logger.debug(depthOrCost + ": " + entry1.getKey());
      for (Map.Entry<Integer, List<Relation>> entry2 : entry1.getValue().entrySet()) {
        logger.debug("  Arity: " + entry2.getKey());
        entry2.getValue().sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        logger.debug("  Expressions (" + entry2.getValue().size() + "): " + entry2.getValue());
      }
    }
  }
}
