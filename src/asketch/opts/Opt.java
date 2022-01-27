package asketch.opts;

import static asketch.etc.Names.BASIC;
import static asketch.etc.Names.BASIC_PRUNING;
import static asketch.etc.Names.DOT;
import static asketch.etc.Names.DYNAMIC;
import static asketch.etc.Names.DYNAMIC_PRUNING;
import static asketch.etc.Names.MODULO;
import static asketch.etc.Names.MODULO_PRUNING;
import static asketch.etc.Names.NONE;
import static asketch.etc.Names.NO_PRUNING;
import static asketch.etc.Names.SLASH;
import static asketch.etc.Names.STATIC;
import static asketch.etc.Names.STATIC_PRUNING;
import static asketch.util.StringUtil.afterSubstring;
import static asketch.util.StringUtil.beforeSubstring;

public abstract class Opt {

  protected String modelName;
  protected String pruningRuleName;
  protected int pruningRule;

  public Opt(String modelPath, String pruningRule) {
    this.modelName = beforeSubstring(afterSubstring(modelPath, SLASH, true), DOT, false);
    this.pruningRuleName = pruningRule;
    this.pruningRule = findPruningRule(pruningRule);
  }

  public String getModelName() {
    return modelName;
  }

  public String getPruningRuleName() {
    return pruningRuleName;
  }

  public int getPruningRule() {
    return pruningRule;
  }

  public int findPruningRule(String pruningRule) {
    switch (pruningRule) {
      case NONE:
        return NO_PRUNING;
      case BASIC:
        return BASIC_PRUNING;
      case STATIC:
        return STATIC_PRUNING;
      case DYNAMIC:
        return DYNAMIC_PRUNING;
      case MODULO:
        return MODULO_PRUNING;
      default:
        return NO_PRUNING;
    }
  }
}
