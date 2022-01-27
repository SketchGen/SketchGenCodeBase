package asketch.opts;

import asketch.etc.BlacklistSigs;
import asketch.util.Logger;

public class DefaultOptions {

  public static Logger logger = new Logger(Logger.INFO);
  public static BlacklistSigs blacklist = new BlacklistSigs();

  // The scope to check equivalent expressions.
  public static int maxDynamicPruningScope = 3;
  // The scope to solve concrete values for holes.
  public static int solvingScope = 3;

  // The target arity for dynamic pruning.  0 means check
  // equivalence of expressions of all arity in every step.
  public static int checkArity = -1;
  // The arity of expressions to use for solving.
  public static int solveArity = 1;
}
