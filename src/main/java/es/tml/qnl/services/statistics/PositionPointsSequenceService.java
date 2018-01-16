package es.tml.qnl.services.statistics;

import es.tml.qnl.beans.statistics.ClassPosResSeqRequest;

public interface PositionPointsSequenceService {

	void calculatePosDiffSeq(ClassPosResSeqRequest request);

}
