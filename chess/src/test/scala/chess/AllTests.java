package chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import chess.codec.FENEncoderSpec;

@RunWith(Suite.class)
@SuiteClasses({
	BoardSpec.class,
	CastlingRightsSpec.class,
	CastlingMetadataSpec.class,
	DynamicMoveGeneratorSpec.class,
	FENEncoderSpec.class,
	GameSpec.class,
	PieceSquareNotifierSpec.class,
	PositionSpec.class,
	GamePositionSpec.class,
	SquareAttackFinderSpec.class,
	StaticMoveGeneratorSpec.class
})
public class AllTests { 

}
