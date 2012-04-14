package chess;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import chess.codec.FENSerializerSpec;
import chess.codec.FENParserSpec;
import chess.format.AlgebraicMoveFormatterSpec;
import chess.format.LongAlgebraicMoveFormatterSpec;
import chess.format.ICCFNumericMoveFormatterSpec;
import chess.search.SearchSpec;


@RunWith(Suite.class)
@SuiteClasses({
	AlgebraicMoveFormatterSpec.class,
	BoardSpec.class,
	CastlingMetadataSpec.class,
	CastlingRightsSpec.class,
	DynamicMoveGeneratorSpec.class,
	FENSerializerSpec.class,
	FENParserSpec.class,
	GamePositionSpec.class,
	GameSpec.class,
	ICCFNumericMoveFormatterSpec.class,
	LongAlgebraicMoveFormatterSpec.class,
	PieceSquareNotifierSpec.class,
	PositionSpec.class,
	SquareAttackFinderSpec.class,
	StaticMoveGeneratorSpec.class,
	SearchSpec.class
})
public class AllTests { 

}
