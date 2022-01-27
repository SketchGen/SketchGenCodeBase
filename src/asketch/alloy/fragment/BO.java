package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.BO;
import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;

import java.util.Arrays;

/**
 * This class represents Binary Operator holes
 */
public class BO extends Hole {

  public BO(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(AMP, PLUS, MINUS);
  }

  @Override
  public void resetContent() {
    setContent(BO);
  }
}
