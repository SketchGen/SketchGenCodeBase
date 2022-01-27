package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.SQ;
import static asketch.alloy.etc.Operators.EMPTY;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.Arrays;

/**
 * This class represents Signature Quantifier holes.
 */
public class SQ extends Hole {

  public SQ(String content, int lineNumber, int begin, int end) {
    super(content, lineNumber, begin, end);
    this.cands = Arrays.asList(LONE, ONE, SOME, EMPTY);
  }

  @Override
  public void resetContent() {
    setContent(SQ);
  }
}
