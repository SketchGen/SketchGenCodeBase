package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UOF;
import static asketch.alloy.etc.Operators.EMPTY;
import static asketch.alloy.etc.Operators.NOT;

import java.util.Arrays;

/**
 * This class represents Unary Operator for Formula holes.
 */
public class UOF extends Hole {

  public UOF(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(NOT, EMPTY);
  }

  @Override
  public void resetContent() {
    setContent(UOF);
  }
}
