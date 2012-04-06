package chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import chess.codec.FENEncoderSpec;
import chess.format.AlgebraicMoveFormatterSpec;
import chess.format.LongAlgebraicMoveFormatterSpec;
import chess.format.ICCFNumericMoveFormatterSpec;


@RunWith(Suite.class)
@SuiteClasses({
	AlgebraicMoveFormatterSpec.class,
	BoardSpec.class,
	CastlingMetadataSpec.class,
	CastlingRightsSpec.class,
	DynamicMoveGeneratorSpec.class,
	FENEncoderSpec.class,
	GamePositionSpec.class,
	GameSpec.class,
	ICCFNumericMoveFormatterSpec.class,
	LongAlgebraicMoveFormatterSpec.class,
	PieceSquareNotifierSpec.class,
	PositionSpec.class,
	SquareAttackFinderSpec.class,
	StaticMoveGeneratorSpec.class
})
public class AllTests { 

}
