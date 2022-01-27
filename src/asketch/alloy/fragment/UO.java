package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UO;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.NO;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.Arrays;

/**
 * This class represents Unary Operator holes
 */
public class UO extends Hole {

  public UO(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(NO, SOME, LONE, ONE);
  }

  @Override
  public void resetContent() {
    setContent(UO);
  }
}
