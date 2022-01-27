package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UOE;
import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.etc.Operators.TILDE;

import java.util.Arrays;

/**
 * This class represents Unary Operator for Expression holes.
 */
public class UOE extends Hole {

  public UOE(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(TILDE, STAR, CARET);
  }

  @Override
  public void resetContent() {
    setContent(UOE);
  }
}
